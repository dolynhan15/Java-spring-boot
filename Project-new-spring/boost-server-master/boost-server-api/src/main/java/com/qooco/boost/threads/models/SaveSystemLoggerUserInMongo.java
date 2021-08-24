package com.qooco.boost.threads.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

@Setter
@Getter
@NoArgsConstructor
public class SaveSystemLoggerUserInMongo {
    private HttpServletRequest request;
    private UserDetails userDetails;

    public SaveSystemLoggerUserInMongo(HttpServletRequest request, UserDetails userDetails) {
        this.request = request;
        this.userDetails = userDetails;
    }
}
