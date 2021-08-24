package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.data.oracle.reposistories.AssessmentRepository;
import com.qooco.boost.data.oracle.services.AssessmentService;
import com.qooco.boost.data.oracle.services.impl.AssessmentServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 8/7/2018 - 10:48 AM
 */
@RunWith(PowerMockRunner.class)
public class AssessmentServiceImplTest {

    @InjectMocks
    private AssessmentService assessmentService = new AssessmentServiceImpl();

    @Mock
    private AssessmentRepository assessmentRepository = Mockito.mock(AssessmentRepository.class);

    @Test
    public void getAssessments_whenPageIsNegativeAndSizeHasValue_thenReturnAssessmentsResp() {
        List<Assessment> assessmentList = new ArrayList<>();
        assessmentList.add(new Assessment(1L));
        assessmentList.add(new Assessment(2L));

        Mockito.when(assessmentRepository.count()).thenReturn(2L);

        Page<Assessment> expectedResponse = new PageImpl<>(assessmentList, PageRequest.of(0, 2), assessmentList.size());
        Mockito.when(assessmentRepository.findAll(PageRequest.of(0, (int) assessmentRepository.count()))).thenReturn(expectedResponse);

        Page<Assessment> actualResp = assessmentService.getAssessments(0, 2);

        Assert.assertEquals(2L, actualResp.getTotalElements());
    }

    @Test
    public void getAssessments_whenPageAndSizeAreValidNumbers_thenReturnAssessmentsResp() {
        List<Assessment> assessmentList = new ArrayList<>();
        assessmentList.add(new Assessment(1L));
        assessmentList.add(new Assessment(2L));

        PageRequest pageRequest = PageRequest.of(1, 3);
        Page<Assessment> expectedResponse = new PageImpl<>(assessmentList, pageRequest, assessmentList.size());

        Mockito.when(assessmentRepository.findAll(pageRequest)).thenReturn(expectedResponse);

        Page<Assessment> actualResp = assessmentService.getAssessments(1, 3);
        Assert.assertEquals(3, actualResp.getSize());
    }
}