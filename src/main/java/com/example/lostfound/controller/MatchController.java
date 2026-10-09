package com.example.lostfound.controller;

import com.example.lostfound.dto.MatchResultDto;
import com.example.lostfound.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items/matches")
@Tag(name = "Item Matching", description = "Smart lost & found item correlation and similarity scoring")
public class MatchController {

    private final MatchingService matchingService;

    @Autowired
    public MatchController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping
    @Operation(summary = "Get all potential lost and found item matches across system")
    public ResponseEntity<List<MatchResultDto>> getAllMatches() {
        return ResponseEntity.ok(matchingService.getAllPotentialMatches());
    }

    @GetMapping("/lost/{id}")
    @Operation(summary = "Find possible found item matches for a specific reported lost item")
    public ResponseEntity<List<MatchResultDto>> getMatchesForLostItem(@PathVariable Long id) {
        return ResponseEntity.ok(matchingService.findMatchesForLostItem(id));
    }

    @GetMapping("/found/{id}")
    @Operation(summary = "Find possible lost item matches for a specific registered found item")
    public ResponseEntity<List<MatchResultDto>> getMatchesForFoundItem(@PathVariable Long id) {
        return ResponseEntity.ok(matchingService.findMatchesForFoundItem(id));
    }
}
