package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ReferralCode;

import java.util.Date;

public interface ReferralCodeService {
    ReferralCode save(ReferralCode referralCode);

    ReferralCode findActiveCodeByOwner(Long userId);

    ReferralCode findActiveByOwnerAndCode(Long userId, String code);

    ReferralCode findActiveByNotOwnerAndCode(Long userId, String code);

    ReferralCode findById(Long id);

    Boolean exists(Long id);

    ReferralCode findByCode(String code);

    ReferralCode findSharedCodesByUserAndDuration(Long userId, Date createdDateEvent);
}
