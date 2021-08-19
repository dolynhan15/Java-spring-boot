package com.example.demouser.service;

import com.example.demouser.entity.User;
import com.example.demouser.modeldto.UserDto;
import com.example.demouser.modeldto.UserMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceImpl implements UserService{
    private static ArrayList<User> users = new ArrayList<User>();
    //add Data Init
    static {
        users.add(new User(1,"Nguyen Mong Mo","mongmo@gmail.com","0123456789","ava1.img","123"));
        users.add(new User(2,"Bui Nhu Lac","buinhulac@gmail.com","0123456789","ava2.img","123"));
        users.add(new User(3,"Phan Thi Long Leo","phanlongleo@gmail.com","0123456789","ava3.img","123"));
    }

    @Override
//    public List<User> getListUser() {
//    return users }
    public List<UserDto> getListUser() {
        List<UserDto> result = new ArrayList<UserDto>();
        for (User user: users){
            result.add(UserMapper.toUserDto(user));
        }
        return result;
    }

    @Override
    public UserDto getUserId(int id) {
        for (User user: users) {
            if (user.getId() == id){
                return UserMapper.toUserDto(user);
            }
        }
        return null;
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<UserDto> result = new ArrayList<>();
        for (User user: users) {
            if (user.getName().contains(keyword)){
                result.add(UserMapper.toUserDto(user));
            }
        }
        return result;
    }
}
