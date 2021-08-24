package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.ServletRequest;

public interface SystemLoggerService {
    void saveSystemLogger(ServletRequest servletRequest);

    void saveSystemLogger(Object body);

    void saveSystemLogger(UserDetails userDetails);

    void saveSystemLogger(BaseResp baseResp, String stackTrace);
}
