package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.LevelEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "LevelTestScaleDoc")
@Getter @Setter
@NoArgsConstructor
public class LevelTestScaleDoc {
    @Id
    private String id;
    private String mappingID;
    private List<LevelEmbedded> levels;
    private Date timestamp;

    public LevelTestScaleDoc(List<LevelEmbedded> levels) {
        this.levels = levels;
    }
}
