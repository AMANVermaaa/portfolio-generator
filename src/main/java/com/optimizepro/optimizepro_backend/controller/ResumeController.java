package com.optimizepro.optimizepro_backend.controller;

import com.optimizepro.optimizepro_backend.model.Resume;
import com.optimizepro.optimizepro_backend.model.ResumeDetails;
import com.optimizepro.optimizepro_backend.repository.ResumeDetailsRepository;
import com.optimizepro.optimizepro_backend.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ResumeDetailsRepository resumeDetailsRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getResumeDetails(@PathVariable String id) {
        return resumeRepository.findById(id)
                .map(resume -> {
                    ResumeDetails details = resume.getResumeDetails();
                    if(details == null) {
                        return ResponseEntity.notFound().build();
                    }
                    return ResponseEntity.ok(details);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}