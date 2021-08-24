package com.qooco.boost.securities;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.SystemLoggerService;
import com.qooco.boost.business.impl.BaseUserService;
import com.qooco.boost.constants.AccountType;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.core.model.authentication.BoostAuthenticationToken;
import com.qooco.boost.data.oracle.entities.views.ViewAccessTokenRoles;
import com.qooco.boost.data.oracle.services.views.ViewAccessTokenRolesService;
import com.qooco.boost.exception.UnauthorizedException;
import com.qooco.boost.threads.BoostActorManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.qooco.boost.constants.AttributeEventType.EVT_ENTER_APPLICATION;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;

@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
public class BoostSecurityTest extends BaseUserService {
    @Mock
    private ViewAccessTokenRolesService viewAccessTokenRolesService = Mockito.mock(ViewAccessTokenRolesService.class);

    private SystemLoggerService systemLoggerService = Mockito.mock(SystemLoggerService.class);

    @InjectMocks
    private BoostAuthenticationProvider boostAuthenticationProvider = new BoostAuthenticationProvider();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);
    @Mock
    private BusinessProfileAttributeEventService businessProfileAttributeEventService = Mockito.mock(BusinessProfileAttributeEventService.class);;

    @Test
    public void testRetrieveUser_whenMissingToken_thenReturnUnauthorizedException() throws Exception {
        String token = "12345678";
        BoostAuthenticationToken boostAuthToken = new BoostAuthenticationToken().token("");
        ViewAccessTokenRoles viewAccessTokenRoles = new ViewAccessTokenRoles((long) 1, "trungmhv", token, (short) AccountType.NORMAL, "ADMIN");

        Mockito.when(viewAccessTokenRolesService.findByToken(token))
                .thenReturn(viewAccessTokenRoles);

        thrown.expect(UnauthorizedException.class);
        boostAuthenticationProvider.retrieveUser("", boostAuthToken);
    }

    @Test
    public void testRetrieveUser_whenHasRightToken_thenReturnSuccess() {
        Authentication authentication = initAuthentication();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        String appId = PROFILE_APP.appId();
        BoostAuthenticationToken boostAuthToken =  new BoostAuthenticationToken().token(authenticatedUser.getToken());
        ViewAccessTokenRoles viewAccessTokenRoles = new ViewAccessTokenRoles((long) 1, "trungmhv", authenticatedUser.getToken(), appId, (short) AccountType.NORMAL, "ADMIN");


        Mockito.when(viewAccessTokenRolesService.findByToken(authenticatedUser.getToken())).thenReturn(viewAccessTokenRoles);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Mockito.doNothing().when(systemLoggerService).saveSystemLogger(Mockito.anyObject(), Mockito.anyObject());
        Mockito.doNothing().when(boostActorManager).saveBoostHelperMessageEventsInMongo(authentication);
        Mockito.when(businessProfileAttributeEventService.onAttributeEventAsync(EVT_ENTER_APPLICATION, 1L)).thenReturn(null);
        AuthenticatedUser user = (AuthenticatedUser) boostAuthenticationProvider.retrieveUser("", boostAuthToken);
        Assert.assertEquals(authenticatedUser.getToken(), user.getToken());
    }
}