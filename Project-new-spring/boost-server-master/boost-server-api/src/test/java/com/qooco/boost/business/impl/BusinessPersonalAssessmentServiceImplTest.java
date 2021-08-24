package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessPersonalAssessmentService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;
import com.qooco.boost.data.oracle.entities.UserPersonality;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.PersonalAssessmentQuestionService;
import com.qooco.boost.data.oracle.services.PersonalAssessmentService;
import com.qooco.boost.data.oracle.services.UserPersonalityService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.threads.BoostActorManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class BusinessPersonalAssessmentServiceImplTest {
    @InjectMocks
    private BusinessPersonalAssessmentService unitService = new BusinessPersonalAssessmentServiceImpl();
    @Mock
    private PersonalAssessmentService personalAssessmentService = Mockito.mock(PersonalAssessmentService.class);
    @Mock
    private UserPersonalityService userPersonalityService = Mockito.mock(UserPersonalityService.class);
    @Autowired
    private PersonalAssessmentQuestionService personalAssessmentQuestionService = Mockito.mock(PersonalAssessmentQuestionService.class);
    @Autowired
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);
    @Rule
    private ExpectedException thrown = ExpectedException.none();

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);

    @Test
    public void getPersonalAssessment_whenInputIsValid_thenReturnSuccess() {
        mockitoGetPersonalAssessment();
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), unitService.getPersonalAssessment(1L, QoocoApiConstants.LOCALE_ZH_CN, authentication).getCode());
    }

    @Test
    public void getQuestions_whenPersonalAssessmentIdIsNull_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        unitService.getQuestions(null, QoocoApiConstants.LOCALE_ZH_CN, 1L);
    }

    @Test
    public void getQuestions_whenNotFoundPersonalAssessmentId_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        Long id = 1L;
        mockitoGetQuestions(id);
        Mockito.when(personalAssessmentService.findById(id)).thenReturn(null);
        unitService.getQuestions(id, QoocoApiConstants.LOCALE_ZH_CN, 1L);
    }

    @Test
    public void getQuestions_whenInputsAreValid_thenReturnSuccess() {
        Long id = 1L;
        mockitoGetQuestions(id);
        unitService.getQuestions(id, QoocoApiConstants.LOCALE_ZH_CN, 1L);
    }

    @Test
    public void getPersonalTestResult_whenNotFoundPersonalAssessment_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        Long id = 1L;
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(personalAssessmentService.findById(id)).thenReturn(null);
        unitService.getPersonalTestResult(1L, QoocoApiConstants.LOCALE_ZH_CN, id, authentication);
    }

    @Test
    public void getPersonalTestResult_whenInputsAreValid_thenReturnSuccess() {
        Long id = 1L;
        mockitoGetPersonalTestResult(id);
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), unitService.getPersonalTestResult(1L, QoocoApiConstants.LOCALE_ZH_CN, id, authentication).getCode());
    }

    /*==================================== Prepare for testing ==================================================*/
    private void mockitoGetPersonalAssessment() {
        Long userId = 1L;
        Mockito.when(personalAssessmentService.getActivePersonalAssessment()).thenReturn(Lists.newArrayList(new PersonalAssessment(1L)));
        Mockito.when(userPersonalityService.getPersonalAssessmentIdByUserProfileId(userId)).thenReturn(Lists.newArrayList(1L));
    }

    private void mockitoGetQuestions(Long id) {
        Mockito.when(personalAssessmentService.findById(id)).thenReturn(new PersonalAssessment(id));
        Mockito.when(personalAssessmentQuestionService.getByPersonalAssessmentId(id))
                .thenReturn(Lists.newArrayList(new PersonalAssessmentQuestion(1L)));
        Mockito.when(userPersonalityService.countPersonalAssessmentByUserProfileId(1L, id)).thenReturn(1);
    }

    private void mockitoGetPersonalTestResult(Long id) {
        Mockito.when(personalAssessmentService.findById(id)).thenReturn(new PersonalAssessment(id));
        Mockito.when(userPersonalityService.getByUserProfileIdAndPersonalAssessmentId(1L, id))
                .thenReturn(Lists.newArrayList(new UserPersonality(1L)));
    }
}