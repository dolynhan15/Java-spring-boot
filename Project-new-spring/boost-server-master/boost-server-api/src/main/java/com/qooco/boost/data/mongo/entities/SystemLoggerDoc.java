package com.qooco.boost.data.mongo.entities;

import com.mongodb.DBObject;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Setter
@Getter
@Document(collection = "SystemLoggerDoc")
public class SystemLoggerDoc {
    @Id
    private ObjectId id;

    private String username;
    private String roles;
    private String boostToken;
    private String clientIP;
    private String appId;
    private Long companyId;

    private String url;
    private String host;
    private String origin;
    private String userAgent;
    private String method;

    private DBObject requestData;
    private Date createdDate;

    private DBObject responseData;
    private Date responseDate;

    private String stackTrace;

    public SystemLoggerDoc() {
        this.createdDate = DateUtils.toServerTimeForMongo();
    }
}
