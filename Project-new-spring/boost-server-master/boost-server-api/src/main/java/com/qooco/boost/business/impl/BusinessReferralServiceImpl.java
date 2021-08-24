package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessReferralService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.QoocoService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.UserCoinsDTO;
import com.qooco.boost.models.dto.referral.PagedResultReferralGift;
import com.qooco.boost.models.dto.referral.ReferralClaimGiftDTO;
import com.qooco.boost.models.dto.referral.ReferralCountDTO;
import com.qooco.boost.models.dto.referral.ReferralGiftDTO;
import com.qooco.boost.models.qooco.QoocoResponseBase;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ListUtil;
import com.qooco.boost.utils.RandomString;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.stream.Collectors;

import static com.qooco.boost.constants.AttributeEventType.*;

@Service
public class BusinessReferralServiceImpl implements BusinessReferralService {
    @Autowired
    private ReferralCodeService referralCodeService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private ReferralClaimGiftService referralClaimGiftService;
    @Autowired
    private ReferralRedeemService referralRedeemService;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private ReferralGiftService referralGiftService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;
    @Autowired
    private QoocoService qoocoService;
    @Autowired
    private ShareCodeService shareCodeService;
    @Autowired
    private BusinessValidatorService businessValidatorService;

    @Value(ApplicationConstant.REFERRAL_CODE_EXPIRED_AFTER)
    private int expiredHours;

    @Value(ApplicationConstant.REFERRAL_CODE_LENGTH)
    private int codeLength;

    @Value(ApplicationConstant.REFERRAL_MAX_REDEEM_TIMES)
    private int maxRedeemTimes;

    @Value(ApplicationConstant.CLAIM_ASSESSMENT_POINT_LEVEL)
    private int[] claimAssessmentPoint;

    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";

    private static final int DEFAULT_ASSESSMENT_EXPIRED_DAY = 90;
    private static final int DEFAULT_ASSESSMENT_COIN = 10;
    private static final int DEFAULT_ASSESSMENT_TOTAL = 20;
    private static final int DEFAULT_ASSESSMENT_REMAIN = 20;
    private static final int DEFAULT_RECEIVED_GIFT_COUNT = 0;
    private static final int DEFAULT_GIFT_COUNT = 0;
    private static final int DEFAULT_ASSESSMENT_COUNT = 0;
    private static final boolean IS_CLAIM = true;
    private static final boolean IS_USED = true;

    @Override
    public BaseResp generateCode(Long userProfileId) {
        String codeReturn = generateActiveCode(userProfileId);
        return new BaseResp<>(codeReturn);
    }

    @Override
    public String generateActiveCode(Long userProfileId) {
        var randomCode = new RandomString(codeLength).generateString();
        var codeReturn = randomCode;
        var existCode = referralCodeService.findActiveCodeByOwner(userProfileId);
        if (Objects.isNull(existCode)) {
            this.createNewReferralCode(randomCode, userProfileId);
        } else if (this.isExpiredReferralCode(existCode.getCreatedDate())) {
            this.setExpiredReferralCode(existCode);
            this.createNewReferralCode(randomCode, userProfileId);
        } else {
            codeReturn = existCode.getCode();
        }
        return codeReturn;
    }

    @Override
    public BaseResp countRedeemCode(Long owner) {
        ReferralCountDTO referralCountDTOList = getReferralCountAndIsClaim(owner);
        return new BaseResp<>(referralCountDTOList);
    }

