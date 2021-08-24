package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.ReferralRedeem;
import com.qooco.boost.data.oracle.reposistories.ReferralRedeemRepository;
import com.qooco.boost.data.oracle.services.ReferralRedeemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 8/6/2018 - 5:19 PM
 */
@Service
@Transactional
public class ReferralRedeemServiceImpl implements ReferralRedeemService {
    @Autowired
    private ReferralRedeemRepository referralRedeemRepository;

    @Override
    public ReferralRedeem save(ReferralRedeem referralRedeem) {
        return referralRedeemRepository.save(referralRedeem);
    }

    @Override
    public int countByReferralCodeId(Long referralCodeId) {
        return referralRedeemRepository.countByCodeId(referralCodeId);
    }

    @Override
    public List<ReferralRedeem> findByReferralCodeId(Long referralCodeId) {
        return referralRedeemRepository.findByCodeId(referralCodeId);
    }
}
