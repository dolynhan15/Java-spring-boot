package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ReferralGift;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReferralGiftService {
    List<Long> getAssessmentIds();

    List<ReferralGift> save(List<ReferralGift> assessmentGifts);

    Page<ReferralGift> getReferralGiftsExceptIds(int page, int size, Long[] claimedGiftIds);

    Page<ReferralGift> getReferralGifts(int page, int size);

    boolean exists(Long[] ids);

    List<ReferralGift> findByIds(Long[] ids);
}