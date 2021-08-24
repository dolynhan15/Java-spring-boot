package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.Education;
import com.qooco.boost.data.oracle.services.EducationService;
import com.qooco.boost.models.BaseResp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static com.qooco.boost.business.impl.BaseUserService.initAuthenticatedUser;

@RunWith(PowerMockRunner.class)
public class BusinessEducationsServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();
    @Mock
    private EducationService educationService = Mockito.mock(EducationService.class);
    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getEducations_whenHaveEducations_thenReturnBaseResp() {
        List<Education> educations = new ArrayList<>();
        educations.add(new Education(1L));
        educations.add(new Education(2L));

        Mockito.when(educationService.getAll())
                .thenReturn(educations);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        BaseResp actualResp = businessCommonService.getEducations(authentication);
        BaseResp expectedResp = new BaseResp<>(educations);

        Assert.assertEquals(expectedResp.getCode(), actualResp.getCode());
    }

    @Test
    public void getEducations_whenHaveNoEducation_thenReturnBaseResp() {
        List<Education> educations = new ArrayList<>();

        Mockito.when(educationService.getAll())
                .thenReturn(educations);

        BaseResp actualResp = businessCommonService.getEducations(authentication);
        BaseResp expectedResp = new BaseResp<>(educations);

        Assert.assertEquals(expectedResp.getCode(), actualResp.getCode());
    }
}
