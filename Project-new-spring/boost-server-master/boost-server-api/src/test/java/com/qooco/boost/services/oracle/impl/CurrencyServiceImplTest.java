package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.reposistories.CurrencyRepository;
import com.qooco.boost.data.oracle.services.CurrencyService;
import com.qooco.boost.data.oracle.services.impl.CurrencyServiceImpl;
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
import java.util.Optional;

@RunWith(PowerMockRunner.class)
public class CurrencyServiceImplTest {

    @InjectMocks
    private CurrencyService currencyService = new CurrencyServiceImpl();

    @Mock
    private CurrencyRepository currencyRepository = Mockito.mock(CurrencyRepository.class);

    @Test
    public void getCurrencies_whenPageIsNegativeAndSizeHasValue_thenReturnCurrencyResp() {
        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(new Currency(1L));
        currencyList.add(new Currency(2L));

        Mockito.when(currencyRepository.count()).thenReturn(2L);

        Page<Currency> expectedResponse = new PageImpl<>(currencyList, PageRequest.of(0, 2, Sort.Direction.ASC, "name"), currencyList.size());
        Mockito.when(currencyRepository.findAll(PageRequest.of(0, (int) currencyRepository.count(), Sort.Direction.ASC, "name"))).thenReturn(expectedResponse);

        Page<Currency> actualResp = currencyService.getCurrencies(0, 2);
        Assert.assertEquals(2L, actualResp.getTotalElements());
    }

    @Test
    public void getCurrencies_whenPageAndSizeAreValidNumbers_thenReturnCurrencyResp() {
        List<Currency> currencyList = new ArrayList<>();
        currencyList.add(new Currency(1L));
        currencyList.add(new Currency(2L));

        PageRequest pageRequest = PageRequest.of(1, 3, Sort.Direction.ASC, "name");
        Page<Currency> expectedResponse = new PageImpl<>(currencyList, pageRequest, currencyList.size());

        Mockito.when(currencyRepository.findAll(pageRequest)).thenReturn(expectedResponse);

        Page<Currency> actualResp = currencyService.getCurrencies(1, 3);
        Assert.assertEquals(3, actualResp.getSize());
    }

    @Test
    public void findById_whenHaveValidId_thenReturnCurrencyResp() {
        Long currencyId = 1L;
        Currency expectedResponse = new Currency(currencyId);

        Mockito.when(currencyRepository.findById(currencyId)).thenReturn(Optional.of(expectedResponse));

        Currency actualResponse = currencyService.findById(currencyId);

        Assert.assertEquals(expectedResponse, actualResponse);
    }
}