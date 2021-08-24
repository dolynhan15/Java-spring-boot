package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;

@Document(collection = "ViewProfileDoc")
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
@FieldNameConstants
public class ViewProfileDoc {
    @Id
    private ObjectId id;
    private UserProfileCvEmbedded candidate;
    private UserProfileEmbedded viewer;
    private VacancyEmbedded vacancy;
    private Date createdDate;
    private Date updatedDate;
    private int status;

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
