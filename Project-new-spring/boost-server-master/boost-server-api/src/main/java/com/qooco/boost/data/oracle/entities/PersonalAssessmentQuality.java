package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 11:15 AM
*/
@Setter @Getter
@Entity
@Table(name = "ASSESSMENT_QUALITY_TYPE")
public class PersonalAssessmentQuality extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASSESSMENT_QUALITY_TYPE_SEQUENCE")
    @SequenceGenerator(sequenceName = "ASSESSMENT_QUALITY_TYPE_SEQ", allocationSize = 1, name = "ASSESSMENT_QUALITY_TYPE_SEQUENCE")
    @NotNull
    @Column(name = "ID")
    private Long id;

    @NotNull
    @JoinColumn(name = "ASSESSMENT_PERSONAL_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PersonalAssessment personalAssessment;

    @NotNull
    @Basic(optional = false)
    @Column(name = "NAME_EN_US", columnDefinition = "NVARCHAR2")
    private String nameEnUs;

    @Basic(optional = false)
    @Column(name = "NAME_ZH_CN", columnDefinition = "NVARCHAR2")
    private String nameZhCn;

    @Column(name = "NAME_ZH_TW", columnDefinition = "NVARCHAR2")
    private String nameZhTw;

    @Column(name = "NAME_ID_ID", columnDefinition = "NVARCHAR2")
    private String nameIdId;

    @Column(name = "NAME_JA_JP", columnDefinition = "NVARCHAR2")
    private String nameJaJp;

    @Basic(optional = false)
    @Column(name = "NAME_MS_MY", columnDefinition = "NVARCHAR2")
    private String nameMsMy;

    @Basic(optional = false)
    @Column(name = "NAME_TH_TH", columnDefinition = "NVARCHAR2")
    private String nameThTh;

    @Basic(optional = false)
    @Column(name = "NAME_VI_VN", columnDefinition = "NVARCHAR2")
    private String nameViVn;

    @Basic(optional = false)
    @Column(name = "NAME_KO_KR", columnDefinition = "NVARCHAR2")
    private String nameKoKr;

    @Basic(optional = false)
    @Column(name = "QUALITY_TYPE")
    private int qualityType;

    @Basic(optional = false)
    @Column(name = "DEFAULT_VALUE")
    private int defaultValue;

}
