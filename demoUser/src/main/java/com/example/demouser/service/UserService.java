package com.example.demouser.service;

import com.example.demouser.entity.User;
import com.example.demouser.modeldto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public List<UserDto> getListUser();

    public UserDto getUserId(int id);

    public List<UserDto> searchUser(String keyword);
}
