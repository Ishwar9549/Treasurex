package com.example.demo.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private")
public class PrivateController {

    private static final Logger logger = LoggerFactory.getLogger(PrivateController.class);

    @GetMapping("/test1")
    public String privateFunctionOne(Principal principal) {
        logger.info("Private endpoint One accessed.");
        return "test one Private endpoint accessed."+principal.getName();
    }
    
    @GetMapping("/test2")
    public String privateFunctionTwo() {
        logger.info("Private endpoint two accessed.");
        return "test two Private endpoint accessed.";
    }
    
}
