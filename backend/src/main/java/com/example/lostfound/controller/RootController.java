package com.example.lostfound.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getRootStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Lost & Found Portal Backend REST API");
        status.put("version", "1.0.0");
        status.put("swaggerUi", "/swagger-ui.html");
        status.put("apiDocs", "/api-docs");
        status.put("message", "Backend API is live and operational!");
        return ResponseEntity.ok(status);
    }
}
