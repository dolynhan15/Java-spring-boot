package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.AuthorizationService;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    @Override
    public UserProfile doLoginSystemAccount (String username, String password){
        return null;
    }
}
