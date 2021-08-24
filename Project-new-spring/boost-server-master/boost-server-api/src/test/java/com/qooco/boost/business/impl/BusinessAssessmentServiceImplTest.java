package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessAssessmentService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.AssessmentType;
import com.qooco.boost.data.mongo.services.AssessmentTestHistoryDocService;
import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.entities.UserQualification;
import com.qooco.boost.data.oracle.services.AssessmentService;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.data.oracle.services.UserQualificationService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.assessment.AssessmentDTO;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.StringUtil;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 8/6/2018 - 5:36 PM
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class BusinessAssessmentServiceImplTest extends BaseUserService{
    @InjectMocks
    private BusinessAssessmentService businessAssessmentService = new BusinessAssessmentServiceImpl();
    @Mock
    private AssessmentService assessmentService = Mockito.mock(AssessmentService.class);
    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);
    @Mock
    private UserQualificationService userQualificationService = Mockito.mock(UserQualificationService.class);
    @Mock
    private AssessmentTestHistoryDocService testHistoryDocService = Mockito.mock(AssessmentTestHistoryDocService.class);
    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);
    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getAssessments_whenPageAndSizeAreNegativeValues_thenReturnInvalidPagination() {
        thrown.expect(InvalidParamException.class);
        businessAssessmentService.getAssessments(-1, -1);
    }

    @Test
    public void getAssessments_whenPageAndSizeAreValidValues_thenReturnBenefitResp() {
        int page = 1;
        int size = 10;
        List<Assessment> assessmentList = new ArrayList<>();
//        assessmentList.add(new Assessment(1, 3, "Upsell English Assessment"));
//        assessmentList.add(new Assessment(2, 3, "Hospitality English Assessment"));

        Page<Assessment> assessmentPage = new PageImpl<>(assessmentList);
        Mockito.when(assessmentService.getAssessments(page, size)).thenReturn(assessmentPage);

        List<AssessmentDTO> assessmentDTOList = assessmentPage.getContent().stream().map(a -> new AssessmentDTO(a, "")).collect(Collectors.toList());
        PagedResult<AssessmentDTO> result = new PagedResult<>(assessmentDTOList, page, assessmentPage.getSize(), assessmentPage.getTotalPages(), assessmentPage.getTotalElements(),
                assessmentPage.hasNext(), assessmentPage.hasPrevious());

        BaseResp actualResponse = businessAssessmentService.getAssessments(page, size);
        Assert.assertEquals(result.getSize(), ((PagedResult) actualResponse.getData()).getSize());
    }

    @Test
    public void deleteAssessment_whenInputIsValid_thenReturnSuccess() {
        mockitoDeleteAssessment();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAssessmentService.deleteAssessment(1L).getCode());
    }

    @Test
    public void getAssessmentsByType_whenPageAndSizeLessThanOne_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        businessAssessmentService.getAssessmentsByType(-1, -1, null);
    }

    @Test
    public void getAssessmentsByType_whenInputsAreValid_thenReturnSuccess() {
        mockitoGetAssessmentByType();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAssessmentService.getAssessmentsByType(1, 10, null).getCode());
    }

    @Test
    public void getUserQualification_whenNotFoundUser_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        mockitoGetUserQualification();
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenThrow(EntityNotFoundException.class);
        businessAssessmentService.getUserQualification(1L, "BC", true, authentication);
    }

    @Test
    public void getUserQualification_whenIsHomepageAndScaleIdIsValid_thenReturnSuccess() {
        mockitoGetUserQualification();
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAssessmentService.getUserQualification(1L, "BC", true, authentication).getCode());
    }

    @Test
    public void getUserQualification_whenIsHomepageAndScaleIdIsNull_thenReturnSuccess() {
        mockitoGetUserQualification();
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAssessmentService.getUserQualification(1L, "", true, authentication).getCode());
    }

    @Test
    public void getUserQualification_whenIsNotHomepageAndScaleIdIsValid_thenReturnSuccess() {
        mockitoGetUserQualification();
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAssessmentService.getUserQualification(1L, "BC", false, authentication).getCode());
    }

    @Test
    public void getUserQualification_whenIsNotHomepageAndScaleIdIsNull_thenReturnSuccess() {
        mockitoGetUserQualification();
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(new UserProfile(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAssessmentService.getUserQualification(1L, "", false, authentication).getCode());
    }

    @Test
    public void syncDataOfEachUser_whenNotFoundUser_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        Long userProfileId = 1L;
        mockitoSyncDataOfEachUser();
        Mockito.when(userProfileService.isExist(userProfileId)).thenReturn(false);
        businessAssessmentService.syncDataOfEachUser(userProfileId);
    }

    @Test
    public void syncDataOfEachUser_whenInputIsValid_thenReturnSuccess() {
        mockitoSyncDataOfEachUser();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAssessmentService.syncDataOfEachUser(1L).getCode());
    }

    @Test
    public void syncDataFromQooco_whenIsNotRootAdmin_thenReturnErrorException() {
        thrown.expect(NoPermissionException.class);
        mockitoSyncDataFromQooco();
        AuthenticatedUser authenticatedUser = initAuthenticatedUser();
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(authenticatedUser.getId())).thenReturn(null);
        businessAssessmentService.syncDataFromQooco(initAuthentication());
    }

    @Test
    public void syncDataFromQooco_whenInputIsValid_thenReturnSuccess() {
        mockitoSyncDataFromQooco();
        businessAssessmentService.syncDataFromQooco(initAuthentication());
    }


    /*=========================================== Prepare for Test ================================================= */

    private void mockitoDeleteAssessment() {
        Long id = 1L;
        Mockito.doNothing().when(assessmentService).deleteAssessment(id);
    }

    private void mockitoGetAssessmentByType() {
        int page = 1;
        int size = 10;
        Integer type = AssessmentType.ALL.getCode();
        Page<Assessment> assessmentPage = new PageImpl<>(Lists.newArrayList(new Assessment(1L)), PageRequest.of(page, size), 1);
        Mockito.when(assessmentService.getAssessmentsByType(page, size, type)).thenReturn(assessmentPage);
    }

    private void mockitoGetUserQualification() {
        Long userProfileId = 1L;
        String scaleId = "BC";
        List<UserQualification> qualifications = Lists.newArrayList(new UserQualification(1L, "BC", 1L));
        Mockito.when(userProfileService.isExist(userProfileId)).thenReturn(true);
        Mockito.when(userQualificationService.findByUserProfileIdAndScaleIdForHomePage(userProfileId, scaleId)).thenReturn(qualifications);
        Mockito.when(userQualificationService.findByUserProfileIdForHomePage(userProfileId)).thenReturn(qualifications);
        Mockito.when(userQualificationService.findByUserProfileIdAndScaleId(userProfileId, scaleId)).thenReturn(qualifications);
        Mockito.when(userQualificationService.findByUserProfileId(userProfileId)).thenReturn(qualifications);
        Mockito.when(testHistoryDocService.countByUserProfileIdAndScaleIdAndAssessmentId(userProfileId,
                qualifications.get(0).getScaleId(), qualifications.get(0).getAssessmentId(), true)).thenReturn(3);
    }

    private void mockitoSyncDataOfEachUser() {
        Long userProfileId = 1L;
        Mockito.when(userProfileService.isExist(userProfileId)).thenReturn(true);
        Mockito.doNothing().when(boostActorManager).syncDataOfUserInMongo(userProfileId.toString());
    }

    private void mockitoSyncDataFromQooco() {
        AuthenticatedUser authenticatedUser = initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(authenticatedUser.getId())).thenReturn(new UserProfile(1L));
        Mockito.doNothing().when(boostActorManager).syncDataFromQooco(StringUtil.append(QoocoApiConstants.SYNC_LEVEL_TESTS, QoocoApiConstants.SYNC_FORCE));
    }
}