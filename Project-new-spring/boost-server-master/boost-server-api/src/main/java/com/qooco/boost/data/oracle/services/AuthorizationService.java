package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserProfile;

public interface AuthorizationService {
    UserProfile doLoginSystemAccount(String username, String password);
}
