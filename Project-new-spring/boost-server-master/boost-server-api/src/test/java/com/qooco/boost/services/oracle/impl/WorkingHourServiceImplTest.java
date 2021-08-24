package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.WorkingHour;
import com.qooco.boost.data.oracle.reposistories.WorkingHourRepository;
import com.qooco.boost.data.oracle.services.WorkingHourService;
import com.qooco.boost.data.oracle.services.impl.WorkingHourServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class WorkingHourServiceImplTest {
    @InjectMocks
    private WorkingHourService workingHourService = new WorkingHourServiceImpl();

    @Mock
    private WorkingHourRepository workingHourRepository = Mockito.mock(WorkingHourRepository.class);

    @Test
    public void getAll_whenListIsEmpty_thenReturnEmptyList() {
        Mockito.when(workingHourRepository.findAll(new Sort(Sort.Direction.ASC, "startDate"))).thenReturn(new ArrayList<>());
        Assert.assertEquals(new ArrayList<>(), workingHourService.getAll());
    }

    @Test
    public void getAll_whenNoParamRequest_thenReturnAllWorkingHourList() {
        List<WorkingHour> workingHourList = new ArrayList<>();
        workingHourList.add(new WorkingHour(1L));
        workingHourList.add(new WorkingHour(2L));

        Mockito.when(workingHourRepository.findAll(new Sort(Sort.Direction.ASC, "startDate"))).thenReturn(workingHourList);
        Assert.assertEquals(workingHourList, workingHourService.getAll());
    }
}