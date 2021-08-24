package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ReferralClaimGift;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReferralClaimGiftService {
    ReferralClaimGift save(ReferralClaimGift referralClaimAssessment);

    int countByOwner(Long owner);

    int countInActiveGiftByOwner(Long owner);

    ReferralClaimGift findById(Long id);

    Boolean exists(Long id);

    List<ReferralClaimGift> save(List<ReferralClaimGift> gifts);

    List<Long> findIdsByOwner(Long userProfileId);

    Page<ReferralClaimGift> findByOwner(Long userProfile, int page, int size);
}