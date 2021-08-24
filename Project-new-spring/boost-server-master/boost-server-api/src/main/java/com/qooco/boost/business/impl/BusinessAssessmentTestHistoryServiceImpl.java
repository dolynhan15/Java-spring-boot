package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessAssessmentTestHistoryService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.data.mongo.entities.LevelTestScaleDoc;
import com.qooco.boost.data.mongo.services.AssessmentTestHistoryDocService;
import com.qooco.boost.data.mongo.services.LevelTestScaleDocService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.assessment.AssessmentHistoryDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BusinessAssessmentTestHistoryServiceImpl implements BusinessAssessmentTestHistoryService {
    @Autowired
    private AssessmentTestHistoryDocService historyDocService;
    @Autowired
    private LevelTestScaleDocService levelTestScaleDocService;
    @Autowired
    private BusinessValidatorService businessValidatorService;

    @Value(ApplicationConstant.BOOST_PATA_CERTIFICATION_PERIOD)
    private int certificationPeriod;

    @Override
    public BaseResp getTestHistoryByAssessment(Long userProfileId, Long assessmentId, String timeZone, Authentication authentication) {
        if (Objects.isNull(assessmentId)) {
            throw new InvalidParamException(ResponseStatus.ASSESSMENT_ID_NOT_EXIST);
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        long userId = authenticatedUser.getId();
        if (Objects.nonNull(userProfileId)) {
            businessValidatorService.checkExistsUserProfile(userProfileId);
            userId = userProfileId;
        }
        List<AssessmentTestHistoryDoc> testHistoryDocs = historyDocService.getTestHistoryByAssessment(userId, assessmentId);
        if (CollectionUtils.isNotEmpty(testHistoryDocs)) {
            LevelTestScaleDoc levelTestScaleDoc = levelTestScaleDocService.findById(testHistoryDocs.get(0).getScaleId());
            AssessmentHistoryDTO historyDTO = new AssessmentHistoryDTO(testHistoryDocs, levelTestScaleDoc.getLevels(), certificationPeriod);
            return new BaseResp<>(historyDTO);
        }
        return new BaseResp<>(new AssessmentHistoryDTO());
    }
}
