package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.message.BoostHelperMessage;
import com.qooco.boost.data.mongo.embedded.message.MessageStatus;
import com.qooco.boost.models.dto.ButtonDTO;
import com.qooco.boost.models.dto.assessment.AssessmentDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.qooco.boost.enumeration.BoostHelperEventType.BOOST_FEEDBACK_CODES_SHARED;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoostHelperMessageDTO extends MessageStatus {
    private String text;
    private int eventType;
    private int[] actionButton;
    private List<ButtonDTO> buttons;
    private AssessmentDTO assessment;
    private String referralCode;
    private VacancyShortInformationDTO vacancy;

    public BoostHelperMessageDTO(BoostHelperMessage message, String qoocoDomainPath, String locale) {
        this.text = message.getText();
        this.eventType = message.getEventType();
        this.actionButton = message.getActionButtons();
        var buttons = message.getButtons();
        if (BOOST_FEEDBACK_CODES_SHARED.type() == message.getEventType() && isNull(message.getReferralCode())) {
            buttons = new ArrayList<>();
        }
        ofNullable(buttons).ifPresent(it -> this.buttons = it.stream().map(obj -> ButtonDTO.builder().id(obj.getId()).name(obj.getName()).build()).collect(Collectors.toList()));
        ofNullable(message.getAssessment()).ifPresent(it -> this.assessment = new AssessmentDTO(it, qoocoDomainPath));
        this.setStatus(message.getStatus());
        this.referralCode = message.getReferralCode();
        ofNullable(message.getVacancy()).ifPresent(it -> this.vacancy = new VacancyShortInformationDTO(it, locale));
    }
}
