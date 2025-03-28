package com.smu.service;

import com.smu.model.Document;
import com.smu.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class AnalysisService {

    @Autowired
    private DocumentRepository documentRepository;

    public Map<String, Object> analyzeDocument(MultipartFile file) throws IOException {
        String text = new String(file.getBytes());

        Document doc = new Document();
        doc.setFilename(file.getOriginalFilename());
        doc.setContent(text);
        documentRepository.save(doc);

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
        for (String keyword : topKeywords) {
            Map<String, Object> topic = new HashMap<>();
            topic.put("label", keyword);
            topic.put("score", Math.random() * 10);
            topicSummary.add(topic);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("keywords", topKeywords);
        response.put("topics", topicSummary);

        return response;
    }
}