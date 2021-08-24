package com.qooco.boost.models.request.authorization;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 6/19/2018 - 10:53 AM
 */
public class SignUpSystem {
    private String email;
    private String username;
    private String password;

    public SignUpSystem() {}

    public SignUpSystem(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
