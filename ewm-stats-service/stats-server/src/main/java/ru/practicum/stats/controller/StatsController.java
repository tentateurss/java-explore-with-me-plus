package ru.practicum.stats.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class StatsController {

    // заглушка для POST /hit
    @PostMapping("/hit")
    public ResponseEntity<Void> saveHit() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // заглушка для GET /stats
    @GetMapping("/stats")
    public ResponseEntity<List<Object>> getStats() {
        return ResponseEntity.ok(Collections.emptyList());
    }
}