package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Assessment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssessmentService {

    Assessment findById(Long assessmentId);

    List<Assessment> findByIds(List<Long> ids);

    List<Assessment> getAssessments();

    Page<Assessment> getAssessments(int page, int size);

    Page<Assessment> getAssessmentsByType(int page, int size, Integer type);

    List<Assessment> getAssessmentsExceptIds(List<Long> assessmentIds);

    boolean exists(Long assessmentId);

    boolean exists(Long[] ids);

    long findLatestUpdateDate();

    Assessment save(Assessment assessment);

    List<Assessment> save(List<Assessment> assessments);

    List<Assessment> getNoneLevelAssessment();

    void deleteAssessment(Long id);
}
