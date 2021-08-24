package com.qooco.boost.data.oracle.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHARE_CODE")
public class ShareCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHARE_CODE_SEQUENCE")
    @SequenceGenerator(sequenceName = "SHARE_CODE_SEQ", allocationSize = 1, name = "SHARE_CODE_SEQUENCE")
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Long id;

    @JoinColumn(name = "REFERRAL_CODE_ID", referencedColumnName = "REFERRAL_CODE_ID")
    @ManyToOne(optional = false)
    private ReferralCode referralCode;

    public ShareCode(ReferralCode referralCode) {
        super(referralCode.getOwner().getUserProfileId());
        this.referralCode = referralCode;
    }
}