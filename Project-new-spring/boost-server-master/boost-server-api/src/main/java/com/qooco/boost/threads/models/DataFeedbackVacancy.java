package com.qooco.boost.threads.models;

import com.qooco.boost.data.mongo.entities.VacancyDoc;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
public class DataFeedbackVacancy extends DataFeedback {
    private VacancyDoc vacancy;
    public DataFeedbackVacancy(int feedbackType) {
        feedbackType(feedbackType);
    }
}
