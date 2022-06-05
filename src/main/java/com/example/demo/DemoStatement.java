package com.example.demo;

import com.example.demo.service.StatementGenerationServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoStatement {

    public static void main(String[] args) {
        SpringApplication.run(DemoStatement.class, args);
        StatementGenerationServiceImpl.generate(DemoStatementApp.Companion.getOperations(), Type.CARD);
        System.exit(112);
    }

}
