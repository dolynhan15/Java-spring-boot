package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 11:16 AM
*/
@Entity
@NoArgsConstructor
@Table(name = "USER_PERSONALITY")
public class UserPersonality extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_PERSONALITY_SEQUENCE")
    @SequenceGenerator(sequenceName = "USER_PERSONALITY_SEQ", allocationSize = 1, name = "USER_PERSONALITY_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Setter @Getter
    @NotNull
    @JoinColumn(name = "PERSONAL_QUESTION_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PersonalAssessmentQuestion personalAssessmentQuestion;

    @Setter @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "ANSWER_VALUE")
    private int answerValue;

    @Setter @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "TIME_TO_ANSWER")
    private int timeToAnswer;

    @Setter @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "ASSESSMENT_PERSONAL_ID")
    private Long personalAssessmentId;

    public UserPersonality(long userId, PersonalAssessmentQuestion question, int answerValue, int timeToAnswer, long personalAssessmentId) {
        super(userId);
        this.personalAssessmentQuestion = new PersonalAssessmentQuestion();
        this.personalAssessmentQuestion = question;
        this.answerValue = answerValue;
        this.timeToAnswer = timeToAnswer;
        this.personalAssessmentId = personalAssessmentId;
    }

    public UserPersonality(Long id) {
        this.id = id;
    }
}
