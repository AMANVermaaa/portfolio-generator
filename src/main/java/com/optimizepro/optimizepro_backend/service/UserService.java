package com.optimizepro.optimizepro_backend.service;

import com.optimizepro.optimizepro_backend.model.User;
import com.optimizepro.optimizepro_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Signup
    public String registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            return "Email already in use.";
        }

        userRepository.save(user);
        return "User registered successfully!";
    }

    // Login
    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "User not found.";
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            return "Invalid password.";
        }

        return "Login successful.";
    }
}
