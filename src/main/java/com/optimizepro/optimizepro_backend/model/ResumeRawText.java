package com.optimizepro.optimizepro_backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "resume_raw_text")
public class ResumeRawText {

    @Id
    private String id;
    private String resumeId; // Foreign key reference to Resume
    private String rawText;

    // Constructors
    public ResumeRawText() {}

    public ResumeRawText(String resumeId, String rawText) {
        this.resumeId = resumeId;
        this.rawText = rawText;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    @Override
    public String toString() {
        return "RawText{" +
                "id='" + id + '\'' +
                ", resumeId='" + resumeId + '\'' +
                ", rawText='" + rawText + '\'' +
                '}';
    }
}
