package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Language;
import com.qooco.boost.data.oracle.reposistories.LanguageRepository;
import com.qooco.boost.data.oracle.services.LanguageService;
import com.qooco.boost.data.oracle.services.impl.LanguageServiceImpl;
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
 * Date: 7/10/2018 - 1:33 PM
 */
@RunWith(PowerMockRunner.class)
public class LanguageServiceImplTest {

    @InjectMocks
    private LanguageService languageService = new LanguageServiceImpl();

    @Mock
    private LanguageRepository languageRepository = Mockito.mock(LanguageRepository.class);

    @Test
    public void getLanguages_whenPageIsNegativeAndSizeHasValue_thenReturnLanguageResp() {
        List<Language> listLanguages = new ArrayList<>();
        listLanguages.add(new Language(1L));
        listLanguages.add(new Language(2L));

        Mockito.when(languageRepository.count()).thenReturn(2L);

        Page<Language> expectedResponse = new PageImpl<>(listLanguages, PageRequest.of(0, 2), listLanguages.size());
        Mockito.when(languageRepository.findAll(PageRequest.of(0, (int) languageRepository.count()))).thenReturn(expectedResponse);

        Page<Language> actualResp = languageService.getLanguages(0, 2);
        Assert.assertEquals(2L, actualResp.getTotalElements());
    }

    @Test
    public void get_whenPageAndSizeAreValidNumbers_thenReturnLanguageResp() {
        List<Language> listLanguages = new ArrayList<>();
        listLanguages.add(new Language(1L));
        listLanguages.add(new Language(2L));

        PageRequest pageRequest = PageRequest.of(1, 3);
        Page<Language> expectedResponse = new PageImpl<>(listLanguages, pageRequest, listLanguages.size());

        Mockito.when(languageRepository.findAll(pageRequest)).thenReturn(expectedResponse);

        Page<Language> actualResp = languageService.getLanguages(1, 3);
        Assert.assertEquals(3, actualResp.getSize());
    }
}