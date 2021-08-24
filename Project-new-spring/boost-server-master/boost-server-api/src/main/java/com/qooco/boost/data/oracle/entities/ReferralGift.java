package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Setter @Getter @NoArgsConstructor
@Entity
@Table(name = "REFERRAL_GIFT")
public class ReferralGift extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REFERRAL_GIFT_SEQUENCE")
    @SequenceGenerator(sequenceName = "REFERRAL_GIFT_SEQ", allocationSize = 1, name = "REFERRAL_GIFT_SEQUENCE")
    @NotNull
    @Column(name = "ID")
    private Long id;

//    @Basic(optional = false)
//    @Column(name = "ASSESSMENT_ID")
//    private Long assessmentId;

    @Basic(optional = false)
    @Column(name = "NAME_EN_US", columnDefinition = "NVARCHAR2")
    private String nameEnUs;

    @Basic(optional = false)
    @Column(name = "NAME_ZH_CN", columnDefinition = "NVARCHAR2")
    private String nameZhCn;

    @Basic(optional = false)
    @Column(name = "NAME_ZH_TW", columnDefinition = "NVARCHAR2")
    private String nameZhTw;

    @Basic(optional = false)
    @Column(name = "NAME_ID_ID", columnDefinition = "NVARCHAR2")
    private String nameIdId;

    @Basic(optional = false)
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
    @Column(name = "IMAGE", columnDefinition = "NVARCHAR2")
    private String image;

    @Basic(optional = false)
    @Column(name = "COIN")
    private int coin = 0;

    @Basic(optional = false)
    @Column(name = "TOTAL")
    private int total = 0;

    @Basic(optional = false)
    @Column(name = "REMAIN")
    private int remain = 0;

    @Column(name = "ACTIVE_DATE")
    private Date activeDate;

    @Column(name = "EXPIRED_DATE")
    private Date expiredDate;

    @JoinColumn(name = "ASSESSMENT_ID", referencedColumnName = "ASSESSMENT_ID")
    @ManyToOne()
    private Assessment assessment;

    public ReferralGift(Assessment assessment) {
        super();
        if (Objects.nonNull(assessment)) {
            this.assessment = assessment;
            nameEnUs = assessment.getName();
            image = assessment.getPicture();
        }
    }
}