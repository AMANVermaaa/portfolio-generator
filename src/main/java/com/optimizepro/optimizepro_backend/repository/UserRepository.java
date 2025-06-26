package com.optimizepro.optimizepro_backend.repository;



import com.optimizepro.optimizepro_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}

