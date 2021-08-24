package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "REFERRAL_CLAIM_GIFT")
public class ReferralClaimGift implements Serializable {
    private static final long serialVersionUID = 1L;


    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REFERRAL_CLAIM_GIFT_SEQUENCE")
    @SequenceGenerator(sequenceName = "REFERRAL_CLAIM_GIFT_SEQ", allocationSize = 1, name = "REFERRAL_CLAIM_GIFT_SEQUENCE")
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "USER_PROFILE_ID")
    private Long owner;

//    @Basic(optional = false)
//    @Column(name = "ASSESSMENT_ID")
//    private Long assessmentId;

    @ManyToOne
    @JoinColumn(name = "REFERRAL_GIFT_ID", referencedColumnName = "ID")
    private ReferralGift gift;

    @Basic(optional = false)
    @NotNull
    @Column(name = "SPENT_POINT")
    private Integer spentPoint;

    @Basic(optional = false)
    @NotNull
    @Column(name = "IS_USED")
    private boolean isUsed;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @Column(name = "ACTIVE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeDate;

    @Column(name = "EXPIRED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredDate;

    public ReferralClaimGift(Long createdBy) {
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
        Date now = DateUtils.nowUtcForOracle();
        this.setCreatedDate(now);
        this.setUpdatedDate(now);
    }

    public ReferralClaimGift(Long owner, Long assessmentId, Integer spentPoint, boolean isUsed) {
        this(owner);
        this.owner = owner;
//        this.assessmentId = assessmentId;
        this.spentPoint = spentPoint;
        this.isUsed = isUsed;
    }

    public ReferralClaimGift(ReferralGift referralGift, Long userProfileId) {
        this(userProfileId);
        owner = userProfileId;
//        assessmentId = referralGift.getAssessmentId();
        gift = referralGift;
        spentPoint = referralGift.getCoin();
        expiredDate = referralGift.getExpiredDate();
        activeDate = referralGift.getActiveDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferralClaimGift that = (ReferralClaimGift) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
}
