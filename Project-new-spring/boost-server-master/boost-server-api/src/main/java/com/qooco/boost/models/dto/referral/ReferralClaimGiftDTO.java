package com.qooco.boost.models.dto.referral;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.oracle.entities.ReferralClaimGift;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferralClaimGiftDTO {
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private Long owner;
    @Getter @Setter
    private Long giftId;
    @Getter @Setter
    private Long assessmentId;
    @Getter @Setter
    private Integer spentPoint;
    private boolean isUsed;
    @Getter @Setter
    private Date activeDate;
    @Getter @Setter
    private Date expiredDate;
    @Getter @Setter
    private ReferralGiftDTO gift;

    public ReferralClaimGiftDTO(ReferralClaimGift referralClaimGift, String locale, String domain) {
        if (Objects.nonNull(referralClaimGift)) {
            this.id = referralClaimGift.getId();
            this.owner = referralClaimGift.getOwner();
            this.giftId = referralClaimGift.getGift().getId();
            this.gift = ReferralGiftDTO.init(referralClaimGift.getGift(), locale, domain);
            if (Objects.nonNull(referralClaimGift.getGift().getAssessment())) {
                this.assessmentId = referralClaimGift.getGift().getAssessment().getId();
            }
            this.spentPoint = referralClaimGift.getSpentPoint();
            this.isUsed = referralClaimGift.isUsed();
            this.activeDate = DateUtils.getUtcForOracle(referralClaimGift.getActiveDate());
            this.expiredDate = DateUtils.getUtcForOracle(referralClaimGift.getExpiredDate());
        }
    }

    public boolean isUsed() {
        return isUsed;
    }

    @JsonProperty("isUsed")
    public void setUsed(boolean used) {
        isUsed = used;
    }
}
