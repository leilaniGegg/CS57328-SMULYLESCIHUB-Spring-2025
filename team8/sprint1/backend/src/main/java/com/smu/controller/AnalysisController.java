package com.smu.controller;

import com.smu.model.Document;
import com.smu.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalysisController {

    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyze(@RequestParam("file") MultipartFile file) throws IOException {
        String text = new String(file.getBytes());

        // Save to database
        Document doc = new Document();
        doc.setFilename(file.getOriginalFilename());
        doc.setContent(text);
        documentRepository.save(doc);

        // Simple NLP simulation for demo:
        String[] words = text.toLowerCase().split("\\W+");
        Map<String, Integer> freq = new HashMap<>();
        for (String word : words) {
            if (word.length() > 4) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }

        List<String> topKeywords = freq.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        List<Map<String, Object>> topicSummary = new ArrayList<>();
        for (int i = 0; i < topKeywords.size(); i++) {
            Map<String, Object> topic = new HashMap<>();
            topic.put("label", topKeywords.get(i));
            topic.put("score", Math.random() * 10); // Fake score for now
            topicSummary.add(topic);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("keywords", topKeywords);
        response.put("topics", topicSummary);

        return ResponseEntity.ok(response);
    }
}