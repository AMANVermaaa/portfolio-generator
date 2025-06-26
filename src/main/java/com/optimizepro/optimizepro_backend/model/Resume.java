package com.optimizepro.optimizepro_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "resumes")
public class Resume {

    @Id
    private String id;
    private String fileName;
    private String originalFileName;
    private String filePath;
    private String fileType;
    private String userId;
    private String fileHash; // ✅ New field for hash

    @DBRef
    private ResumeDetails resumeDetails;

    // Constructors
    public Resume() {}

    public Resume(String fileName, String originalFileName, String filePath, String fileType, String fileHash) {
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileHash = fileHash;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public ResumeDetails getResumeDetails() {
        return resumeDetails;
    }

    public void setResumeDetails(ResumeDetails resumeDetails) {
        this.resumeDetails = resumeDetails;
    }


}
