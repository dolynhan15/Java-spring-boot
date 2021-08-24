package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessUserService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.data.enumeration.Gender;
import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.entities.Language;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.CountryService;
import com.qooco.boost.data.oracle.services.LanguageService;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.data.oracle.services.impl.CountryServiceImpl;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.user.UserProfileReq;
import com.qooco.boost.utils.ServletUriUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qooco.boost.business.impl.BaseUserService.initAuthentication;

@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest(ServletUriUtils.class)
@SpringBootTest
public class BusinessUserServiceImplTest {

    @InjectMocks
    private BusinessUserService businessUserService = new BusinessUserServiceImpl();
    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);
    @Mock
    private LanguageService languageService = Mockito.mock(LanguageService.class);
    @Mock
    private CountryService countryService = Mockito.mock(CountryServiceImpl.class);
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveUserProfile_whenEmptyUserProfileId_thenReturnInvalidParamException() {
        UserProfileReq emptyUserProfile = new UserProfileReq();

        try {
            businessUserService.saveUserProfile(emptyUserProfile, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.ID_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserProfile_whenNullCountry_thenReturnInvalidParamException() {
        UserProfileReq userProfileReq = createUserProfileReq();
        userProfileReq.setCountry(null);

        thrown.expect(InvalidParamException.class);
        businessUserService.saveUserProfile(userProfileReq, initAuthentication());
    }

    @Test
    public void saveUserProfile_whenNullNativeLanguage_thenReturnInvalidParamException() {
        UserProfileReq userProfileReq = createUserProfileReq();
        userProfileReq.setNativeLanguageIds(new long[]{});
        mockito(userProfileReq);
        prepareStaticClass();
        thrown.expect(InvalidParamException.class);
        businessUserService.saveUserProfile(userProfileReq, initAuthentication());
    }

    @Test
    public void saveUserProfile_whenNotExistNativeLanguage_thenReturnInvalidParamException() {
        UserProfileReq userProfileReq = createUserProfileReq();
        mockito(userProfileReq);
        prepareStaticClass();
        Mockito.when(languageService.findByIds(userProfileReq.getNativeLanguageIds())).thenReturn(null);
        thrown.expect(EntityNotFoundException.class);
        businessUserService.saveUserProfile(userProfileReq, initAuthentication());
    }

    @Test
    public void saveUserProfile_whenNotExistLanguage_thenReturnInvalidParamException() {
        UserProfileReq userProfileReq = createUserProfileReq();
        mockito(userProfileReq);
        prepareStaticClass();
        Mockito.when(languageService.findByIds(userProfileReq.getLanguageIds())).thenReturn(null);
        thrown.expect(EntityNotFoundException.class);
        businessUserService.saveUserProfile(userProfileReq, initAuthentication());
    }


//    @Test
//    public void saveUserProfile_whenHaveNativeLanguage_thenUpdateNativeLanguageAndReturnUserProfile() {
//        UserProfileReq userProfileReq = createUserProfileReq();
//        mockito(userProfileReq);
//        prepareStaticClass();
//
//        UserProfile userProfile = new UserProfile(userProfileReq.getId(), "longntran");
//        BaseResp<UserProfile> userProfileBaseResp = new BaseResp<>(userProfile);
//        Mockito.when(userProfileService.save(userProfile)).thenReturn(userProfile);
//
//        Assert.assertEquals(userProfileBaseResp.getCode(), businessUserService.saveUserProfile(userProfileReq).getCode());
//    }
//
//    @Test
//    public void saveUserProfile_whenHaveValidUserProfileId_thenReturnUserProfile() {
//        UserProfileReq userProfileReq = createUserProfileReq();
//        prepareStaticClass();
//        mockito(userProfileReq);
//
//        UserProfile validUserProfile = new UserProfile(userProfileReq.getId(), "longntran");
//        Mockito.when(userProfileService.save(validUserProfile)).thenReturn(validUserProfile);
//        BaseResp<UserProfile> userProfileBaseResp = new BaseResp<>(validUserProfile);
//        Assert.assertEquals(userProfileBaseResp.getCode(), businessUserService.saveUserProfile(userProfileReq).getCode());
//    }

    @Test
    public void findById_whenEmptyId_thenReturnNullData() {
        Long userProfileId = 1L;

        Mockito.when(userProfileService.findById(userProfileId)).thenReturn(null);

        BaseResp foundUserProfile = businessUserService.findById(userProfileId);
        Assert.assertNull(foundUserProfile.getData());
    }

    @Test
    public void findById_whenValidId_thenReturnUserProfile() {
        Long userProfileId = 10005236466L;
        UserProfile responseUserProfile = new UserProfile(userProfileId);

        Mockito.when(userProfileService.findById(userProfileId))
                .thenReturn(responseUserProfile);

        BaseResp<UserProfile> expectedUserProfile = new BaseResp<>(responseUserProfile);
        BaseResp foundUserProfile = businessUserService.findById(userProfileId);
        Assert.assertEquals(expectedUserProfile.getData(), foundUserProfile.getData());
    }

    @Test
    public void getUserProfile_whenUserProfileIdIsNull_thenReturnIdIsEmpty() {
        try {
            businessUserService.getBasicProfile(null, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.ID_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getUserProfile_whenUserProfileIdIsInvalid_thenReturnIdIsNotFound() {
        try {
            businessUserService.getBasicProfile(0L, initAuthentication());
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND.getCode(), ex.getStatus().getCode());
        }
    }

    private static UserProfileReq createUserProfileReq() {
        UserProfileReq userProfileReq = new UserProfileReq();
        userProfileReq.setId(1L);
        userProfileReq.setNativeLanguageIds(new long[]{1L});
        userProfileReq.setLanguageIds(new long[]{2L});
        userProfileReq.setCountry(1L);
        userProfileReq.setAvatar("http://google.com/image.png");
        userProfileReq.setBirthday(new Date());
        userProfileReq.setGender(Gender.FEMALE);
        userProfileReq.setNationalId("212323");
        userProfileReq.setFirstName("FirstName");
        userProfileReq.setLastName("LastName");
        return userProfileReq;
    }

    private void mockito(UserProfileReq userProfileReq) {
        Mockito.when(countryService.findById(userProfileReq.getCountry())).thenReturn(new Country(userProfileReq.getCountry()));
        Mockito.when(languageService.findByIds(userProfileReq.getLanguageIds())).thenReturn(createLanguages(userProfileReq.getLanguageIds()));
        Mockito.when(languageService.findByIds(userProfileReq.getNativeLanguageIds())).thenReturn(createLanguages(userProfileReq.getNativeLanguageIds()));
        Mockito.when(businessValidatorService.checkExistsUserProfile(userProfileReq.getId())).thenReturn(new UserProfile(userProfileReq.getId()));
        Mockito.when(businessValidatorService.checkExistsCountry(userProfileReq.getCountry())).thenReturn(new Country(userProfileReq.getCountry()));
        Mockito.when(businessValidatorService.checkExistsCity(userProfileReq.getCityId())).thenReturn(new City(userProfileReq.getCityId()));
    }

    private void prepareStaticClass() {
        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getRelativePath(Mockito.anyObject())).thenReturn("avatarLink");
    }

    private List<Language> createLanguages(long[] object) {
        List<Language> languages = new ArrayList<>();
        for (long id : object) {
            languages.add(new Language(id));
        }
        return languages;
    }
}
