package com.optimizepro.optimizepro_backend.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.optimizepro.optimizepro_backend.model.Resume;

import java.util.List;
import java.util.Optional;
public interface ResumeRepository extends MongoRepository<Resume, String> {
    Optional<Resume> findByFileName(String fileName);
    Optional<Resume> findByOriginalFileName(String originalFileName);
    List<Resume> findByUserId(String userId);
    Optional<Resume> findByFileHash(String fileHash);

}









