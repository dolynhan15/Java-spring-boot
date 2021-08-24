package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessReferralService;
import com.qooco.boost.business.QoocoService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.oracle.entities.ReferralCode;
import com.qooco.boost.data.oracle.entities.ReferralRedeem;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.entities.UserWallet;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.RandomString;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@PrepareForTest({ApplicationConstant.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class BusinessReferralServiceImplTest {
    @InjectMocks
    private BusinessReferralService businessReferralService = new BusinessReferralServiceImpl();

    @Mock
    private ReferralCodeService referralCodeService = Mockito.mock(ReferralCodeService.class);

    @Mock
    private UserWalletService userWalletService = Mockito.mock(UserWalletService.class);

    @Mock
    private ReferralClaimGiftService referralClaimAssessmentService = Mockito.mock(ReferralClaimGiftService.class);

    @Mock
    private ReferralRedeemService referralRedeemService = Mockito.mock(ReferralRedeemService.class);

    @Mock
    private AssessmentService assessmentService = Mockito.mock(AssessmentService.class);

    @Mock
    private QoocoService qoocoService = Mockito.mock(QoocoService.class);

    @Mock
    private RandomString randomString = Mockito.mock(RandomString.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void redeemCode_whenReferralCodeIsNull_thenReturnInvalidExceptionError() {
        thrown.expect(InvalidParamException.class);
        businessReferralService.redeemCode(1L, null);
    }

    @Test
    public void redeemCode_whenNotFoundReferralCode_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        String referralCode = "CKIKZ2IG";
        Long redeemer = 1L;
        mockitoRedeemCode();
        Mockito.when(referralCodeService.findByCode(referralCode)).thenReturn(null);
        businessReferralService.redeemCode(redeemer, referralCode);
    }

    @Test
    public void redeemCode_whenOwnerIsSameRedeemer_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        String referralCode = "CKIKZ2IG";
        Long redeemer = 1L;
        mockitoRedeemCode();
        Date createdDate = DateUtils.addDays(new Date(), -10);
        ReferralCode existCode = new ReferralCode(1L, referralCode, new UserProfile(1L), createdDate, false);
        Mockito.when(referralCodeService.findByCode(referralCode)).thenReturn(existCode);
        businessReferralService.redeemCode(redeemer, referralCode);
    }

    @Test
    public void redeemCode_whenCodeIsExpired_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        String referralCode = "CKIKZ2IG";
        Long redeemer = 1L;
        mockitoRedeemCode();
        Date createdDate = DateUtils.addDays(new Date(), -10);
        ReferralCode existCode = new ReferralCode(1L, referralCode, new UserProfile(2L), createdDate, true);
        Mockito.when(referralCodeService.findByCode(referralCode)).thenReturn(existCode);
        businessReferralService.redeemCode(redeemer, referralCode);
    }

    @Test
    public void redeemCode_whenCodeIsLargerThan12Hours_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        String referralCode = "CKIKZ2IG";
        Long redeemer = 1L;
        mockitoRedeemCode();
        Date createdDate = DateUtils.addDays(new Date(), -10);
        ReferralCode existCode = new ReferralCode(1L, referralCode, new UserProfile(2L), createdDate, false);
        Mockito.when(referralCodeService.findByCode(referralCode)).thenReturn(existCode);
        businessReferralService.redeemCode(redeemer, referralCode);
    }

    @Test
    public void redeemCode_whenCodeRedeemOver6Times_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        String referralCode = "CKIKZ2IG";
        Long redeemer = 1L;
        Long referralCodeId = 1L;
        mockitoRedeemCode();
        List<ReferralRedeem> totalRedeemTimes = new ArrayList<>();
        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, redeemer));
        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 2L));
        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 3L));
        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 4L));
        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 5L));
        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 6L));
        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 7L));
        Mockito.when(referralRedeemService.findByReferralCodeId(referralCodeId)).thenReturn(totalRedeemTimes);
        businessReferralService.redeemCode(redeemer, referralCode);
    }

    @Test
    public void redeemCode_whenCodeIsUsed_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        String referralCode = "CKIKZ2IG";
        Long redeemer = 1L;
        mockitoRedeemCode();
        businessReferralService.redeemCode(redeemer, referralCode);
    }

