package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.AssessmentLevel;
import com.qooco.boost.data.oracle.reposistories.AssessmentLevelRepository;
import com.qooco.boost.data.oracle.services.AssessmentLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/10/2018 - 10:58 AM
 */
@Service
public class AssessmentLevelServiceImpl implements AssessmentLevelService {
    @Autowired
    private AssessmentLevelRepository repository;
    @Override
    public List<AssessmentLevel> findByIds(Long[] ids) {
        if(null != ids && ids.length > 0) {
            return repository.findByIds(ids);
        }
        return new ArrayList<>();
    }

    @Override
    public List<AssessmentLevel> save(List<AssessmentLevel> assessmentLevels) {
        return Lists.newArrayList(repository.saveAll(assessmentLevels));
    }

    @Override
    public List<AssessmentLevel> findByMappingIdAndScaleId(String mappingId, String scaleId) {
        return repository.findByMappingIdAndScaleId(mappingId, scaleId);
    }

    @Override
    public List<AssessmentLevel> findByScaleIds(List<String> scaleIds) {
        return repository.findByScaleIds(scaleIds);
    }
}
