package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.google.inject.internal.util.ImmutableList;
import com.qooco.boost.data.oracle.entities.VacancyCandidate;
import com.qooco.boost.data.oracle.reposistories.VacancyCandidateRepository;
import com.qooco.boost.data.oracle.services.VacancyCandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class VacancyCandidateServiceImpl implements VacancyCandidateService {
    @Autowired
    private VacancyCandidateRepository repository;

    @Override
    public VacancyCandidate save(VacancyCandidate vacancyCandidate) {
        return repository.save(vacancyCandidate);
    }

    @Override
    public List<VacancyCandidate> save(List<VacancyCandidate> vacancyCandidates) {
        return Lists.newArrayList(repository.saveAll(vacancyCandidates));
    }

    @Override
    public Page<VacancyCandidate> findByVacancyAndStatus(Long vacancyId, List<Integer> statuses, int page, int size) {
        return repository.findByVacancyAndStatus(ImmutableList.of(vacancyId), statuses, PageRequest.of(page, size));
    }

    @Override
    public List<VacancyCandidate> findByVacancyAndStatus(Long vacancyId, List<Integer> statuses) {
        return repository.findByVacancyAndStatus(ImmutableList.of(vacancyId), statuses, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    @Override
    public List<VacancyCandidate> findByVacancyAndStatus(List<Long> vacancyIds, List<Integer> statuses) {
        return repository.findByVacancyAndStatus(vacancyIds, statuses, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    @Override
    public VacancyCandidate findByVacancyAndUserProfileAndStatus(Long vacancyId, Long userProfileId, List<Integer> status) {
        return repository.findByVacancyAndUserProfileAndStatus(vacancyId, userProfileId, status);
    }

    @Override
    public List<VacancyCandidate> findByVacancyAndStatusAndDuration(List<Long> vacancyIds, List<Integer> statuses, Date startDate, Date endDate) {
        return repository.findAllByVacancyIdAndStatusAndCreatedDateBetween(vacancyIds, statuses, startDate, endDate);
    }
}
