package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Language;
import com.qooco.boost.data.oracle.entities.UserLanguage;
import com.qooco.boost.data.oracle.reposistories.UserLanguageRepository;
import com.qooco.boost.data.oracle.services.UserLanguageService;
import com.qooco.boost.data.oracle.services.impl.UserLanguageServiceImpl;
import com.qooco.boost.exception.EntityNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/17/2018 - 2:00 PM
 */
@RunWith(PowerMockRunner.class)
public class UserLanguageServiceImplTest {

    @InjectMocks
    private UserLanguageService userLanguageService = new UserLanguageServiceImpl();

    @Mock
    private UserLanguageRepository userLanguageRepository = Mockito.mock(UserLanguageRepository.class);

    @Test
    public void save_whenSaveNativeLanguage_thenReturnUserLanguageListResp() {
        List<UserLanguage> userLanguages = new ArrayList<>();

        UserLanguage userLanguage = new UserLanguage();

        userLanguage.setNative(true);
        userLanguage.setLanguage(new Language(1L));

        userLanguages.add(userLanguage);

        Mockito.when(userLanguageRepository.saveAll(userLanguages))
                .thenReturn(userLanguages);

        List<UserLanguage> actualResp = userLanguageService.save(userLanguages);

        Assert.assertEquals(userLanguages.size(), actualResp.size());
        Assert.assertEquals(userLanguages.get(0), actualResp.get(0));
    }

    @Test
    public void save_whenSaveLanguage_thenReturnUserLanguageListResp() {
        List<UserLanguage> userLanguages = new ArrayList<>();

        UserLanguage userLanguage = new UserLanguage();

        userLanguage.setNative(false);
        userLanguage.setLanguage(new Language(1L));

        userLanguages.add(userLanguage);

        Mockito.when(userLanguageRepository.saveAll(userLanguages))
                .thenReturn(userLanguages);

        List<UserLanguage> actualResp = userLanguageService.save(userLanguages);

        Assert.assertEquals(userLanguages.size(), actualResp.size());
        Assert.assertEquals(userLanguages.get(0), actualResp.get(0));
    }

    @Test
    public void deleteUserLangByUserProfileId_whenInputRightUserProfileId_thenReturnSuccess() {
        Mockito.doNothing().when(userLanguageRepository).deleteByUserProfileId(12345L);
        userLanguageService.deleteUserLangByUserProfileId(12345L);
    }

    @Test
    public void deleteUserLangByUserProfileId_whenInputWrongUserProfileId_thenReturnSuccess() {
        Mockito.doThrow(EntityNotFoundException.class).when(userLanguageRepository).deleteByUserProfileId(12345L);
        try {
            userLanguageService.deleteUserLangByUserProfileId(12345L);
        } catch (EntityNotFoundException ex) {
        }
    }
}