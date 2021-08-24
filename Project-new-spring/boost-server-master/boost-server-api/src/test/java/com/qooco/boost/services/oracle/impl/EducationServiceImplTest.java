package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Education;
import com.qooco.boost.data.oracle.reposistories.EducationRepository;
import com.qooco.boost.data.oracle.services.EducationService;
import com.qooco.boost.data.oracle.services.impl.EducationServiceImpl;
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
public class EducationServiceImplTest {

    @InjectMocks
    private EducationService educationService = new EducationServiceImpl();

    @Mock
    private EducationRepository educationRepository = Mockito.mock(EducationRepository.class);

    @Test
    public void getAll_whenListIsEmpty_thenReturnEmptyList() {
        Mockito.when(educationRepository.findAll(new Sort(Sort.Direction.ASC, "educationId"))).thenReturn(new ArrayList<>());
        Assert.assertEquals(new ArrayList<>(), educationService.getAll());
    }

    @Test
    public void getAll_whenNoParamRequest_thenReturnAllEducationList() {
        List<Education> educationList = new ArrayList<>();
        educationList.add(new Education(1L));
        educationList.add(new Education(2L));

        Mockito.when(educationRepository.findAll(new Sort(Sort.Direction.ASC, "educationId"))).thenReturn(educationList);
        Assert.assertEquals(educationList, educationService.getAll());
    }
}