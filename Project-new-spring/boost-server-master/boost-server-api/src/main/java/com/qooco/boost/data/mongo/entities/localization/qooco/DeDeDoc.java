package com.qooco.boost.data.mongo.entities.localization.qooco;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@Document(collection = "LocaleDeDeDoc")
public class DeDeDoc {
    @Id
    private String id;
    private String collection;
    private Date timestamp;
    private String content;

    public DeDeDoc(String id, String key, Date timestamp, String content) {
        this.id = id;
        this.timestamp = timestamp;
        this.collection = key;
        this.content = content;
    }
}


