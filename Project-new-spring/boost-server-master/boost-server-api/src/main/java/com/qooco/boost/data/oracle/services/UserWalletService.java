package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserWallet;

public interface UserWalletService {
    UserWallet findById(Long id);

    Boolean exists(Long id);

    UserWallet save(UserWallet userWallet);
}