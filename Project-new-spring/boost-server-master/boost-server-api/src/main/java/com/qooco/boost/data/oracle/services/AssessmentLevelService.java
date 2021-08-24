package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.AssessmentLevel;

import java.util.List;

public interface AssessmentLevelService {
    List<AssessmentLevel> findByIds(Long[] ids);

    List<AssessmentLevel> save(List<AssessmentLevel> assessmentLevels);

    List<AssessmentLevel> findByMappingIdAndScaleId(String mappingId, String scaleId);

    List<AssessmentLevel> findByScaleIds(List<String> scaleIds);
}
