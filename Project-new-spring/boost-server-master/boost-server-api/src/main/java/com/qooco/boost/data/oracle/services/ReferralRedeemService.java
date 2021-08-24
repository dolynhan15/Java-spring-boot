package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ReferralRedeem;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 8/6/2018 - 5:18 PM
 */
public interface ReferralRedeemService {
    ReferralRedeem save(ReferralRedeem referralRedeem);

    int countByReferralCodeId(Long referralCodeId);

    List<ReferralRedeem> findByReferralCodeId(Long referralCodeId);
}
