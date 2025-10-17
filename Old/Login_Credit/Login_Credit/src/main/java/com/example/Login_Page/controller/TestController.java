package com.example.Login_Page.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/secure-data")
    public String getSecureData() {
        return "This is protected data!";
    }
}
