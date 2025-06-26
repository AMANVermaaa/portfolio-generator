package com.optimizepro.optimizepro_backend.repository;

import com.optimizepro.optimizepro_backend.model.ResumeDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeDetailsRepository extends MongoRepository<ResumeDetails, String> {
}
