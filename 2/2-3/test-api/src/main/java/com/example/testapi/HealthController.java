package com.example.testapi;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

@RestController
public class HealthController {
    @GetMapping("/health")
    public  ResponseEntity<String>  health() {
        return ResponseEntity.ok("hello world\n");
    }
}