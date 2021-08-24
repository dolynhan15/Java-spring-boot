package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.data.oracle.entities.WorkingHour;
import com.qooco.boost.data.oracle.services.WorkingHourService;
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

@RunWith(PowerMockRunner.class)
public class BusinessWorkingHourServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();

    @Mock
    private WorkingHourService workingHourService = Mockito.mock(WorkingHourService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    @Test
    public void getWorkingHours_whenHaveWorkingHour_thenReturnBaseResp() {
        List<WorkingHour> workingHours = new ArrayList<>();
        workingHours.add(new WorkingHour(1L));
        workingHours.add(new WorkingHour(2L));

        Mockito.when(workingHourService.getAll())
                .thenReturn(workingHours);

        BaseResp actualResp = businessCommonService.getWorkingHours(authentication);
        BaseResp expectedResp = new BaseResp<>(workingHours);

        Assert.assertEquals(expectedResp.getCode(), actualResp.getCode());
    }

    @Test
    public void getWorkingHours_whenHaveNoWorkingHour_thenReturnBaseResp() {
        List<WorkingHour> workingHours = new ArrayList<>();

        Mockito.when(workingHourService.getAll())
                .thenReturn(workingHours);

        BaseResp actualResp = businessCommonService.getWorkingHours(authentication);
        BaseResp expectedResp = new BaseResp<>(workingHours);

        Assert.assertEquals(expectedResp.getCode(), actualResp.getCode());
    }
}
