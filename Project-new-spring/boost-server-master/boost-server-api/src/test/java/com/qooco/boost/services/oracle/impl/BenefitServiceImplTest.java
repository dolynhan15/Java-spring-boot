package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Benefit;
import com.qooco.boost.data.oracle.reposistories.BenefitRepository;
import com.qooco.boost.data.oracle.services.BenefitService;
import com.qooco.boost.data.oracle.services.impl.BenefitServiceImpl;
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
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class BenefitServiceImplTest {
    @InjectMocks
    private BenefitService benefitService = new BenefitServiceImpl();

    @Mock
    private BenefitRepository benefitRepository = Mockito.mock(BenefitRepository.class);

    @Test
    public void getBenefits_whenPageIsNegativeAndSizeHasValue_thenReturnBenefitResp() {
        List<Benefit> benefitList = new ArrayList<>();
        benefitList.add(new Benefit(1L));
        benefitList.add(new Benefit(2L));

        Mockito.when(benefitRepository.count()).thenReturn(2L);

        Page<Benefit> expectedResponse = new PageImpl<>(benefitList, PageRequest.of(0, 2, Sort.Direction.ASC, "name"), benefitList.size());
        Mockito.when(benefitRepository.findAll(PageRequest.of(0, (int) benefitRepository.count(), Sort.Direction.ASC, "name"))).thenReturn(expectedResponse);

        Page<Benefit> actualResp = benefitService.getBenefits(0, 2);
        Assert.assertEquals(2L, actualResp.getTotalElements());
    }

    @Test
    public void getBenefits_whenPageAndSizeAreValidNumbers_thenReturnBenefitResp() {
        List<Benefit> benefitList = new ArrayList<>();
        benefitList.add(new Benefit(1L));
        benefitList.add(new Benefit(2L));

        PageRequest pageRequest = PageRequest.of(1, 3, Sort.Direction.ASC, "name");
        Page<Benefit> expectedResponse = new PageImpl<>(benefitList, pageRequest, benefitList.size());

        Mockito.when(benefitRepository.findAll(pageRequest)).thenReturn(expectedResponse);

        Page<Benefit> actualResp = benefitService.getBenefits(1, 3);
        Assert.assertEquals(3, actualResp.getSize());
    }
}