package com.qooco.boost.threads.services;

import com.qooco.boost.data.enumeration.MessageCenterType;
import com.qooco.boost.data.mongo.embedded.BoostHelperEmbedded;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;

public interface MessageCenterDocActorService {

    MessageCenterDoc saveForSendAppointment(VacancyEmbedded vacancy, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv);

    MessageCenterDoc createForSendAppointment(VacancyEmbedded vacancy, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv);

    MessageCenterDoc updateForSendAppointment(MessageCenterDoc doc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv);

    MessageCenterDoc createForSendApplicant(VacancyEmbedded vacancy, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv);

    MessageCenterDoc updateForSendApplicant(MessageCenterDoc doc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv);

    MessageCenterDoc saveForBoostHelper(BoostHelperEmbedded boostHelperEmbedded, UserProfileCvEmbedded customer);

    MessageCenterDoc saveForBoostHelper(BoostHelperEmbedded boostHelperEmbedded, UserProfileCvEmbedded customer, CompanyEmbedded company, MessageCenterType type);

    int getReceiveAppTypeByMessageCenter(MessageBase message);
}
