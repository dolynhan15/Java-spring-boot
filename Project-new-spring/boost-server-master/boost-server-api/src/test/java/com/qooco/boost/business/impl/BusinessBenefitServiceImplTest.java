package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.Benefit;
import com.qooco.boost.data.oracle.services.BenefitService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.BenefitDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.qooco.boost.business.impl.BaseUserService.initAuthenticatedUser;

@RunWith(PowerMockRunner.class)
public class BusinessBenefitServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();

    @Mock
    private BenefitService benefitService = Mockito.mock(BenefitService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getBenefits_whenPageAndSizeAreValidValues_thenReturnBenefitResp() {
        int page = 1;
        int size = 10;
        List<Benefit> benefitList = new ArrayList<>();
        benefitList.add(new Benefit(1L));
        benefitList.add(new Benefit(2L));
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Page<Benefit> benefitPage = new PageImpl<>(benefitList);
        Mockito.when(benefitService.getBenefits(page, size)).thenReturn(benefitPage);
        List<BenefitDTO> benefitDTOList = benefitPage.getContent().stream().map(it -> new BenefitDTO(it, "")).collect(Collectors.toList());
        PagedResult<BenefitDTO> result = new PagedResult<>(benefitDTOList, page, benefitPage.getSize(), benefitPage.getTotalPages(), benefitPage.getTotalElements(),
                benefitPage.hasNext(), benefitPage.hasPrevious());

        BaseResp actualResponse = businessCommonService.getBenefits(page, size, authentication);
        Assert.assertEquals(result.getSize(), ((PagedResult) actualResponse.getData()).getSize());
    }
}
