package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessLanguageService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.Language;
import com.qooco.boost.data.oracle.services.LanguageService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.LanguageDTO;
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

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 6:12 PM
 */

@RunWith(PowerMockRunner.class)
public class BusinessLanguageServiceImplTest {
    @InjectMocks
    private BusinessLanguageService businessLanguageService = new BusinessLanguageServiceImpl();
    @Mock
    private LanguageService languageService = Mockito.mock(LanguageService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getLanguages_whenPageAndSizeAreValidValues_thenReturnLanguageResp() {
        int page = 1;
        int size = 10;
        List<Language> languageList = new ArrayList<>();
        languageList.add(new Language(1L));
        languageList.add(new Language(2L));

        Page<Language> languagePage = new PageImpl<>(languageList);
        Mockito.when(languageService.getLanguages(page, size)).thenReturn(languagePage);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        List<LanguageDTO> languageDTOList = languagePage.getContent().stream().map(it -> new LanguageDTO(it, "")).collect(Collectors.toList());
        PagedResult<LanguageDTO> result = new PagedResult<>(languageDTOList, page, languagePage.getSize(), languagePage.getTotalPages(), languagePage.getTotalElements(),
                languagePage.hasNext(), languagePage.hasPrevious());

        BaseResp actualResponse = businessLanguageService.getLanguages(page, size, authentication);
        Assert.assertEquals(result.getSize(), ((PagedResult)actualResponse.getData()).getSize());
    }
}
