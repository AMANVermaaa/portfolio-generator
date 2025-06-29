package com.optimizepro.optimizepro_backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/test")
    public String secureTest() {
        return "Access granted! You are authenticated.";
    }
}
