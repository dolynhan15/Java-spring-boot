package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessUserCurriculumVitaeService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.UserPreviousPosition;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.CurrencyService;
import com.qooco.boost.data.oracle.services.UserCurriculumVitaeService;
import com.qooco.boost.data.oracle.services.UserPreviousPositionService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.dto.user.UserPreviousPositionDTO;
import com.qooco.boost.models.user.UserPreviousPositionReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@PrepareForTest({ServletUriUtils.class})
public class BusinessUserCurriculumVitaeImplTests extends BaseUserService {

    @InjectMocks
    private BusinessUserCurriculumVitaeService businessAuthorizationService = new BusinessUserCurriculumVitaeServiceImpl();

    @Mock
    private UserPreviousPositionService userPreviousPositionService = Mockito.mock(UserPreviousPositionService.class);

    @Mock
    private CurrencyService currencyService = Mockito.mock(CurrencyService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);

    @Mock
    private UserCurriculumVitaeService userCurriculumVitaeService = Mockito.mock(UserCurriculumVitaeService.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static long userPreviousPositionId = 1L;
    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    /*=============== START: Unit test for user previous position =========================*/
    private UserPreviousPositionReq initUserPreviousPositionReq() {
        UserPreviousPositionReq userPreviousPositionReq = new UserPreviousPositionReq();
        userPreviousPositionReq.setId(userPreviousPositionId);
        userPreviousPositionReq.setCompanyName("Company A");
        userPreviousPositionReq.setPositionName("Position A");
        userPreviousPositionReq.setPhotos(Arrays.asList("avatar"));
        userPreviousPositionReq.setStartDate(new Date());
        userPreviousPositionReq.setEndDate(new Date());
        return userPreviousPositionReq;
    }

    @Test
    public void doSaveUserPrevious_whenEmptyCompanyName_thenReturnErrorCompanyEmpty() {
        UserPreviousPositionReq req = initUserPreviousPositionReq();
        thrown.expect(InvalidParamException.class);
        req.setCompanyName(null);
        businessAuthorizationService.saveUserPreviousPosition(req, null);
    }

    @Test
    public void doSaveUserPrevious_whenEmptyPositionName_thenReturnErrorPositionEmpty() {
        UserPreviousPositionReq req = initUserPreviousPositionReq();
        thrown.expect(InvalidParamException.class);
        req.setPositionName(null);
        businessAuthorizationService.saveUserPreviousPosition(req, null);
    }

    @Test
    public void doSaveUserPrevious_whenEmptyStartDate_thenReturnInvalidParamException() {
        UserPreviousPositionReq req = initUserPreviousPositionReq();
        thrown.expect(InvalidParamException.class);
        req.setStartDate(null);
        businessAuthorizationService.saveUserPreviousPosition(req, null);
    }

    @Test
    public void doSaveUserPrevious_whenWrongStartDate_thenReturnInvalidParamException() {
        UserPreviousPositionReq req = initUserPreviousPositionReq();
        thrown.expect(InvalidParamException.class);
        req.setStartDate(DateUtils.addDays(new Date(), 1));
        businessAuthorizationService.saveUserPreviousPosition(req, null);
    }

    @Test
    public void doSaveUserPrevious_whenStartDateAfterEndDate_thenReturnInvalidParamException() {
        UserPreviousPositionReq req = initUserPreviousPositionReq();
        thrown.expect(InvalidParamException.class);
        req.setStartDate(new Date());
        req.setEndDate(DateUtils.addDays(new Date(), -1));
        businessAuthorizationService.saveUserPreviousPosition(req, null);
    }

    @Test
    public void doSaveUserPrevious_whenSaveNewPreviousPosition_thenReturnSuccess() {
        mockStatic();

        UserPreviousPositionReq req = initUserPreviousPositionReq();
        req.setId(null);
        req.setSalary(1L);
        req.setCurrencyId(1L);

        UserPreviousPosition input = req.updateToPreviousPosition(new UserPreviousPosition());
        UserPreviousPosition out = req.updateToPreviousPosition(new UserPreviousPosition(userPreviousPositionId));

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(userPreviousPositionService.save(input)).thenReturn(out);
        Mockito.when(currencyService.findById(1L)).thenReturn(new Currency(1L));
        Mockito.when(userCurriculumVitaeService.findByUserProfile(new UserProfile(authenticatedUser.getId()))).thenReturn(null);
        businessAuthorizationService.saveUserPreviousPosition(req, authentication);
        Assert.assertEquals(new UserPreviousPositionDTO(out, ""), (businessAuthorizationService.saveUserPreviousPosition(req, authentication)).getData());
    }

    @Test
    public void doSaveUserPrevious_whenUpdatePreviousPosition_thenReturnSuccess() {
        mockStatic();

        UserPreviousPositionReq req = initUserPreviousPositionReq();
        req.setId(userPreviousPositionId);
        req.setSalary(1L);
        req.setCurrencyId(1L);

        UserPreviousPosition input = req.updateToPreviousPosition(new UserPreviousPosition());
        UserPreviousPosition out = req.updateToPreviousPosition(new UserPreviousPosition(userPreviousPositionId));

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(currencyService.findById(1L)).thenReturn(new Currency(1L));
        Mockito.doReturn(out).when(userPreviousPositionService).save(input);
        Mockito.doReturn(out).when(userPreviousPositionService).findById(userPreviousPositionId);
        Mockito.doNothing().when(boostActorManager).deleteFile(StringUtil.convertToList(out.getPhoto()));
        Mockito.when(userCurriculumVitaeService.findByUserProfile(new UserProfile(authenticatedUser.getId()))).thenReturn(null);
        Assert.assertNotEquals(out, (businessAuthorizationService.saveUserPreviousPosition(req, authentication)).getData());
    }

    @Test
    public void doSaveUserPrevious_whenUpdatePreviousPositionWithWrongPreviousId_thenReturnEntityNotFoundException() {
        mockStatic();

        UserPreviousPositionReq req = initUserPreviousPositionReq();
        req.setId(userPreviousPositionId);
        req.setSalary(1L);
        req.setCurrencyId(1L);

        UserPreviousPosition input = req.updateToPreviousPosition(new UserPreviousPosition());
        UserPreviousPosition out = req.updateToPreviousPosition(new UserPreviousPosition(userPreviousPositionId));

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(currencyService.findById(1L)).thenReturn(new Currency(1L));
        Mockito.doReturn(out).when(userPreviousPositionService).findById(2L);
        thrown.expect(EntityNotFoundException.class);

        Assert.assertNotEquals(out, (businessAuthorizationService.saveUserPreviousPosition(req, authentication)).getData());
    }

    @Test
    public void getUserPreviousPositionById_whenPreviousPositionIdEmpty_thenReturnInvalidParamException() {
        mockStatic();
        thrown.expect(InvalidParamException.class);
        businessAuthorizationService.getUserPreviousPositionById(null, authentication);
    }

    @Test
    public void getUserPreviousPositionById_whenRightPreviousPositionID_thenReturnSuccess() {
        mockStatic();
        UserPreviousPositionReq req = initUserPreviousPositionReq();
        UserPreviousPosition out = req.updateToPreviousPosition(new UserPreviousPosition());
        Mockito.doReturn(out).when(userPreviousPositionService).findById(1L);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Assert.assertEquals(new UserPreviousPositionDTO(out, ""), businessAuthorizationService.getUserPreviousPositionById((long) 1, authentication).getData());
    }

    @Test
    public void deleteUserPreviousPositions_whenPreviousPositionIdEmpty_thenReturnInvalidParamException() {
        thrown.expect(InvalidParamException.class);
        businessAuthorizationService.deleteUserPreviousPositions(null);
    }

    @Test
    public void deleteUserPreviousPositions_whenRightPreviousPositionID_thenReturnSuccess() {
        UserPreviousPosition userPreviousPosition = new UserPreviousPosition(1L);
        userPreviousPosition.setCreatedBy(1L);
        Mockito.doReturn(userPreviousPosition).when(userPreviousPositionService).findById(1L);
        Mockito.doNothing().when(userPreviousPositionService).deleteUserPreviousPositionById(1L);
        Mockito.when(userCurriculumVitaeService.findByUserProfile(new UserProfile(1L))).thenReturn(null);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAuthorizationService.deleteUserPreviousPositions((long) 1).getCode());
    }

    private static void mockStatic(){
        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getRelativePaths(Mockito.any())).thenReturn(Arrays.asList("http://google.com/image.jpg"));
    }


    /*=============== END: Unit test for user previous position =========================*/

}