//    @Test
//    public void redeemCode_whenReferralRedeemCannotSave_thenReturnSuccess() throws ReflectiveOperationException {
//        String referralCode = "CKIKZ2IG";
//        Long redeemer = 13L;
//        Long referralCodeId = 1L;
//        mockitoRedeemCode();
////        setFinalStaticField(ApplicationConstant.class, ApplicationConstant.REFERRAL_MAX_REDEEM_TIMES, 6);
//        Mockito.when(referralRedeemService.createConversation(new ReferralRedeem(referralCodeId, redeemer))).thenReturn(null);
//        businessReferralService.redeemCode(redeemer, referralCode);
//    }
//
//    @Test
//    public void redeemCode_whenUserWallerIsNull_thenReturnSuccess() {
//        String referralCode = "CKIKZ2IG";
//        Long redeemer = 3L;
//        Long ownerId = 2L;
//        mockitoRedeemCode();
//        Mockito.when(userWalletService.findById(ownerId)).thenReturn(null);
//        businessReferralService.redeemCode(redeemer, referralCode);
//    }
//
//    @Test
//    public void redeemCode_whenRedeemCodeIs5Times_thenReturnSuccess() {
//        String referralCode = "CKIKZ2IG";
//        Long redeemer = 3L;
//        Long referralCodeId = 1L;
//        mockitoRedeemCode();
//        List<ReferralRedeem> totalRedeemTimes = new ArrayList<>();
//        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, redeemer));
//        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 4L));
//        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 5L));
//        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 6L));
//        totalRedeemTimes.add(new ReferralRedeem(referralCodeId, 7L));
//        Mockito.when(referralRedeemService.findByReferralCodeId(referralCodeId)).thenReturn(totalRedeemTimes);
//        businessReferralService.redeemCode(redeemer, referralCode);
//    }

    /*======================================= Prepare for test ================================================== */
    private void mockitoRedeemCode() {
        String referralCode = "CKIKZ2IG";
        Long referralCodeId = 1L;
        Long redeemer = 1L;
        Long ownerId = 2L;
        Date createdDate = new Date();
        ReferralCode existCode = new ReferralCode(referralCodeId, referralCode, new UserProfile(ownerId), createdDate, false);
        ReferralRedeem referralRedeem = new ReferralRedeem(referralCodeId, redeemer);
        List<ReferralRedeem> totalRedeemTimes = Lists.newArrayList(referralRedeem);
        UserWallet ownerWallet = new UserWallet(ownerId);
        UserWallet userWallet = new UserWallet(redeemer);

        Mockito.when(referralCodeService.findByCode(referralCode)).thenReturn(existCode);
        Mockito.when(referralRedeemService.findByReferralCodeId(existCode.getReferralCodeId())).thenReturn(totalRedeemTimes);
        Mockito.when(referralCodeService.save(existCode)).thenReturn(existCode);
        Mockito.when(referralRedeemService.save(new ReferralRedeem(existCode.getReferralCodeId(), redeemer))).thenReturn(referralRedeem);
        Mockito.when(userWalletService.findById(ownerId)).thenReturn(ownerWallet);
        Mockito.when(userWalletService.findById(redeemer)).thenReturn(userWallet);
        Mockito.when(userWalletService.save(ownerWallet)).thenReturn(ownerWallet);
        Mockito.when(userWalletService.save(userWallet)).thenReturn(userWallet);
    }

    private static void setFinalStaticField(Class<?> clazz, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, value);
    }
//    static void setFinalStatic(Field field, Object newValue) throws Exception {
//        field.setAccessible(true);
//        Field modifiersField = Field.class.getDeclaredField("modifiers");
//        modifiersField.setAccessible(true);
//        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
//        field.set(null, newValue);
//    }
}