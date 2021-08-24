package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.ReferralGift;
import com.qooco.boost.data.oracle.reposistories.ReferralGiftRepository;
import com.qooco.boost.data.oracle.services.ReferralGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferralGiftServiceImpl implements ReferralGiftService {

    @Autowired
    private ReferralGiftRepository referralGiftRepository;

    @Override
    public List<Long> getAssessmentIds() {
        return referralGiftRepository.getAssessmentIds();
    }

    @Override
    public List<ReferralGift> save(List<ReferralGift> assessmentGifts) {
        return Lists.newArrayList(referralGiftRepository.saveAll(assessmentGifts));
    }

    @Override
    public Page<ReferralGift> getReferralGiftsExceptIds(int page, int size, Long[] claimedGiftIds) {
        return referralGiftRepository.findAll(PageRequest.of(page, size), claimedGiftIds);
    }

    @Override
    public Page<ReferralGift> getReferralGifts(int page, int size) {
        return referralGiftRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public boolean exists(Long[] ids) {
        int size = referralGiftRepository.countByIds(ids);
        return size == ids.length;
    }

    @Override
    public List<ReferralGift> findByIds(Long[] ids) {
        return referralGiftRepository.findByIds(ids);
    }
}