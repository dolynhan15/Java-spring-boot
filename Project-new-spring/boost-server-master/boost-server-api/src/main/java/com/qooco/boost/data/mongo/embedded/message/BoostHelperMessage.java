package com.qooco.boost.data.mongo.embedded.message;

import com.qooco.boost.data.mongo.embedded.AssessmentFullEmbedded;
import com.qooco.boost.data.mongo.embedded.ButtonEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BoostHelperMessage extends MessageStatus {
    private String text;
    private int eventType;
    private int[] actionButtons;
    private List<ButtonEmbedded> buttons;
    private AssessmentFullEmbedded assessment;
    private String referralCode;
    private VacancyEmbedded vacancy;
}
