package com.optimizepro.optimizepro_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimizepro.optimizepro_backend.model.*;
import com.optimizepro.optimizepro_backend.repository.ResumeDetailsRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

@Service
public class ResumeParserService {


    private String OPENAI_API_KEY;



    @Autowired
    public ResumeParserService(
            @Value("${OPENAI_API_KEY:}") String openaiApiKey // : makes it optional

    ) {
        this.OPENAI_API_KEY = openaiApiKey;

    }
    @Value("${openai.api.endpoint}")
    private String OPENAI_ENDPOINT;

    @Value("${resume.upload.dir}")
    private String UPLOAD_DIR;

    @Autowired
    private ResumeDetailsRepository resumeDetailsRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public String extractTextFromFile(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public String extractTextFromFile(MultipartFile file) throws IOException {
        File tempFile = new File(UPLOAD_DIR + file.getOriginalFilename());
        file.transferTo(tempFile);
        try {
            return extractTextFromFile(tempFile);
        } finally {
            tempFile.delete();
        }
    }

    public ResumeDetails parseResumeWithAI(String rawText, Resume resume) throws Exception {
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is not set");
        }

        String prompt = """
        Extract the following details from the resume and return a JSON object with this structure:
        {
            "personalInfo": {
                "name": "Full Name",
                "title": "Generate a job title that summarizes the individual’s expertise (e.g., 'Full Stack Developer').",
                "email": "email@example.com",
                "phone": "123-456-7890",
                "summary": "Professional summary",
                "linkedIn": "Extract the LinkedIn profile URL if present (even if it's embedded as a hyperlink). Return only the clean URL.",
                "github": "Extract the GitHub profile URL if present (even if it's embedded as a hyperlink). Return only the clean URL.",
                "about": "Brief 50-word About Me paragraph based on resume"
            },
            "skills": [
                {
                    "name": "Skill Name",
                    "category": "Frontend/Backend/etc",
                    "proficiency": 0-100
                }
            ],
            "experience": [
                {
                    "company": "Company Name",
                    "position": "Job Title",
                    "startDate": "YYYY-MM",
                    "endDate": "YYYY-MM or Present",
                    "description": "Job description",
                    "achievements": ["Achievement 1", "Achievement 2"]
                }
            ],
            "education": [
                {
                    "institution": "University Name",
                    "degree": "Degree Name",
                    "fieldOfStudy": "Field of Study",
                    "startDate": "YYYY-MM",
                    "endDate": "YYYY-MM"
                }
            ],
            "projects": [
                {
                    "title": "Project Name",
                    "description": "Project description",
                    "technologies": ["Tech 1", "Tech 2"]
                }
            ]
        }

        Resume Text:
        """ + rawText;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("temperature", 0.2);
        requestBody.put("max_tokens", 2000);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(
                OPENAI_ENDPOINT,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("OpenAI API error: " + response.getBody());
        }

        JsonNode root = mapper.readTree(response.getBody());
        String content = root.path("choices").get(0).path("message").path("content").asText();
        content = content.replaceAll("^``````$", "").trim();

        Map<String, Object> parsedMap = mapper.readValue(content, Map.class);

        return createResumeDetailsFromParsedData(parsedMap, resume);
    }

