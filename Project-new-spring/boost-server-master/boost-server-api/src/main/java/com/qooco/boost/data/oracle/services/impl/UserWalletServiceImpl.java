package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.UserWallet;
import com.qooco.boost.data.oracle.reposistories.UserWalletRepository;
import com.qooco.boost.data.oracle.services.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserWalletServiceImpl implements UserWalletService {
    @Autowired
    private UserWalletRepository userWalletRepository;

    @Override
    public UserWallet findById(Long id) {
        return userWalletRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean exists(Long id) {
        return userWalletRepository.existsById(id);
    }

    @Override
    public UserWallet save(UserWallet userWallet) {
        return userWalletRepository.save(userWallet);
    }
}