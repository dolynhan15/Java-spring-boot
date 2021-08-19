package com.example.demouser.entity;
import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String password;
}
