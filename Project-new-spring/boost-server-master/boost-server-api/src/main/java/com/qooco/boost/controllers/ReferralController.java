package com.qooco.boost.controllers;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessReferralService;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.UserCoinsDTO;
import com.qooco.boost.models.dto.referral.PagedResultReferralGift;
import com.qooco.boost.models.dto.referral.ReferralClaimGiftDTO;
import com.qooco.boost.models.dto.referral.ReferralCountDTO;
import com.qooco.boost.models.dto.referral.ReferralGiftDTO;
import com.qooco.boost.models.request.ClaimCodeReq;
import com.qooco.boost.models.request.ClaimGiftReq;
import com.qooco.boost.models.request.PageRequest;
import com.qooco.boost.models.request.RedeemCodeReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Referral", value = URLConstants.REFERRAL_PATH, description = "Referral Controller")
@RestController
@RequestMapping()
public class ReferralController extends BaseController {
    @Autowired
    private BusinessReferralService referralService;

    @ApiOperation(value = "Generate redeem code", httpMethod = "GET", response = GenerateRedeemResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.REFERRAL_PATH + URLConstants.GENERATE_CODE, method = RequestMethod.GET)
    public Object generateRedeemCode(Authentication authentication) {
        Long userProfileId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        BaseResp randomCode = referralService.generateCode(userProfileId);
        return success(randomCode);
    }

