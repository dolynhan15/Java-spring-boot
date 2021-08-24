package com.qooco.boost.models.response;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.assessment.UserPersonalityDTO;
import lombok.Getter;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 3:38 PM
*/
@Getter
public class SubmitPersonalAssessmentResp extends BaseResp<List<UserPersonalityDTO>> {}
