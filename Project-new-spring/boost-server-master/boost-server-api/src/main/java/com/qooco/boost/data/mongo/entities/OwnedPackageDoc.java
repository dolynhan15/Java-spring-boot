package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.AssessmentEmbedded;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.Objects;

@Document(collection = "OwnedPackageDoc")
@Getter @Setter
public class OwnedPackageDoc {
    @Id
    private String id;

    private Long packageId;
    private Long userProfileId;
    private AssessmentEmbedded lesson;
    private Date timestamp;
    private Date expires;
    private Date activationDate;
    private int limitPassCount;
    private int orderByLesson;
    private int orderByTopic;
    private int topicLimit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OwnedPackageDoc that = (OwnedPackageDoc) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}