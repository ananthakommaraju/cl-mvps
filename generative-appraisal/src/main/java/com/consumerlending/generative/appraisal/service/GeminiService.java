package com.consumerlending.generative.appraisal.service;

import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    public String generateSummary(String prompt) {
        return "Generated Summary.";
    }
}