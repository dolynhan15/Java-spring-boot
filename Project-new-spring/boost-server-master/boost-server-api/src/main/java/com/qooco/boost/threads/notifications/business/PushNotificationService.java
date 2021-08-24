package com.qooco.boost.threads.notifications.business;

import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.oracle.entities.Company;

import java.util.List;

public interface PushNotificationService {
    void notifyViewCandidateProfile(ViewProfileDoc message, boolean isCounted);

    void notifyApplicantMessage(MessageDoc message, boolean isCounted);

    void notifyCompanyApproval(Company company, boolean isCounted);

    void notifyNewMessage(MessageBase message, boolean isCounted);

    void notifyBoostHelperMessage(MessageDoc message, boolean isCounted);

    void notifySendFileMessage(MessageBase message, boolean isCounted);

    void notifyApplicantResponseMessage(MessageDoc messageDoc, boolean isCounted);

    void notifyAppointmentResponseMessage(MessageDoc message, boolean isCounted);

    void notifyAppointmentMessage(List<MessageDoc> messages, boolean isCounted);

    void notifyJoinRequestCompanyMessage(List<MessageDoc> messages, boolean isCounted);

    void notifyJoinCompanyApproval(MessageDoc message, boolean isCounted);

    void notifyAssignRoleMessage(MessageDoc message, boolean isCounted);

    void notifyVacancyMessage(List<MessageDoc> messages, boolean isCounted);

    void notifyChangedContactPersonMessage(List<MessageDoc> messages, boolean isCounted);
}