    @Override
    public BaseResp redeemCode(Long redeemer, String referralCode) {
        Optional.ofNullable(referralCode).filter(StringUtils::isNotBlank).orElseThrow(() -> new InvalidParamException(ResponseStatus.INVALID_REDEEM_CODE));

        ReferralCode existCode = referralCodeService.findByCode(referralCode);

        Optional.ofNullable(existCode).map(Optional::of)
                .orElseThrow(() -> new EntityNotFoundException(ResponseStatus.NOT_FOUND_REDEEM_CODE))
                .filter(it -> !it.getOwner().getUserProfileId().equals(redeemer)).map(Optional::of)
                .orElseThrow(() -> new InvalidParamException(ResponseStatus.CAN_NOT_REDEEM_YOUR_OWN_CODE))
                .filter(it -> !it.isExpired()).map(Optional::of)
                .orElseThrow(() -> new InvalidParamException(ResponseStatus.CODE_IS_EXPIRED));

        List<ReferralRedeem> totalRedeemTimes = referralRedeemService.findByReferralCodeId(existCode.getReferralCodeId());

        if (this.isExpiredReferralCode(existCode.getCreatedDate())) {
            this.setExpiredReferralCode(existCode);
            throw new InvalidParamException(ResponseStatus.CODE_IS_EXPIRED);
        } else if (totalRedeemTimes.size() >= maxRedeemTimes) {
            this.setExpiredReferralCode(existCode);
            throw new InvalidParamException(ResponseStatus.CODE_IS_USED);
        }

        boolean isUsed = totalRedeemTimes.stream().anyMatch(ref -> ref.getRedeemer().equals(redeemer));
        if (isUsed) {
            throw new InvalidParamException(ResponseStatus.CODE_IS_USED);
        }

        ReferralRedeem referralRedeem = referralRedeemService.save(new ReferralRedeem(existCode.getReferralCodeId(), redeemer));
        if (Objects.nonNull(referralRedeem)) {
            increaseRedeemCoin(existCode.getOwner().getUserProfileId());
            increaseRedeemCoin(redeemer);
            if (totalRedeemTimes.size() == maxRedeemTimes - 1) {
                this.setExpiredReferralCode(existCode);
            }
            setAttributeAction(existCode.getOwner().getUserProfileId(), redeemer);
            return new BaseResp<>(true);
        }

        return new BaseResp<>(false);
    }

    private void setAttributeAction(Long ownerCode, Long redeemer) {
        businessProfileAttributeEventService.onAttributeEvent(EVT_SHARED_CODE_ACTIVATED, ownerCode);
        businessProfileAttributeEventService.onAttributeEvent(EVT_REDEEMED_GITF_CODE, redeemer);
    }

    private void increaseRedeemCoin(Long receiver) {
        UserWallet userWallet = userWalletService.findById(receiver);
        if (Objects.isNull(userWallet)) {
            userWallet = new UserWallet(receiver);
        }
        userWallet.setBoostCoins(userWallet.getBoostCoins() + 1);
        userWallet.setUpdatedDate(DateUtils.nowUtcForOracle());
        userWalletService.save(userWallet);
    }

    private ReferralCountDTO getReferralCountAndIsClaim(Long ownerId) {
        if (Objects.isNull(ownerId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        if (claimAssessmentPoint.length == 0) {
            claimAssessmentPoint = new int[]{0};
        }

        UserWallet userWallet = userWalletService.findById(ownerId);
        if (Objects.isNull(userWallet)) {
            return new ReferralCountDTO(claimAssessmentPoint[0], DEFAULT_RECEIVED_GIFT_COUNT);
        }
        ReferralCountDTO referralCountDTO = new ReferralCountDTO(userWallet);

        int assessmentCount = referralClaimGiftService.countByOwner(ownerId);
        if (assessmentCount < DEFAULT_ASSESSMENT_COUNT) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_CLAIM_ASSESSMENT);
        }
        referralCountDTO.setReceivedGiftCount(assessmentCount);

        int nextGiftPoint = getPointForNextGift(claimAssessmentPoint, assessmentCount);

        if (userWallet.getBoostCoins() < nextGiftPoint) {
            referralCountDTO.setClaim(!IS_CLAIM);
            referralCountDTO.setGiftCount(DEFAULT_GIFT_COUNT);
            referralCountDTO.setRedeemRequire(nextGiftPoint);
            return referralCountDTO;
        }

        int giftCount = 0;
        int point = claimAssessmentPoint[assessmentCount < claimAssessmentPoint.length ? assessmentCount : claimAssessmentPoint.length - 1];
        while (userWallet.getBoostCoins() >= point) {
            giftCount += 1;
            userWallet.setBoostCoins(userWallet.getBoostCoins() - point);
            assessmentCount++;
            point = claimAssessmentPoint[assessmentCount < claimAssessmentPoint.length ? assessmentCount : claimAssessmentPoint.length - 1];
        }
        referralCountDTO.setClaim(IS_CLAIM);
        referralCountDTO.setGiftCount(giftCount);
        referralCountDTO.setRedeemRequire(nextGiftPoint);
        return referralCountDTO;
    }

