package com.qooco.boost.models.dto.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.AssessmentEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import com.qooco.boost.data.oracle.entities.UserQualification;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/1/2018 - 2:28 PM
 */
@Setter @Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QualificationDTO {
    private Long id;
    private Long assessmentId;
    private String scaleId ;
    private String name;
    private Integer level;
    private String levelName;
    private Integer attempts;
    private Date expiredTime;
    private Date submissionTime;
    private Date serverTime;
    private BaseAssessmentDTO assessment;

    public QualificationDTO() {
    }

    public QualificationDTO(UserQualification qualification, int expiredDay) {
        if (Objects.nonNull(qualification)) {
            this.id = qualification.getId();
            this.level = qualification.getLevelValue();
            this.assessmentId = qualification.getAssessmentId();
            this.scaleId = qualification.getScaleId();
            this.name = qualification.getAssessmentName();
            this.levelName = qualification.getLevelName();
            this.submissionTime = DateUtils.getUtcForOracle(qualification.getSubmissionTime());
            this.expiredTime = DateUtils.addDays(this.submissionTime, expiredDay);
            this.serverTime = new Date();
            if (Objects.nonNull(qualification.getAssessment())) {
                this.assessment = new BaseAssessmentDTO(qualification.getAssessment());
            }
        }
    }

    public QualificationDTO(UserQualification qualification, int expiredDay, int countAssessmentHistory) {
        this(qualification, expiredDay);
        if (Objects.nonNull(qualification)) {
            this.attempts = countAssessmentHistory;
        }
    }

    public QualificationDTO(QualificationEmbedded qualification, int expiredDay) {
        if (Objects.nonNull(qualification)) {
            this.id = qualification.getId();
            AssessmentEmbedded assessmentEmbedded = qualification.getAssessment();
            if (Objects.nonNull(assessmentEmbedded)) {
                this.assessmentId = assessmentEmbedded.getId();
                this.scaleId = assessmentEmbedded.getScaleId();
                this.name = assessmentEmbedded.getName();
            }
            AssessmentLevelEmbedded levelEmbedded = qualification.getLevel();
            if (Objects.nonNull(levelEmbedded)) {
                this.level = levelEmbedded.getAssessmentLevel();
                this.levelName = levelEmbedded.getLevelName();
            }
            this.submissionTime = qualification.getSubmissionTime();
            this.expiredTime = DateUtils.addDays(qualification.getSubmissionTime(), expiredDay);
            this.serverTime = DateUtils.nowUtc();
            this.assessment = new BaseAssessmentDTO(qualification.getAssessment());
        }
    }
}