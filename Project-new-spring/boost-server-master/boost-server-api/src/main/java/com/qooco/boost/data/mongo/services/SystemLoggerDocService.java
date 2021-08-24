package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.SystemLoggerDoc;
import com.qooco.boost.models.BaseResp;

import java.util.Date;
import java.util.List;

public interface SystemLoggerDocService {
    SystemLoggerDoc save(SystemLoggerDoc doc);

    void deleteOldLoggerByCreatedDate(Date date);

    SystemLoggerDoc findByUrlAndBoostToken(String url, String boostToken);

    void updateRequestBodyByUrlAndBoostToken(String url, String boostToken, Object body);

    void updateResponseByUrlAndBoostToken(String url, String boostToken, BaseResp response);

    void updateResponseByUrlAndBoostToken(String url, String boostToken, BaseResp response, String stackTrace);

    void updateUserInfoByUrlAndBoostToken(String url, String boostToken, String username, String appId, Long companyId, String roles);

    List<SystemLoggerDoc> findByCreatedDate(Date from, Date to);

}
