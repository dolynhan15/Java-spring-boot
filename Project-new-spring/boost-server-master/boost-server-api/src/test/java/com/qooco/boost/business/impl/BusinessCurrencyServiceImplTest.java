package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.services.CurrencyService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
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
public class BusinessCurrencyServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();
    @Mock
    private CurrencyService currencyService = Mockito.mock(CurrencyService.class);
    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getCurrencies_whenPageAndSizeAreValidValues_thenReturnCurrencyResp() {
        int page = 1;
        int size = 10;

        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(new Currency(1L));
        currencyList.add(new Currency(2L));

        Page<Currency> currencyPage = new PageImpl<>(currencyList);
        Mockito.when(currencyService.getCurrencies(page, size)).thenReturn(currencyPage);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        List<CurrencyDTO> currencyDTOList = currencyPage.getContent().stream()
                .map(it -> new CurrencyDTO(it, "")).collect(Collectors.toList());
        PagedResult<CurrencyDTO> result = new PagedResult<>(currencyDTOList, page,
                currencyPage.getSize(), currencyPage.getTotalPages(), currencyPage.getTotalElements(),
                currencyPage.hasNext(), currencyPage.hasPrevious());

        BaseResp actualResponse = businessCommonService.getCurrencies(page, size, authentication);
        Assert.assertEquals(result.getSize(), ((PagedResult) actualResponse.getData()).getSize());
    }
}
