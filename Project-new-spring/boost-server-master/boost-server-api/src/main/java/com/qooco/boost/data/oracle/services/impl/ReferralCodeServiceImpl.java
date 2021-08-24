package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.ReferralCode;
import com.qooco.boost.data.oracle.reposistories.ReferralCodeRepository;
import com.qooco.boost.data.oracle.services.ReferralCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ReferralCodeServiceImpl implements ReferralCodeService {
    @Autowired
    private ReferralCodeRepository repository;

    @Override
    public ReferralCode save(ReferralCode referralCode) {
        return repository.save(referralCode);
    }

    @Override
    public ReferralCode findActiveCodeByOwner(Long userId) {
        return repository.findByOwnerAndExpiredStatus(userId, false);
    }

    @Override
    public ReferralCode findActiveByOwnerAndCode(Long userId, String code) {
        return repository.findActiveByOwnerAndCode(userId, code);
    }

    @Override
    public ReferralCode findActiveByNotOwnerAndCode(Long userId, String code) {
        return repository.findActiveByNotOwnerAndCode(userId, code);
    }

    @Override
    public ReferralCode findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public ReferralCode findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public ReferralCode findSharedCodesByUserAndDuration(Long userId, Date createdDateEvent) {
        return repository.findFirstByOwner_UserProfileIdAndCreatedDateBefore(userId, createdDateEvent);
    }
}
