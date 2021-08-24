package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.views.ViewAccessTokenRoles;
import com.qooco.boost.data.oracle.reposistories.views.ViewAccessTokenRolesRepository;
import com.qooco.boost.data.oracle.services.impl.views.ViewAccessTokenRolesServiceImpl;
import com.qooco.boost.data.oracle.services.views.ViewAccessTokenRolesService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/4/2018 - 8:31 AM
 */
@RunWith(PowerMockRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class ViewAccessTokenRolesServiceImplTest {

    @InjectMocks
    private ViewAccessTokenRolesService viewAccessTokenRolesService = new ViewAccessTokenRolesServiceImpl();

    @Mock
    private ViewAccessTokenRolesRepository viewAccessTokenRolesRepository = Mockito.mock(ViewAccessTokenRolesRepository.class);


    @Test
    public void findByToken_whenInputRightToken_thenReturnSuccess() {

        ViewAccessTokenRoles viewAccessTokenRoles = new ViewAccessTokenRoles();
        viewAccessTokenRoles.setToken("1234567890");

        Mockito.when(viewAccessTokenRolesRepository.findByToken("1234567890")).thenReturn(viewAccessTokenRoles);

        Assert.assertEquals(viewAccessTokenRoles, viewAccessTokenRolesService.findByToken("1234567890"));
    }

    @Test
    public void findByToken_whenInputWrongToken_thenReturnFail() {

        ViewAccessTokenRoles viewAccessTokenRoles = new ViewAccessTokenRoles();
        viewAccessTokenRoles.setToken("1234567890");

        Mockito.when(viewAccessTokenRolesRepository.findByToken("1234567890")).thenReturn(viewAccessTokenRoles);

        Assert.assertNotEquals(viewAccessTokenRoles, viewAccessTokenRolesService.findByToken("12345678901"));
    }
}