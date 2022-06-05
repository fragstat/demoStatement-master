package com.example.demo;

import com.example.demo.service.StatementGenerationServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final StatementGenerationServiceImpl service;

    public Controller(StatementGenerationServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    public void generate() {

    }

}
