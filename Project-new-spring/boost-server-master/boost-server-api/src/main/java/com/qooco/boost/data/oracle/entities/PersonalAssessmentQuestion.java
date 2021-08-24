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
@Table(name = "ASSESSMENT_PERSONAL_QUESTION")
public class PersonalAssessmentQuestion extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASSESSMENT_PERSONAL_QUES_SEQUENCE")
    @SequenceGenerator(sequenceName = "ASSESSMENT_PERSONAL_QUES_SEQ", allocationSize = 1, name = "ASSESSMENT_PERSONAL_QUES_SEQUENCE")
    @NotNull
    @Column(name = "ID")
    private Long id;

    @Setter
    @Getter
    @NotNull
    @JoinColumn(name = "ASSESSMENT_QUALITY_TYPE_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PersonalAssessmentQuality personalAssessmentQuality;

    @Setter
    @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "CONTENT_EN_US", columnDefinition = "NVARCHAR2")
    private String contentEnUs;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "CONTENT_ZH_CN", columnDefinition = "NVARCHAR2")
    private String contentZhCn;

    @Setter
    @Getter
    @Column(name = "CONTENT_ZH_TW", columnDefinition = "NVARCHAR2")
    private String contentZhTw;

    @Setter
    @Getter
    @Column(name = "CONTENT_ID_ID", columnDefinition = "NVARCHAR2")
    private String contentIdId;

    @Setter
    @Getter
    @Column(name = "CONTENT_JA_JP", columnDefinition = "NVARCHAR2")
    private String contentJaJp;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "CONTENT_MS_MY", columnDefinition = "NVARCHAR2")
    private String contentMsMy;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "CONTENT_TH_TH", columnDefinition = "NVARCHAR2")
    private String contentThTh;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "CONTENT_VI_VN", columnDefinition = "NVARCHAR2")
    private String contentViVn;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "CONTENT_KO_KR", columnDefinition = "NVARCHAR2")
    private String contentKoKr;


    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "QUESTION_TYPE")
    private int questionType = 1;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "QUALITY_TYPE")
    private int qualityType = 0;

    @Setter
    @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "MIN_VALUE")
    private int minValue = 0;

    @Setter
    @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "MAX_VALUE")
    private int maxValue = 1;

    @Setter
    @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "IS_REVERSED")
    private boolean isReversed;

    @Setter
    @Getter
    @NotNull
    @Basic(optional = false)
    @Column(name = "VALUE_RATE")
    private int valueRate;

    public PersonalAssessmentQuestion(Long id) {
        this.id = id;
    }
}
