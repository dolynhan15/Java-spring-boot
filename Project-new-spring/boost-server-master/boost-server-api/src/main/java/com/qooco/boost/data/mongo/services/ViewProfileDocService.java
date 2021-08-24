package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public interface ViewProfileDocService {
    ViewProfileDoc save(ViewProfileDoc viewProfileDoc);

    ViewProfileDoc findById(ObjectId id);

    List<ViewProfileDoc> save(List<ViewProfileDoc> viewProfileDocs);

    long countViewProfileByCandidateIdAndTimestamp(Long userProfileId, Date startDateOfWeek);

    @Deprecated
    void updateViewerOrCandidate(UserProfileEmbedded userProfileEmbedded);

    @Deprecated
    void updateVacancyEmbedded(VacancyEmbedded vacancyEmbedded);

}
