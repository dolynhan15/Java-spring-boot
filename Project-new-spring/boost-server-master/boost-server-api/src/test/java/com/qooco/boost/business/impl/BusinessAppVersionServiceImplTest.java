package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessAppVersionService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.AppVersion;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.AppVersionService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.request.AppVersionReq;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class BusinessAppVersionServiceImplTest extends BaseUserService {

    @InjectMocks
    private BusinessAppVersionService businessAppVersionService = new BusinessAppVersionServiceImpl();
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);
    @Mock
    private AppVersionService appVersionService = Mockito.mock(AppVersionService.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getLatestVersion_whenAppVersionIsNull_returnThenErrorException() {
        thrown.expect(InvalidParamException.class);
        businessAppVersionService.getLatestVersion(null, "", "");
    }

    @Test
    public void getLatestVersion_whenAppIdIsNull_returnThenErrorException() {
        thrown.expect(InvalidParamException.class);
        businessAppVersionService.getLatestVersion(1, "", "");
    }

    @Test
    public void getLatestVersion_whenOSIsNull_returnThenErrorException() {
        thrown.expect(InvalidParamException.class);
        businessAppVersionService.getLatestVersion(1, "com.boost.fit", "");
    }

    @Test
    public void getLatestVersion_whenNotFoundOldAppVersion_returnThenSuccess() {
        Integer appVersion = 1;
        String appId = "com.boost.fit";
        String os = "ios";
        mockitoGetLatestVersion(appVersion, appId, os);
        Mockito.when(appVersionService.getAppVersion(appVersion, appId, os)).thenReturn(null);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAppVersionService.getLatestVersion(appVersion, appId, os).getCode());
    }

    @Test
    public void getLatestVersion_whenInputIsValidAndHaveNewVersion_returnThenSuccess() {
        Integer appVersion = 1;
        String appId = "com.boost.fit";
        String os = "ios";
        mockitoGetLatestVersion(appVersion, appId, os);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAppVersionService.getLatestVersion(appVersion, appId, os).getCode());
    }

    @Test
    public void saveAppVersion_whenRequestIdIsNull_thenReturnSuccess() {
        AppVersionReq appVersionReq = new AppVersionReq();
        Authentication authentication = initAuthentication();
        mockitoSaveAppVersion(appVersionReq);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAppVersionService.saveAppVersion(appVersionReq, authentication).getCode());
    }

    @Test
    public void saveAppVersion_whenRequestIdIsNotNullAndNotFoundAppVersion_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        AppVersionReq appVersionReq = new AppVersionReq(1L);
        Authentication authentication = initAuthentication();
        mockitoSaveAppVersion(appVersionReq);
        Mockito.when(appVersionService.findById(appVersionReq.getId())).thenReturn(null);
        businessAppVersionService.saveAppVersion(appVersionReq, authentication);
    }

    @Test
    public void saveAppVersion_whenRequestIsNotNull_thenReturnSuccess() {
        AppVersionReq appVersionReq = new AppVersionReq(1L);
        Authentication authentication = initAuthentication();
        mockitoSaveAppVersion(appVersionReq);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAppVersionService.saveAppVersion(appVersionReq, authentication).getCode());
    }

    @Test
    public void getAll_whenInputIsValid_thenReturnSuccess() {
        Authentication authentication = initAuthentication();
        mockitoGetAll();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAppVersionService.getAll(authentication).getCode());
    }

    /*============================ Prepare before test =============================== */
    private void mockitoGetLatestVersion(Integer appVersion, String appId, String os) {
        Object[] version = new Object[]{1L, false, 2};
        List<Object[]> appVer = new ArrayList<>();
        appVer.add(version);
        Mockito.when(appVersionService.getAppVersion(appVersion, appId, os)).thenReturn(appVer);
    }

    private void mockitoSaveAppVersion(AppVersionReq request) {
        AuthenticatedUser user = initAuthenticatedUser();
        Mockito.when(businessValidatorService.checkUserProfileIsRootAdmin(user.getId())).thenReturn(new UserProfile(user.getId()));
        AppVersion appVersion = new AppVersion(1L);
        Mockito.when(appVersionService.findById(request.getId())).thenReturn(appVersion);
        Mockito.when(appVersionService.save(appVersion)).thenReturn(appVersion);
    }

    private void mockitoGetAll() {
        AuthenticatedUser user = initAuthenticatedUser();
        Mockito.when(businessValidatorService.checkUserProfileIsRootAdmin(user.getId())).thenReturn(new UserProfile(user.getId()));
        Mockito.when(appVersionService.findAll()).thenReturn(Lists.newArrayList(new AppVersion(1L)));
    }
}