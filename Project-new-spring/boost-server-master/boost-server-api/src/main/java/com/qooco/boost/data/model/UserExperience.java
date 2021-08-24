package com.qooco.boost.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class UserExperience {
    private Long id;
    private Long userProfileId;
    private Date maxEndDate;
    private Date minStartDate;
    private Long dateDifference;
    private Date startWorking;
    private Date endWorking;
}
