package com.optimizepro.optimizepro_backend.repository;

import com.optimizepro.optimizepro_backend.model.Resume;
import com.optimizepro.optimizepro_backend.model.ResumeRawText;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRawTextRepository extends MongoRepository<ResumeRawText, String> {

}
