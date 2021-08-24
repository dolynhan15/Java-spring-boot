package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "REFERRAL_CODE")
public class ReferralCode implements Serializable {
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REFERRAL_CODE_SEQUENCE")
    @SequenceGenerator(sequenceName = "REFERRAL_CODE_SEQ", allocationSize = 1, name = "REFERRAL_CODE_SEQUENCE")
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "REFERRAL_CODE_ID")
    private Long referralCodeId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "CODE", columnDefinition = "NVARCHAR2")
    private String code;

    @JoinColumn(name = "USER_PROFILE_ID", referencedColumnName = "USER_PROFILE_ID")
    @ManyToOne(optional = false)
    private UserProfile owner;

    @Basic(optional = false)
	@NotNull
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Basic(optional = false)
    @Column(name = "IS_EXPIRED")
    private boolean isExpired;

    public ReferralCode() {
    }

    public ReferralCode(String code, UserProfile owner, Date createdDate, boolean isExpired) {
        this.code = code;
        this.owner = owner;
        this.createdDate = createdDate;
        this.isExpired = isExpired;
    }

    public ReferralCode(Long id, String code, UserProfile owner, Date createdDate, boolean isExpired) {
        this(code, owner, createdDate, isExpired);
        this.referralCodeId = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (referralCodeId != null ? referralCodeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReferralCode that = (ReferralCode) o;
        return Objects.equals(referralCodeId, that.referralCodeId);
    }

    @Override
    public String toString() {
        return code;
    }
}