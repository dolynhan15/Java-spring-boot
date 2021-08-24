package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.dto.assessment.AssessmentDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
public class CareerDTO {
    private UserCurriculumVitaeDTO userCurriculumVitae;
    private int userCoins;
    private int totalViews;
    private long totalUnreadMessage;
    private int totalOpportunities;
    private int totalAppointment;
    private int nextAppointment;
    private List<AssessmentDTO> topAssessments;

    @ApiModelProperty(notes = "The number attribute which user reach after finishing the action")
    private long attribute;
    @ApiModelProperty(notes = "The number attribute of system")
    private long totalAttribute;
}
