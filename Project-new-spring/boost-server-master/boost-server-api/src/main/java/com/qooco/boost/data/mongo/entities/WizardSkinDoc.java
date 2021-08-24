package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.QuestionEmbedded;
import com.qooco.boost.data.mongo.embedded.TestByValueEmbedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "WizardSkinDoc")
@Setter @Getter @NoArgsConstructor @AllArgsConstructor
public class WizardSkinDoc {
    @Id
    private long id;
    private String textResId;
    private Date timestamp;
    private String mappingId;

    private List<QuestionEmbedded> questions;
    private List<TestByValueEmbedded> testsByValue;
}
