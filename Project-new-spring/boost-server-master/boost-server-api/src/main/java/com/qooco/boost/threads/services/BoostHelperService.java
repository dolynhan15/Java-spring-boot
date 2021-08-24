package com.qooco.boost.threads.services;

import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.enumeration.BoostHelperEventType;

import java.util.Locale;

public interface BoostHelperService {
    void saveAndSendMessage(MessageDoc doc, String locale);
    MessageDoc createMessageDoc(UserProfileCvMessageEmbedded recipient, BoostHelperEventType eventType, int receiveInApp);
    MessageDoc createMessageDoc(UserProfileCvMessageEmbedded recipient, BoostHelperEventType eventType, int receiveInApp, Locale locale);
}
