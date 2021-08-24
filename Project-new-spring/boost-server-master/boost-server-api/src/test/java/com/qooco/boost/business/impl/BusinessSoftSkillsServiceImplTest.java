package com.qooco.boost.business.impl;


import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.SoftSkill;
import com.qooco.boost.data.oracle.services.SoftSkillsService;
import com.qooco.boost.models.BaseResp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

import static com.qooco.boost.business.impl.BaseUserService.initAuthenticatedUser;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/16/2018 - 3:07 PM
 */
@RunWith(PowerMockRunner.class)
@SpringBootTest
public class BusinessSoftSkillsServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();
    @Mock
    private SoftSkillsService softSkillsService = Mockito.mock(SoftSkillsService.class);
    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getSoftSkills_whenHaveSoftSkill_thenReturnBaseResp() {
        List<SoftSkill> softSkills = new ArrayList<>();
        softSkills.add(new SoftSkill(1L, "Teamwork"));
        softSkills.add(new SoftSkill(2L, "Creativity"));

        Mockito.when(softSkillsService.getAll())
                .thenReturn(softSkills);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        BaseResp actualResp = businessCommonService.getSoftSkills(authentication);
        BaseResp expectedResp = new BaseResp<>(softSkills);

        Assert.assertEquals(expectedResp.getCode(), actualResp.getCode());
    }

    @Test
    public void getSoftSkills_whenHaveNoSoftSkill_thenReturnBaseResp() {
        List<SoftSkill> softSkills = new ArrayList<>();

        Mockito.when(softSkillsService.getAll())
                .thenReturn(softSkills);

        BaseResp actualResp = businessCommonService.getSoftSkills(authentication);
        BaseResp expectedResp = new BaseResp<>(softSkills);

        Assert.assertEquals(expectedResp.getCode(), actualResp.getCode());
    }
}
