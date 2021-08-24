package com.qooco.boost.models.transfer;

import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ViewProfileTransfer {
    private UserProfileCvEmbedded candidate;
    private UserProfileEmbedded viewer;
    private VacancyEmbedded vacancy;
}