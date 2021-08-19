package com.example.demouser.controller;

import com.example.demouser.entity.User;
import com.example.demouser.modeldto.UserDto;
import com.example.demouser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam(name = "keyword", required = false, defaultValue = "") String name) {
        List<UserDto> users = userService.searchUser(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("")
    public ResponseEntity<?> getListUser() {
        List<UserDto> users = userService.getListUser();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
//        System.out.println(id);
        UserDto result = userService.getUserId(id);
//        return null;
        return ResponseEntity.ok(result);
    }

    @PostMapping ("/")
    public ResponseEntity<?> createUser() {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser() {
        return null;
    }
}
