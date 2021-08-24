package com.qooco.boost.threads.models;

import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class SaveContactPersonInMongo {
    private StaffEmbedded contact;
    private List<Long> vacancyIds;
}
