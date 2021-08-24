package com.qooco.boost.threads.models.messages;

import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.entities.Vacancy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @NoArgsConstructor
public class AppliedVacancyMessage {
    private Vacancy vacancy;
    private UserFit sender;
    private UserProfile recipient;
}