    @ApiOperation(value = "Count redeem code", httpMethod = "GET", response = CountRedeemResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_CLAIM_ASSESSMENT + " : " + StatusConstants.NOT_FOUND_CLAIM_ASSESSMENT_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.REFERRAL_PATH + URLConstants.COUNT_REDEEM)
    public Object countRedeemCode(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.countRedeemCode(authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "Redeem code", httpMethod = "POST", response = RedeemCodeResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_REDEEM_CODE + " : " + StatusConstants.NOT_FOUND_REDEEM_CODE_MESSAGE
                    + "<br>" + StatusConstants.INVALID_REDEEM_CODE + " : " + StatusConstants.INVALID_REDEEM_CODE_MESSAGE
                    + "<br>" + StatusConstants.CAN_NOT_REDEEM_YOUR_OWN_CODE + " : " + StatusConstants.CAN_NOT_REDEEM_YOUR_OWN_CODE_MESSAGE
                    + "<br>" + StatusConstants.CODE_IS_EXPIRED + " : " + StatusConstants.CODE_IS_EXPIRED_MESSAGE
                    + "<br>" + StatusConstants.CODE_IS_USED + " : " + StatusConstants.CODE_IS_USED_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.REFERRAL_PATH + URLConstants.REDEEM_CODE)
    public Object redeemCode(@RequestBody final RedeemCodeReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
		AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.redeemCode(authenticatedUser.getId(), request.getCode());
        return success(result);
    }

    @ApiOperation(value = "Claim code", httpMethod = "POST", response = ClaimGiftResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ASSESSMENT_EMPTY + " : " + StatusConstants.ASSESSMENT_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_ASSESSMENT + " : " + StatusConstants.NOT_FOUND_ASSESSMENT_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_CLAIM_ASSESSMENT + " : " + StatusConstants.NOT_FOUND_CLAIM_ASSESSMENT_MESSAGE
                    + "<br>" + StatusConstants.CAN_NOT_CLAIM + " : " + StatusConstants.CAN_NOT_CLAIM_MESSAGE
                    + "<br>" + StatusConstants.INTERNAL_SERVER_ERROR + " : " + StatusConstants.INTERNAL_SERVER_ERROR_MESSAGE
                    + "<br>" + StatusConstants.CHECK_SUM_NOT_MATCH + " : " + StatusConstants.CHECK_SUM_NOT_MATCH_MESSAGE
                    + "<br>" + StatusConstants.USER_PROFILE_ID_NOT_EXIST_CODE + " : " + StatusConstants.USER_PROFILE_ID_NOT_EXIST_MESSAGE
                    + "<br>" + StatusConstants.ASSESSMENT_ID_NOT_EXIST + " : " + StatusConstants.ASSESSMENT_ID_NOT_EXIST_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.REFERRAL_PATH + URLConstants.CLAIM_FREE_ASSESSMENT)
    public Object claimFreeAssessment(@RequestBody final ClaimCodeReq request, Authentication authentication) {
       saveRequestBodyToSystemLogger(request);
	   AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.claimFreeAssessment(authenticatedUser.getId(), request.getAssessmentIdList());
        return success(result);
    }

    @ApiOperation(value = "Claim code", httpMethod = "POST", response = ClaimGiftResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.GIFTS_EMPTY + " : " + StatusConstants.GIFTS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_GIFT + " : " + StatusConstants.NOT_FOUND_GIFT_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_REFERRAL_COUNT + " : " + StatusConstants.NOT_FOUND_REFERRAL_COUNT_MESSAGE
                    + "<br>" + StatusConstants.CAN_NOT_CLAIM + " : " + StatusConstants.CAN_NOT_CLAIM_MESSAGE
                    + "<br>" + StatusConstants.NO_GIFTS_AVAILABLE + " : " + StatusConstants.NO_GIFTS_AVAILABLE_MESSAGE
                    + "<br>" + StatusConstants.SAVE_FAIL + " : " + StatusConstants.SAVE_FAIL_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(URLConstants.VERSION_2 + URLConstants.REFERRAL_PATH + URLConstants.CLAIM_GIFT)
    public Object claimGifts(@RequestBody final ClaimGiftReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
		AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.claimGifts(authenticatedUser.getId(), request.getGiftIds());
        return success(result);
    }

    @ApiOperation(value = "Claim code", httpMethod = "PUT", response = ClaimGiftResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.GIFTS_EMPTY + " : " + StatusConstants.GIFTS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_GIFT + " : " + StatusConstants.NOT_FOUND_GIFT_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_REFERRAL_COUNT + " : " + StatusConstants.NOT_FOUND_REFERRAL_COUNT_MESSAGE
                    + "<br>" + StatusConstants.CAN_NOT_CLAIM + " : " + StatusConstants.CAN_NOT_CLAIM_MESSAGE
                    + "<br>" + StatusConstants.NO_GIFTS_AVAILABLE + " : " + StatusConstants.NO_GIFTS_AVAILABLE_MESSAGE
                    + "<br>" + StatusConstants.SAVE_FAIL + " : " + StatusConstants.SAVE_FAIL_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.VERSION_2 + URLConstants.REFERRAL_PATH + URLConstants.CLAIM_GIFT + URLConstants.ID_PATH)
    @PutMapping(URLConstants.VERSION_2 + URLConstants.REFERRAL_PATH + URLConstants.CLAIM_GIFT + URLConstants.ID_PATH)
    public Object claimGift(@PathVariable(value = "id") Long id, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.claimGifts(authenticatedUser.getId(), Lists.newArrayList(id));
        return success(result);
    }

    @ApiOperation(value = "Synchronize Real Assessment Data to Referral Gift Table", httpMethod = "GET", response = ReferralGiftsResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.VERSION_2 + URLConstants.REFERRAL_PATH + URLConstants.SYNC_DATA_GIFT)
    public Object syncDataGift() {
        BaseResp result = referralService.syncRealAssessmentToReferralGiftTable();
        return success(result);
    }

    @ApiOperation(value = "Get list gift", httpMethod = "GET", response = PagingReferralGiftsResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.VERSION_2 + URLConstants.REFERRAL_PATH + URLConstants.GIFTS)
    public Object getGifts(@Valid PageRequest request,
                           @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale,
                           Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.getGifts(request.getPage(), request.getSize(), authenticatedUser.getId(), locale);
        return success(result);
    }

    @ApiOperation(value = "Get list owned gifts", httpMethod = "GET", response = PagingReferralClaimGiftResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
                    + "<br>" + StatusConstants.INVALID_PAGINATION + " : " + StatusConstants.INVALID_PAGINATION_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.VERSION_2 + URLConstants.REFERRAL_PATH + URLConstants.OWNED_GIFTS)
    public Object getOwnedGifts(@RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE_NUMBER) int page,
                           @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
                           @RequestParam(value = "locale", required = false, defaultValue = "en_US") String locale,
                           Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.getOwnedGifts(page, size, authenticatedUser.getId(), locale);
        return success(result);
    }

    @ApiOperation(value = "Get user coins", httpMethod = "GET", response = UserCoinsResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_USER_PROFILE + " : " + StatusConstants.NOT_FOUND_USER_PROFILE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.VERSION_2 + URLConstants.REFERRAL_PATH + URLConstants.USER_COINS)
    public Object getUserCoins(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        BaseResp result = referralService.getUserCoins(authenticatedUser.getId());
        return success(result);
    }

    @ApiOperation(value = "To tracking share code via OTA/Social application, It used to count point for user", httpMethod = "PATCH", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE    )
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.REFERRAL_PATH + URLConstants.SHARE_CODE + URLConstants.CODE)
    public Object trackingShareCode(Authentication authentication, @PathVariable(value = "code") String code) {
        BaseResp result = referralService.trackingShareCode(authentication, code);
        return success(result);
    }

    class GenerateRedeemResp extends BaseResp<String> {}
    class CountRedeemResp extends BaseResp<ReferralCountDTO> {}
    class UserCoinsResp extends BaseResp<UserCoinsDTO> {}
    class RedeemCodeResp extends BaseResp<Boolean> {}
    class ClaimGiftResp extends BaseResp<List<ReferralClaimGiftDTO>> {}
    class ReferralGiftsResp extends BaseResp<List<ReferralGiftDTO>> {}
    class PagingReferralGiftsResp extends BaseResp<PagedResultReferralGift<ReferralGiftDTO>> {}
    class PagingReferralClaimGiftResp extends BaseResp<PagedResultReferralGift<ReferralClaimGiftDTO>> {}

}