    public String extractProfileImageFromResume(MultipartFile resumeFile) throws IOException {
        String uploadDir = "uploads/profile_images/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) uploadPath.mkdirs();

        try (PDDocument document = Loader.loadPDF((RandomAccessRead) resumeFile.getInputStream())) {
            PDPageTree pages = document.getPages();

            for (PDPage page : pages) {
                PDResources resources = page.getResources();
                int imageCount = 0;

                for (COSName xObjectName : resources.getXObjectNames()) {
                    if (resources.isImageXObject(xObjectName)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(xObjectName);

                        // Only save the first image
                        if (image != null && imageCount == 0) {
                            BufferedImage bImage = image.getImage();

                            // Save image
                            String fileName = UUID.randomUUID() + "-profile.png";
                            File outputFile = new File(uploadDir + fileName);
                            ImageIO.write(bImage, "png", outputFile);

                            return fileName; // or return full URL if needed
                        }
                        imageCount++;
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Failed to extract image from PDF: " + e.getMessage(), e);
        }

        return null; // No image found
    }

    private ResumeDetails createResumeDetailsFromParsedData(Map<String, Object> parsedData, Resume resume) {
        ResumeDetails details = new ResumeDetails();
        details.setResume(resume);

        Map<String, Object> personalInfo = (Map<String, Object>) parsedData.getOrDefault("personalInfo", new HashMap<>());
        details.setName((String) personalInfo.getOrDefault("name", ""));
        details.setTitle((String) personalInfo.getOrDefault("title", ""));
        details.setEmail((String) personalInfo.getOrDefault("email", ""));

        details.setPhone((String) personalInfo.getOrDefault("phone", ""));
        details.setSummary((String) personalInfo.getOrDefault("summary", ""));
        details.setLinkedIn((String) personalInfo.getOrDefault("linkedIn", ""));
        details.setGithub((String) personalInfo.getOrDefault("github", ""));
        details.setAbout((String) personalInfo.getOrDefault("about", ""));
        List<Map<String, Object>> skills = (List<Map<String, Object>>) parsedData.getOrDefault("skills", new ArrayList<>());
        details.setSkills(skills.stream()
                .map(skill -> {
                    ResumeDetails.Skill s = new ResumeDetails.Skill();
                    s.setName((String) skill.getOrDefault("name", ""));
                    s.setCategory((String) skill.getOrDefault("category", "Other"));
                    Object profObj = skill.get("proficiency");
                    if (profObj instanceof Number) {
                        s.setProficiency(((Number) profObj).intValue());
                    } else {
                        s.setProficiency(50);
                    }
                    s.setIconClass(getIconClassForSkill((String) skill.get("name")));
                    return s;
                }).collect(Collectors.toList()));

        List<Map<String, Object>> experiences = (List<Map<String, Object>>) parsedData.getOrDefault("experience", new ArrayList<>());
        details.setExperiences(experiences.stream()
                .map(exp -> {
                    ResumeDetails.Experience e = new ResumeDetails.Experience();
                    e.setCompany((String) exp.getOrDefault("company", ""));
                    e.setPosition((String) exp.getOrDefault("position", ""));
                    e.setStartDate((String) exp.getOrDefault("startDate", ""));
                    e.setEndDate((String) exp.getOrDefault("endDate", "Present"));
                    e.setDescription((String) exp.getOrDefault("description", ""));
                    e.setAchievements((List<String>) exp.getOrDefault("achievements", new ArrayList<>()));
                    return e;
                }).collect(Collectors.toList()));

        List<Map<String, Object>> educations = (List<Map<String, Object>>) parsedData.getOrDefault("education", new ArrayList<>());
        details.setEducations(educations.stream()
                .map(edu -> {
                    ResumeDetails.Education e = new ResumeDetails.Education();
                    e.setInstitution((String) edu.getOrDefault("institution", ""));
                    e.setDegree((String) edu.getOrDefault("degree", ""));
                    e.setFieldOfStudy((String) edu.getOrDefault("fieldOfStudy", ""));
                    e.setStartDate((String) edu.getOrDefault("startDate", ""));
                    e.setEndDate((String) edu.getOrDefault("endDate", ""));
                    return e;
                }).collect(Collectors.toList()));

        List<Map<String, Object>> projects = (List<Map<String, Object>>) parsedData.getOrDefault("projects", new ArrayList<>());
        details.setProjects(projects.stream()
                .map(proj -> {
                    ResumeDetails.Project p = new ResumeDetails.Project();
                    p.setTitle((String) proj.getOrDefault("title", ""));
                    p.setDescription((String) proj.getOrDefault("description", ""));
                    p.setTechnologies((List<String>) proj.getOrDefault("technologies", new ArrayList<>()));
                    return p;
                }).collect(Collectors.toList()));

        return resumeDetailsRepository.save(details);
    }

    private String getIconClassForSkill(String skillName) {
        if (skillName == null) return "devicon-code-plain";
        String normalized = skillName.toLowerCase();
        return switch (normalized) {
            case "java" -> "devicon-java-plain";
            case "python" -> "devicon-python-plain";
            case "javascript" -> "devicon-javascript-plain";
            case "typescript" -> "devicon-typescript-plain";
            case "angular" -> "devicon-angularjs-plain";
            case "react" -> "devicon-react-plain";
            case "spring" -> "devicon-spring-plain";
            case "node.js" -> "devicon-nodejs-plain";
            case "html" -> "devicon-html5-plain";
            case "css" -> "devicon-css3-plain";
            case "mongodb" -> "devicon-mongodb-plain";
            case "mysql" -> "devicon-mysql-plain";
            case "docker" -> "devicon-docker-plain";
            case "aws" -> "devicon-amazonwebservices-plain";
            default -> "devicon-code-plain";
        };
    }

    public String computeFileHash(MultipartFile file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize SHA-256 MessageDigest", e);
        }

        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
