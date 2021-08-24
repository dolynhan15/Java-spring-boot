package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.UserWallet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCoinsDTO {
    @Getter @Setter
    private int coins;

    public UserCoinsDTO(UserWallet userWallet) {
        coins = userWallet.getBoostCoins();
    }
}