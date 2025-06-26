package com.optimizepro.optimizepro_backend.controller;

import com.optimizepro.optimizepro_backend.model.Resume;
import com.optimizepro.optimizepro_backend.model.ResumeDetails;
import com.optimizepro.optimizepro_backend.repository.ResumeDetailsRepository;
import com.optimizepro.optimizepro_backend.repository.ResumeRepository;
import com.optimizepro.optimizepro_backend.service.ResumeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:4200")
public class FileUploadController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeParserService resumeParserService;

    @Value("${resume.upload.dir}")
    private String uploadDir;

    @Autowired
    private ResumeDetailsRepository resumeDetailsRepository;

    @PostMapping("/upload-and-parse")
    public ResponseEntity<Map<String, Object>> uploadAndParseResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String userId) {

        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("error", "File is empty");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // STEP 1: Compute file hash
            String fileHash = resumeParserService.computeFileHash(file);

            // STEP 2: Check if a resume with this hash already exists
            Optional<Resume> existingResumeOpt = resumeRepository.findByFileHash(fileHash);
            if (existingResumeOpt.isPresent() && existingResumeOpt.get().getResumeDetails() != null) {
                Resume existing = existingResumeOpt.get();
                ResumeDetails details = existing.getResumeDetails();

                ResumeDetailsDTO dto = new ResumeDetailsDTO(details);
                response.put("parsedData", dto);
                response.put("resumeId", existing.getId());
                response.put("fileName", existing.getFileName());
                response.put("originalFileName", existing.getOriginalFileName());

                response.put("message", "Duplicate resume detected. Returning existing parsed data.");

                return ResponseEntity.ok(response);
            }

            // STEP 3: Save uploaded file
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename);

            file.transferTo(filePath.toFile());

            // STEP 4: Create new Resume object
            Resume resume = new Resume();
            resume.setFileName(uniqueFilename);
            resume.setOriginalFileName(originalFilename);
            resume.setFilePath(filePath.toString());
            resume.setFileType(file.getContentType());
            resume.setFileHash(fileHash);
            if (userId != null) {
                resume.setUserId(userId);
            }



            Resume savedResume = resumeRepository.save(resume);

            // STEP 5: Parse and store details
            String text = resumeParserService.extractTextFromFile(filePath.toFile());
            ResumeDetails resumeDetails = resumeParserService.parseResumeWithAI(text, savedResume);
            savedResume.setResumeDetails(resumeDetails);
            resumeRepository.save(savedResume);

            // STEP 6: Prepare response
            ResumeDetailsDTO dto = new ResumeDetailsDTO(resumeDetails);
            response.put("parsedData", dto);
            response.put("resumeId", savedResume.getId());
            response.put("fileName", uniqueFilename);
            response.put("originalFileName", originalFilename);

            response.put("message", "Resume uploaded, image extracted, and parsed successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Upload and parse failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/resume-details/{id}")
    public ResponseEntity<ResumeDetailsDTO> getResumeDetails(@PathVariable String id) {
        Optional<ResumeDetails> detailsOpt = resumeDetailsRepository.findById(id);

        if (detailsOpt.isPresent()) {
            ResumeDetails details = detailsOpt.get();
            return ResponseEntity.ok(new ResumeDetailsDTO(details));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    public static class ResumeDetailsDTO {

        private String id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private String title;
        private String linkedin;
        private String github;
        private String summary;
        private String about;
        private List<ResumeDetails.Skill> skills;
        private List<ResumeDetails.Education> education;
        private List<ResumeDetails.Experience> experience;
        private List<ResumeDetails.Project> projects;

        public ResumeDetailsDTO(ResumeDetails details) {
            this.id = details.getId();
            this.name = details.getName();
            this.email = details.getEmail();
            this.phone = details.getPhone();
            this.address = details.getAddress();
            this.linkedin = details.getLinkedIn();
            this.title = details.getTitle();
            this.github = details.getGithub();
            this.summary = details.getSummary();
            this.about = details.getAbout();
            this.skills = details.getSkills();
            this.education = details.getEducations();
            this.experience = details.getExperiences();
            this.projects = details.getProjects();
        }

        // Getters only
        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getLinkedin() { return linkedin; }
        public String getGithub() { return github; }
        public String getSummary() { return summary; }
        public String getAbout() { return about; }
        public String getTitle() { return title; }
        public List<ResumeDetails.Skill> getSkills() { return skills; }
        public List<ResumeDetails.Education> getEducation() { return education; }
        public List<ResumeDetails.Experience> getExperience() { return experience; }
        public List<ResumeDetails.Project> getProjects() { return projects; }
    }
}
