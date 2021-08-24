package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 11:15 AM
*/
@Setter @Getter
@NoArgsConstructor
@Entity
@Table(name = "ASSESSMENT_PERSONAL")
public class PersonalAssessment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASSESSMENT_PERSONAL_SEQUENCE")
    @SequenceGenerator(sequenceName = "ASSESSMENT_PERSONAL_SEQ", allocationSize = 1, name = "ASSESSMENT_PERSONAL_SEQUENCE")
    @NotNull
    @Column(name = "ID")
    private Long id;

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
    @Column(name = "FULL_NAME_EN_US", columnDefinition = "NVARCHAR2")
    private String fullNameEnUs;

    @Basic(optional = false)
    @Column(name = "FULL_NAME_ZH_CN", columnDefinition = "NVARCHAR2")
    private String fullNameZhCn;

    @Column(name = "FULL_NAME_ZH_TW", columnDefinition = "NVARCHAR2")
    private String fullNameZhTw;

    @Column(name = "FULL_NAME_ID_ID", columnDefinition = "NVARCHAR2")
    private String fullNameIdId;

    @Column(name = "FULL_NAME_JA_JP", columnDefinition = "NVARCHAR2")
    private String fullNameJaJp;

    @Basic(optional = false)
    @Column(name = "FULL_NAME_MS_MY", columnDefinition = "NVARCHAR2")
    private String fullNameMsMy;

    @Basic(optional = false)
    @Column(name = "FULL_NAME_TH_TH", columnDefinition = "NVARCHAR2")
    private String fullNameThTh;

    @Basic(optional = false)
    @Column(name = "FULL_NAME_VI_VN", columnDefinition = "NVARCHAR2")
    private String fullNameViVn;

    @Basic(optional = false)
    @Column(name = "FULL_NAME_KO_KR", columnDefinition = "NVARCHAR2")
    private String fullNameKoKr;

    @Basic(optional = false)
    @Column(name = "DESCRIPTION_EN_US", columnDefinition = "NVARCHAR2")
    private String descriptionEnUs;

    @Basic(optional = false)
    @Column(name = "DESCRIPTION_ZH_CN", columnDefinition = "NVARCHAR2")
    private String descriptionZhCn;

    @Column(name = "DESCRIPTION_ZH_TW", columnDefinition = "NVARCHAR2")
    private String descriptionZhTw;

    @Column(name = "DESCRIPTION_ID_ID", columnDefinition = "NVARCHAR2")
    private String descriptionIdId;

    @Column(name = "DESCRIPTION_JA_JP", columnDefinition = "NVARCHAR2")
    private String descriptionJaJp;

    @Basic(optional = false)
    @Column(name = "DESCRIPTION_MS_MY", columnDefinition = "NVARCHAR2")
    private String descriptionMsMy;

    @Basic(optional = false)
    @Column(name = "DESCRIPTION_TH_TH", columnDefinition = "NVARCHAR2")
    private String descriptionThTh;

    @Basic(optional = false)
    @Column(name = "DESCRIPTION_VI_VN", columnDefinition = "NVARCHAR2")
    private String descriptionViVn;

    @Basic(optional = false)
    @Column(name = "DESCRIPTION_KO_KR", columnDefinition = "NVARCHAR2")
    private String descriptionKoKr;

    @Basic(optional = false)
    @Column(name = "CODE_NAME", columnDefinition = "NVARCHAR2")
    private String codeName;

    @Basic(optional = false)
    @Column(name = "TYPE_GRAPH")
    private int typeGraph = 1;

    @OneToMany(mappedBy = "personalAssessment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Where(clause = "IS_DELETED = 0")
    private List<PersonalAssessmentQuality> personalAssessmentQualities;

    public PersonalAssessment(Long id) {
        this.id = id;
    }
}
