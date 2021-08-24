package com.qooco.boost.threads.services;

import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.sdo.VacancyCandidateSDO;
import org.bson.types.ObjectId;

import java.util.List;

public interface MessageDocActorService {
    int getMessageReceiveInApp(MessageBase message);

    void updateReceiveInApp(MessageBase message);

    List<MessageDoc> saveMessageForAppointmentDetails(List<AppointmentDetailDoc> docs);

    List<MessageDoc> saveMessageForAppointmentDetail(AppointmentDetailDoc doc);

    List<MessageDoc> saveChangedContactPersonMessage(List<AppointmentDetail> appointmentDetails);

    List<MessageDoc> saveChangedContactPersonMessageByConversation(List<ObjectId> conversationIds);

    List<MessageDoc> saveMessageForSuspendedOrInactiveVacancy(VacancyDoc vacancyDoc, int reason, String locale);

    List<MessageDoc> saveMessageForRecruitedVacancyCandidate(VacancyCandidateSDO vacancyCandidate);
}
