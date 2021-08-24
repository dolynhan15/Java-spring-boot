package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "REFERRAL_REDEEM")
public class ReferralRedeem implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REFERRAL_REDEEM_SEQUENCE")
    @SequenceGenerator(sequenceName = "REFERRAL_REDEEM_SEQ", allocationSize = 1, name = "REFERRAL_REDEEM_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "REFERRAL_REDEEM_ID")
    private Long referralRedeemId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "REFERRAL_CODE_ID")
    private Long codeId;

    @Basic(optional = false)
    @NotNull
    @Column(name = "REDEEMER")
    private Long redeemer;

    @Column(name = "REDEEM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date redeemDate;

    public ReferralRedeem() {
    }

    public ReferralRedeem(Long codeId, Long redeemer) {
        this.codeId = codeId;
        this.redeemer = redeemer;
        this.redeemDate = DateUtils.nowUtcForOracle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferralRedeem that = (ReferralRedeem) o;
        return Objects.equals(referralRedeemId, that.referralRedeemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referralRedeemId);
    }

    @Override
    public String toString() {
        return "OK.ReferralRedeem[ referralRedeemId=" + referralRedeemId + " ]";
    }
    
}
