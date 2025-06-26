package com.optimizepro.optimizepro_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "resume_details")
public class ResumeDetails {

    @Id
    private String id;

    @DBRef
    private Resume resume;

    // Personal Information
    private String name;
    private String title;
    private String email;
    private String phone;
    private String address;
    private String summary;
    private String avatarUrl;
    private String about;
    // Social Links
    private String linkedIn;
    private String github;
    private String portfolio;

    // Structured Data
    private List<Skill> skills;
    private List<Experience> experiences;
    private List<Education> educations;
    private List<Project> projects;
    private List<Testimonial> testimonials;

    // Nested Classes
    public static class Skill {
        private String name;
        private String category; // e.g., "Frontend", "Backend"
        private Integer proficiency; // 1-100
        private String iconClass;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getProficiency() { return proficiency; }
        public void setProficiency(Integer proficiency) { this.proficiency = proficiency; }
        public String getIconClass() { return iconClass; }
        public void setIconClass(String iconClass) { this.iconClass = iconClass; }
    }

    public static class Experience {
        private String company;
        private String position;
        private String startDate;
        private String endDate;
        private boolean current;
        private String description;
        private List<String> achievements;

        // Getters and Setters
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public boolean isCurrent() { return current; }
        public void setCurrent(boolean current) { this.current = current; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getAchievements() { return achievements; }
        public void setAchievements(List<String> achievements) { this.achievements = achievements; }
    }

    public static class Education {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private String startDate;
        private String endDate;
        private String grade;

        // Getters and Setters
        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getFieldOfStudy() { return fieldOfStudy; }
        public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }
        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
    }

    public static class Project {
        private String title;
        private String description;
        private List<String> technologies;
        private String imageUrl;
        private String projectUrl;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getTechnologies() { return technologies; }
        public void setTechnologies(List<String> technologies) { this.technologies = technologies; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getProjectUrl() { return projectUrl; }
        public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }
    }

    public static class Testimonial {
        private String author;
        private String role;
        private String company;
        private String content;

        // Getters and Setters
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    // Constructors
    public ResumeDetails() {}

    // Getters and Setters for main class
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about =about; }
    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getLinkedIn() { return linkedIn; }
    public void setLinkedIn(String linkedIn) { this.linkedIn = linkedIn; }
    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }
    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
    public List<Experience> getExperiences() { return experiences; }
    public void setExperiences(List<Experience> experiences) { this.experiences = experiences; }
    public List<Education> getEducations() { return educations; }
    public void setEducations(List<Education> educations) { this.educations = educations; }
    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }
    public List<Testimonial> getTestimonials() { return testimonials; }
    public void setTestimonials(List<Testimonial> testimonials) { this.testimonials = testimonials; }

    @Override
    public String toString() {
        return "ResumeDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}