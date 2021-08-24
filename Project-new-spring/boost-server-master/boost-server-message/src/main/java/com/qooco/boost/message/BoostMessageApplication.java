package com.qooco.boost.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = {"com.qooco.boost.message"})
public class BoostMessageApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(BoostMessageApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BoostMessageApplication.class);
    }

    @PostConstruct
    public void setSchedule() {

    }
}
