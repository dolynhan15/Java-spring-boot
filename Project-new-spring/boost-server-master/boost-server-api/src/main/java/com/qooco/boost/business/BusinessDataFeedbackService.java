package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;

public interface BusinessDataFeedbackService {

    BaseResp sendDataFeedbackMessage(int feedbackType);
}
