package com.qooco.boost.threads.models.messages;

import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ChangeContactPersonApplicantMessage {
    private List<Long> vacancyIds;
    private StaffEmbedded oldContactPerson;
    private StaffEmbedded targetContactPerson;
}
