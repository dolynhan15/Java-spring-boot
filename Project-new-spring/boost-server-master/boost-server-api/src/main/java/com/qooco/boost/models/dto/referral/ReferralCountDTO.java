package com.qooco.boost.models.dto.referral;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.oracle.entities.UserWallet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferralCountDTO {

    @Getter @Setter
    private int redeemCount;

    private boolean isClaim;

    @Getter() @Setter
    private int giftCount;

    @Getter() @Setter
    private int redeemRequire;

    @Getter() @Setter
    @JsonIgnore
    private int receivedGiftCount;

    public ReferralCountDTO(int redeemRequire, int receivedGiftCount) {
        this.redeemRequire = redeemRequire;
        this.receivedGiftCount = receivedGiftCount;
    }

    public ReferralCountDTO(UserWallet userWallet) {
        if (Objects.nonNull(userWallet)) {
            this.redeemCount = userWallet.getBoostCoins();
        }
    }

    public boolean isClaim() {
        return isClaim;
    }

    @JsonProperty("isClaim")
    public void setClaim(boolean claim) {
        isClaim = claim;
    }
}