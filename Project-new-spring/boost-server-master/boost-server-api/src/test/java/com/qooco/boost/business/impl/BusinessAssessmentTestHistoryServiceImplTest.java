package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessAssessmentTestHistoryService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.mongo.embedded.LevelEmbedded;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.data.mongo.entities.LevelTestScaleDoc;
import com.qooco.boost.data.mongo.services.AssessmentTestHistoryDocService;
import com.qooco.boost.data.mongo.services.LevelTestScaleDocService;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
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
import org.springframework.security.core.Authentication;

import java.util.List;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.security.*"})
public class BusinessAssessmentTestHistoryServiceImplTest {
    @InjectMocks
    private BusinessAssessmentTestHistoryService assessmentTestHistoryService = new BusinessAssessmentTestHistoryServiceImpl();
    @Mock
    private AssessmentTestHistoryDocService historyDocService = Mockito.mock(AssessmentTestHistoryDocService.class);
    @Mock
    private LevelTestScaleDocService levelTestScaleDocService = Mockito.mock(LevelTestScaleDocService.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);

    @Test
    public void getTestHistoryByAssessment_whenAssessmentIdIsNull_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        assessmentTestHistoryService.getTestHistoryByAssessment(1L, null, "Asia/Ho_Chi_Minh", authentication);
    }

    @Test
    public void getTestHistoryByAssessment_whenNotFoundTestHistoryDocs_thenReturnSuccess() {
        mockito();
        Mockito.when(historyDocService.getTestHistoryByAssessment(1L, 1L)).thenReturn(null);
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), assessmentTestHistoryService.getTestHistoryByAssessment(1L, 1L, "Asia/Ho_Chi_Minh", authentication).getCode());
    }

    @Test
    public void getTestHistoryByAssessment_whenInputIsValid_thenReturnSuccess() {
        mockito();
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), assessmentTestHistoryService.getTestHistoryByAssessment(1L, 1L, "Asia/Ho_Chi_Minh", authentication).getCode());
    }

    private void mockito() {
        Long userProfileId = 1L;
        Long assessmentId = 1L;
        List<AssessmentTestHistoryDoc> testHistoryDocs = Lists.newArrayList(new AssessmentTestHistoryDoc("BC"));
        List<LevelEmbedded> levels = Lists.newArrayList(new LevelEmbedded("1", "A1", "A1"));
        LevelTestScaleDoc levelTestScaleDoc = new LevelTestScaleDoc(levels);
        Mockito.when(historyDocService.getTestHistoryByAssessment(userProfileId, assessmentId))
                .thenReturn(testHistoryDocs);
        Mockito.when(levelTestScaleDocService.findById(testHistoryDocs.get(0).getScaleId()))
                .thenReturn(levelTestScaleDoc);
    }
}