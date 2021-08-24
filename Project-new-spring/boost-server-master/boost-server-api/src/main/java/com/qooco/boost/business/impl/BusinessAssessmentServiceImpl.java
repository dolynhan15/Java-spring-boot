package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessAssessmentService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.ApplicationConstant;
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
import com.qooco.boost.models.dto.assessment.QualificationDTO;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class BusinessAssessmentServiceImpl implements BusinessAssessmentService {
    protected Logger logger = LogManager.getLogger(BusinessAssessmentServiceImpl.class);
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserQualificationService userQualificationService;
    @Autowired
    private AssessmentTestHistoryDocService testHistoryDocService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private BusinessValidatorService businessValidatorService;

    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";

    @Value(ApplicationConstant.BOOST_PATA_CERTIFICATION_PERIOD)
    private int certificationPeriod;

    @Override
    public BaseResp getAssessments(int page, int size) {
        if (page < 0 || size < 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_PAGINATION);
        }

        Page<Assessment> assessmentPage = assessmentService.getAssessments(page, size);
        return new BaseResp<>(getAssessmentDTOPagedResult(assessmentPage, page));
    }

    @Override
    public BaseResp deleteAssessment(Long id) {
        assessmentService.deleteAssessment(id);
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp getAssessmentsByType(int page, int size, Integer type) {
        if (page < 0 || size < 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_PAGINATION);
        }

        if (Objects.isNull(type)) {
            type = AssessmentType.ALL.getCode();
        }
        Page<Assessment> assessmentPage = assessmentService.getAssessmentsByType(page, size, type);
        return new BaseResp<>(getAssessmentDTOPagedResult(assessmentPage, page));
    }

    @Override
    public BaseResp getUserQualification(Long userProfileId, String scaleId, boolean isHomepage, Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        long userId = authenticatedUser.getId();
        if (nonNull(userProfileId)) {
            businessValidatorService.checkExistsUserProfile(userId);
            userId = userProfileId;
        }
        List<UserQualification> qualifications;
        List<QualificationDTO> result = new ArrayList<>();
        if (isHomepage) {
            qualifications = StringUtils.isNotBlank(scaleId) ?
                    userQualificationService.findByUserProfileIdAndScaleIdForHomePage(userId, scaleId) :
                    userQualificationService.findByUserProfileIdForHomePage(userId);
            if (isNotEmpty(qualifications)) {
                result = qualifications.stream().map(qlf -> new QualificationDTO(qlf, certificationPeriod)).collect(Collectors.toList());
            }

        } else {
            qualifications = StringUtils.isNotBlank(scaleId) ?
                    userQualificationService.findByUserProfileIdAndScaleId(userId, scaleId) :
                    userQualificationService.findByUserProfileId(userId);
            result = getQualificationAttempts(userId, qualifications, certificationPeriod);
        }

        return new BaseResp<>(result);
    }

    private PagedResult<AssessmentDTO> getAssessmentDTOPagedResult(@NotNull Page<Assessment> assessmentPage, int page) {
        List<AssessmentDTO> assessmentDTOList = new ArrayList<>();
        if (isNotEmpty(assessmentPage.getContent())) {
            assessmentDTOList = assessmentPage.getContent().stream()
                    .filter(a -> isNotEmpty(a.getAssessmentLevels()))
                    .map(a -> new AssessmentDTO(a, qoocoDomainPath)).collect(Collectors.toList());
        }

        return new PagedResult<>(
                assessmentDTOList,
                page,
                assessmentPage.getSize(),
                assessmentPage.getTotalPages(),
                assessmentPage.getTotalElements(),
                assessmentPage.hasNext(),
                assessmentPage.hasPrevious());

    }

    private List<QualificationDTO> getQualificationAttempts(Long userProfileId, List<UserQualification> qualifications, int expiredDay) {
        List<QualificationDTO> result = new ArrayList<>();
        if (isNotEmpty(qualifications)) {
            for (UserQualification qualification : qualifications) {
                int countAssessmentHistory
                        = testHistoryDocService.countByUserProfileIdAndScaleIdAndAssessmentId(userProfileId,
                        qualification.getScaleId(), qualification.getAssessmentId(), true);
                result.add(new QualificationDTO(qualification, expiredDay, countAssessmentHistory));
            }
        }
        return result;
    }

    @Override
    public BaseResp syncDataOfEachUser(Long userProfileId) {
        boolean isExist = userProfileService.isExist(userProfileId);
        if (!isExist) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }
        boostActorManager.syncDataOfUserInMongo(userProfileId.toString());
        logger.info("syncDataOfEachUser : " + userProfileId);
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp syncDataFromQooco(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        UserProfile isRootAdmin = userProfileService.checkUserProfileIsRootAdmin(authenticatedUser.getId());
        if (Objects.isNull(isRootAdmin)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        boostActorManager.syncDataFromQooco(StringUtil.append(QoocoApiConstants.SYNC_LEVEL_TESTS, QoocoApiConstants.SYNC_FORCE));
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }
}
