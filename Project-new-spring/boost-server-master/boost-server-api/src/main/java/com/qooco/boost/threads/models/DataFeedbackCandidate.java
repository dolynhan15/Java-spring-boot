package com.qooco.boost.threads.models;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class DataFeedbackCandidate extends DataFeedback{
    private long userProfileId;
    public DataFeedbackCandidate(int feedbackType){
        feedbackType(feedbackType);
    }
}
