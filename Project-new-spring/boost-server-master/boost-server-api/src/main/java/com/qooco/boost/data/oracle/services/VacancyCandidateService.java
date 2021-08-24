package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.VacancyCandidate;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface VacancyCandidateService {
    VacancyCandidate save(VacancyCandidate vacancyCandidate);

    List<VacancyCandidate> save(List<VacancyCandidate> vacancyCandidates);

    List<VacancyCandidate> findByVacancyAndStatus(List<Long> vacancyIds, List<Integer> statuses);

    Page<VacancyCandidate> findByVacancyAndStatus(Long vacancyId, List<Integer> statuses, int page, int size);

    List<VacancyCandidate> findByVacancyAndStatus(Long vacancyId, List<Integer> statuses);

    VacancyCandidate findByVacancyAndUserProfileAndStatus(Long vacancyId, Long userProfileId, List<Integer> status);

    List<VacancyCandidate> findByVacancyAndStatusAndDuration(List<Long> vacancyIds, List<Integer> statuses, Date startDate, Date endDate);
}