    @Override
    @Transactional
    public BaseResp claimFreeAssessment(Long owner, List<Long> assessmentIds) {
        if (CollectionUtils.isEmpty(assessmentIds)) {
            throw new InvalidParamException(ResponseStatus.ASSESSMENT_EMPTY);
        }
        Long[] uniqueList = ListUtil.removeDuplicatesLongArray(assessmentIds.toArray(new Long[0]));
        boolean isExisted = assessmentService.exists(uniqueList);
        if (!isExisted) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_ASSESSMENT);
        }

        ReferralCountDTO myRedeem = this.getReferralCountAndIsClaim(owner);
        if (assessmentIds.size() > myRedeem.getGiftCount()) {
            throw new InvalidParamException(ResponseStatus.CAN_NOT_CLAIM);
        }

        //TODO: sync Qooco assessment list to check existed
        List<Long> idIsClaimed = new ArrayList<>();
        try {
            for (Long assId : uniqueList) {
                QoocoResponseBase responseBase = qoocoService.assignAssessment(owner, assId);
                switch (responseBase.getErrorCode()) {
                    case QoocoApiConstants.CLAIM_SUCCESS:
                        idIsClaimed.add(assId);
                        break;
                    case QoocoApiConstants.CLAIM_SERVER_ERROR:
                        throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);
                    case QoocoApiConstants.CLAIM_CHECK_SUM_NOT_MATCH:
                        throw new InvalidParamException(ResponseStatus.CHECK_SUM_NOT_MATCH);
                    case QoocoApiConstants.CLAIM_USER_ID_NOT_EXIST:
                        throw new InvalidParamException(ResponseStatus.USER_PROFILE_ID_NOT_EXIST);
                    case QoocoApiConstants.CLAIM_LESSON_ID_NOT_EXIST:
                        throw new InvalidParamException(ResponseStatus.ASSESSMENT_ID_NOT_EXIST);
                }
            }

            List<ReferralClaimGift> gifts = this.createAssessmentToClaim(owner, myRedeem.getReceivedGiftCount(), idIsClaimed);
            List<ReferralClaimGift> resultList = referralClaimGiftService.save(gifts);
            List<ReferralClaimGiftDTO> respList = resultList.stream().map(r -> new ReferralClaimGiftDTO(r, "", qoocoDomainPath)).collect(Collectors.toList());

            int remained = myRedeem.getRedeemCount();
            for (ReferralClaimGift assessment : gifts) {
                remained -= assessment.getSpentPoint();
            }

            UserWallet foundUserWallet = userWalletService.findById(owner);
            foundUserWallet.setBoostCoins(remained);
            userWalletService.save(foundUserWallet);
            return new BaseResp<>(respList);
        } catch (ResourceAccessException ex) {
            throw new InvalidParamException(ResponseStatus.QOOCO_SERVER_ERROR);
        }

    }

    @Override
    public BaseResp syncRealAssessmentToReferralGiftTable() {
        List<Long> foundAssessmentIds = referralGiftService.getAssessmentIds();
        List<Assessment> assessments;
        if (CollectionUtils.isNotEmpty(foundAssessmentIds)) {
            assessments = assessmentService.getAssessmentsExceptIds(foundAssessmentIds);
        } else {
            assessments = assessmentService.getAssessments();
        }
        if (CollectionUtils.isEmpty(assessments)) {
            return new BaseResp();
        }
        List<ReferralGift> assessmentGifts = new ArrayList<>();
        assessments.forEach(a -> {
            ReferralGift referralGift = new ReferralGift(a);
            referralGift.setCoin(DEFAULT_ASSESSMENT_COIN);
            referralGift.setTotal(DEFAULT_ASSESSMENT_TOTAL);
            referralGift.setRemain(DEFAULT_ASSESSMENT_REMAIN);
            referralGift.setActiveDate(DateUtils.nowUtcForOracle());
            referralGift.setExpiredDate(DateUtils.addDays(DateUtils.nowUtcForOracle(), DEFAULT_ASSESSMENT_EXPIRED_DAY));
            assessmentGifts.add(referralGift);
        });
        List<ReferralGift> referralGifts = referralGiftService.save(assessmentGifts);
        List<ReferralGiftDTO> giftDTOS = referralGifts.stream().map(
                g -> ReferralGiftDTO.init(g, QoocoApiConstants.LOCALE_EN_US, ""))
                .collect(Collectors.toList());
        return new BaseResp<>(giftDTOS);
    }


    @Override
    public BaseResp getGifts(int page, int size, Long userProfileId, String locale) {
        boolean isExisted = userProfileService.isExist(userProfileId);
        if (!isExisted) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }

        Page<ReferralGift> referralGiftPage;
        List<Long> claimedGiftIds = referralClaimGiftService.findIdsByOwner(userProfileId);
        if (CollectionUtils.isNotEmpty(claimedGiftIds)) {
            Long[] uniqueList = ListUtil.removeDuplicatesLongArray(claimedGiftIds.toArray(new Long[0]));
            referralGiftPage = referralGiftService.getReferralGiftsExceptIds(page, size, uniqueList);
        } else {
            referralGiftPage = referralGiftService.getReferralGifts(page, size);
        }

        List<ReferralGift> referralGifts = referralGiftPage.getContent();
        List<ReferralGiftDTO> giftDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(referralGifts)) {
            giftDTOs = referralGifts.stream().map(g -> ReferralGiftDTO.init(g, locale, qoocoDomainPath)).collect(Collectors.toList());
        }
        PagedResultReferralGift<ReferralGiftDTO> giftResult = new PagedResultReferralGift<>(giftDTOs, page, referralGiftPage.getSize(),
                referralGiftPage.getTotalPages(), referralGiftPage.getTotalElements(),
                referralGiftPage.hasNext(), referralGiftPage.hasPrevious());
        int inactiveGift = referralClaimGiftService.countInActiveGiftByOwner(userProfileId);
        giftResult.setInActiveGift(inactiveGift);
        return new BaseResp<>(giftResult);
    }

    @Override
    public BaseResp getOwnedGifts(int page, int size, Long userProfileId, String locale) {
        boolean isExisted = userProfileService.isExist(userProfileId);
        if (!isExisted) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }
        if (page < 0 || size < 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_PAGINATION);
        }
        Page<ReferralClaimGift> referralClaimGiftPage = referralClaimGiftService.findByOwner(userProfileId, page, size);
        List<ReferralClaimGift> referralClaimGifts = referralClaimGiftPage.getContent();
        List<ReferralClaimGiftDTO> referralClaimGiftDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(referralClaimGifts)) {
            referralClaimGiftDTOS = referralClaimGifts.stream().map(r -> new ReferralClaimGiftDTO(r, locale, qoocoDomainPath)).collect(Collectors.toList());
        }
        PagedResultReferralGift<ReferralClaimGiftDTO> giftResult = new PagedResultReferralGift<>(referralClaimGiftDTOS, page, referralClaimGiftPage.getSize(),
                referralClaimGiftPage.getTotalPages(), referralClaimGiftPage.getTotalElements(),
                referralClaimGiftPage.hasNext(), referralClaimGiftPage.hasPrevious());
        int inactiveGift = referralClaimGiftService.countInActiveGiftByOwner(userProfileId);
        giftResult.setInActiveGift(inactiveGift);
        return new BaseResp<>(giftResult);
    }

    @Override
    @Transactional
    public BaseResp claimGifts(Long userProfileId, List<Long> giftIds) {
        if (CollectionUtils.isEmpty(giftIds)) {
            throw new InvalidParamException(ResponseStatus.GIFTS_EMPTY);
        }
        Long[] uniqueList = ListUtil.removeDuplicatesLongArray(giftIds.toArray(new Long[0]));
        List<ReferralGift> foundGifts = referralGiftService.findByIds(uniqueList);
        if (CollectionUtils.isEmpty(foundGifts)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_GIFT);
        }

        UserWallet userWallet = userWalletService.findById(userProfileId);
        if (Objects.isNull(userWallet)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_REFERRAL_COUNT);
        }
        int userCoins = userWallet.getBoostCoins();
        int totalGiftCoins = foundGifts.stream().mapToInt(ReferralGift::getCoin).sum();
        if (userCoins < totalGiftCoins) {
            throw new InvalidParamException(ResponseStatus.CAN_NOT_CLAIM);
        }

        List<ReferralGift> updatedGifts = foundGifts.stream().peek(gift -> {
            if (gift.getRemain() > 0) {
                int remain = gift.getRemain() - 1;
                gift.setRemain(remain);
            } else {
                throw new InvalidParamException(ResponseStatus.NO_GIFTS_AVAILABLE);
            }
        }).collect(Collectors.toList());

        List<ReferralClaimGift> gifts = foundGifts.stream().map(g -> new ReferralClaimGift(g, userProfileId)).collect(Collectors.toList());
        List<ReferralClaimGift> savedClaimAssessment = referralClaimGiftService.save(gifts);
        if (CollectionUtils.isEmpty(savedClaimAssessment)) {
            throw new InvalidParamException(ResponseStatus.SAVE_FAIL);
        }
        List<ReferralClaimGiftDTO> giftResult = savedClaimAssessment.stream().map(r -> new ReferralClaimGiftDTO(r, "", qoocoDomainPath)).collect(Collectors.toList());

        userWallet.setBoostCoins(userCoins - totalGiftCoins);
        UserWallet savedUserWallet = userWalletService.save(userWallet);
        if (Objects.isNull(savedUserWallet)) {
            throw new InvalidParamException(ResponseStatus.SAVE_FAIL);
        }

        List<ReferralGift> savedGifts = referralGiftService.save(updatedGifts);
        if (CollectionUtils.isEmpty(savedGifts)) {
            throw new InvalidParamException(ResponseStatus.SAVE_FAIL);
        }

        return new BaseResp<>(giftResult);
    }

    @Override
    public BaseResp getUserCoins(Long id) {
        boolean isExisted = userProfileService.isExist(id);
        if (!isExisted) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }
        UserWallet userWallet = userWalletService.findById(id);
        if (Objects.isNull(userWallet)) {
            return new BaseResp<>(new UserCoinsDTO());
        }
        return new BaseResp<>(new UserCoinsDTO(userWallet));
    }

    @Override
    public BaseResp trackingShareCode(Authentication auth, String code) {
        ReferralCode referralCode = referralCodeService.findActiveByOwnerAndCode(getUserId(auth), code);
        Optional.ofNullable(referralCode).map(Optional::of)
                .orElseThrow(() -> new EntityNotFoundException(ResponseStatus.NOT_FOUND_REDEEM_CODE))
                .filter(it -> !it.isExpired() || !isExpiredReferralCode(it.getCreatedDate())).map(Optional::of)
                .orElseThrow(() -> new InvalidParamException(ResponseStatus.CODE_IS_EXPIRED));

        shareCodeService.save(new ShareCode(referralCode));
        businessProfileAttributeEventService.onAttributeEvent(EVT_SHARED_GITF_CODE, getUserId(auth));
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    private List<ReferralClaimGift> createAssessmentToClaim(Long owner, int receivedGift, List<Long> assessmentIds) {
        List<ReferralClaimGift> result = new ArrayList<>();
        ReferralClaimGift assessment;
        for (Long assId : assessmentIds) {
            //TODO: when user pass Assessment test, please update ReferralClaimGift.isUsed = true
            assessment = new ReferralClaimGift(owner, assId, getPointForNextGift(claimAssessmentPoint, receivedGift), !IS_USED);
            result.add(assessment);
            receivedGift++;
        }
        return result;
    }

    private int getPointForNextGift(int[] points, int receivedGift) {
        if (receivedGift < points.length) {
            return points[receivedGift];
        } else {
            return points[claimAssessmentPoint.length - 1];
        }
    }

    private void setExpiredReferralCode(ReferralCode referralCode) {
        referralCode.setExpired(true);
        referralCodeService.save(referralCode);
    }

    private boolean isExpiredReferralCode(Date createdDate) {
        long diffHours = DateUtils.diffHours(createdDate, DateUtils.nowUtc());
        return diffHours > expiredHours;
    }

    private void createNewReferralCode(String randomCode, Long userProfileId) {
        referralCodeService.save(
                new ReferralCode(randomCode, new UserProfile(userProfileId), DateUtils.nowUtc(), false));
    }
}