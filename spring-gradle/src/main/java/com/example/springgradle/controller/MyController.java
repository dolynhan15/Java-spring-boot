package com.example.springgradle.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping(path = "/welcome")
    public String welcome() {
        return "Hello World";
    }
}
