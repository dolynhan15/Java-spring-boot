package com.qooco.boost.data.mongo.entities.localization.qooco;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/1/2018 - 5:06 PM
 */
@Document(collection = "LocaleThThDoc")
public class ThThDoc {
    @Id
    private String id;
    private String collection;
    private Date timestamp;
    private String content;

    public ThThDoc() {
    }

    public ThThDoc(String id, String key, Date timestamp, String content) {
        this.id = id;
        this.timestamp = timestamp;
        this.collection = key;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


