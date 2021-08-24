package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.enumeration.AssessmentType;
import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.data.oracle.reposistories.AssessmentRepository;
import com.qooco.boost.data.oracle.services.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRepository repository;

    @Override
    public Assessment findById(Long assessmentId) {
        return repository.findValidById(assessmentId);
    }

    @Override
    public List<Assessment> findByIds(List<Long> ids) {
        return repository.findByIds(ids);
    }

    @Override
    public List<Assessment> getAssessments() {
        return repository.findAssessments();
    }

    @Override
    public Page<Assessment> getAssessments(int page, int size) {
        if (page < 0 || size <= 0) {
            return repository.findAll(PageRequest.of(0, Integer.MAX_VALUE));
        }
        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Page<Assessment> getAssessmentsByType(int page, int size, Integer type) {
        if (page < 0 || size <= 0) {
            if (type == AssessmentType.ALL.getCode()) {
                return repository.findAll(PageRequest.of(0, Integer.MAX_VALUE));
            } else {
                return repository.findAll(PageRequest.of(0, Integer.MAX_VALUE), type);
            }
        } else if (type == AssessmentType.ALL.getCode()) {
            return repository.findAll(PageRequest.of(page, size));
        }
        return repository.findAll(PageRequest.of(page, size), type);
    }

    @Override
    public List<Assessment> getAssessmentsExceptIds(List<Long> assessmentIds) {
        return repository.findAssessmentsExceptIds(assessmentIds);
    }

    @Override
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public boolean exists(Long[] ids) {
        int size = repository.countByIds(ids);
        return size == ids.length;
    }

    @Override
    public long findLatestUpdateDate() {
        Date latestUpdate = repository.getLatestUpdate();
        if (Objects.nonNull(latestUpdate)) {
            return latestUpdate.getTime();
        }
        return 0;
    }

    @Override
    public Assessment save(Assessment assessment) {
        return repository.save(assessment);
    }

    @Override
    public List<Assessment> save(List<Assessment> assessments) {
        return Lists.newArrayList(repository.saveAll(assessments));
    }

    @Override
    public List<Assessment> getNoneLevelAssessment() {
        return repository.getNoneLevelAssessment();
    }

    @Override
    public void deleteAssessment(Long id) {
        repository.setIsDeleteById(id, true);
    }
}
