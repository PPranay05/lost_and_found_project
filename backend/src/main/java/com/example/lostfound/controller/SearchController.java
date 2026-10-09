package com.example.lostfound.controller;

import com.example.lostfound.dto.SearchRequest;
import com.example.lostfound.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Search & Discovery", description = "Centralized multi-criteria search and filtering for items")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    @Operation(summary = "Search lost and found items via POST payload")
    public ResponseEntity<Map<String, Object>> searchItemsPost(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(searchService.searchItems(request));
    }

    @GetMapping("/search")
    @Operation(summary = "Search lost and found items via GET query parameters")
    public ResponseEntity<Map<String, Object>> searchItemsGet(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "ALL") String type) {

        SearchRequest req = new SearchRequest();
        req.setQuery(query);
        req.setCategory(category);
        req.setLocation(location);
        req.setDate(date);
        req.setStatus(status);
        req.setType(type);

        return ResponseEntity.ok(searchService.searchItems(req));
    }
}
