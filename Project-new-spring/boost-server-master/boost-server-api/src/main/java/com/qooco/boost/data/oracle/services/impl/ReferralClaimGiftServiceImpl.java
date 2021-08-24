package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.ReferralClaimGift;
import com.qooco.boost.data.oracle.reposistories.ReferralClaimGiftRepository;
import com.qooco.boost.data.oracle.services.ReferralClaimGiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReferralClaimGiftServiceImpl implements ReferralClaimGiftService {
    @Autowired
    private ReferralClaimGiftRepository referralClaimGiftRepository;

    @Override
    public ReferralClaimGift save(ReferralClaimGift referralClaimAssessment) {
        return referralClaimGiftRepository.save(referralClaimAssessment);
    }

    @Override
    public int countByOwner(Long owner) {
        return referralClaimGiftRepository.countByOwner(owner);
    }

    @Override
    public int countInActiveGiftByOwner(Long owner) {
        return referralClaimGiftRepository.countInActiveByOwner(owner);
    }

    @Override
    public ReferralClaimGift findById(Long id) {
        return referralClaimGiftRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean exists(Long id) {
        return referralClaimGiftRepository.existsById(id);
    }

    @Override
    public List<ReferralClaimGift> save(List<ReferralClaimGift> gifts) {
        return Lists.newArrayList(referralClaimGiftRepository.saveAll(gifts));
    }

    @Override
    public List<Long> findIdsByOwner(Long userProfileId) {
        return referralClaimGiftRepository.findIdsByOwner(userProfileId).stream().map(BigDecimal::longValue).collect(Collectors.toList());
    }

    @Override
    public Page<ReferralClaimGift> findByOwner(Long userProfile, int page, int size) {
        if (page < 0 || size <= 0) {
            return referralClaimGiftRepository.findByOwner(userProfile, PageRequest.of(page, Integer.MAX_VALUE));
        }
        return referralClaimGiftRepository.findByOwner(userProfile, PageRequest.of(page, size));
    }
}