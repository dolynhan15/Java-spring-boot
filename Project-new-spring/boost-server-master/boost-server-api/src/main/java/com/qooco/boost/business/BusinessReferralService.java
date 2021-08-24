package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BusinessReferralService extends BaseBusinessService {
    BaseResp generateCode(Long userProfileId);

    String generateActiveCode(Long userProfileId);

    BaseResp countRedeemCode(Long ownerId);

    BaseResp redeemCode(Long redeemer, String referralCode);

    BaseResp claimFreeAssessment(Long owner, List<Long> assessmentIds);

    BaseResp syncRealAssessmentToReferralGiftTable();

    BaseResp getGifts(int page, int size, Long userProfileId, String locale);

    BaseResp getOwnedGifts(int page, int size, Long userProfileId, String locale);

    BaseResp claimGifts(Long userProfileId, List<Long> giftIds);

    BaseResp getUserCoins(Long id);

    BaseResp trackingShareCode(Authentication authentication, String code);
}