package com.example.hellospringboot;

import org.springframework.stereotype.Component;

@Component("bikini1")
public class Bikini1 implements Outfit {
    @Override
    public void wear() {
        System.out.println("Dang mac bikini 1");
    }
}
