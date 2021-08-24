package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.CurriculumVitaeJob;
import com.qooco.boost.data.oracle.reposistories.CurriculumVitaeJobRepository;
import com.qooco.boost.data.oracle.services.CurriculumVitaeJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 1:49 PM
*/
@Service
public class CurriculumVitaeJobServiceImpl implements CurriculumVitaeJobService {

    @Autowired
    private CurriculumVitaeJobRepository curriculumVitaeJobRepository;

    @Override
    public List<CurriculumVitaeJob> findByUserCurriculumVitaeId(long userCurriculumVitaeId) {
        return curriculumVitaeJobRepository.findByUserCurriculumVitaeId(userCurriculumVitaeId);
    }

    @Override
    public List<CurriculumVitaeJob> save(List<CurriculumVitaeJob> lstCurriculumVitaeJob) {
        return Lists.newArrayList(curriculumVitaeJobRepository.saveAll(lstCurriculumVitaeJob));
    }

    @Override
    public CurriculumVitaeJob save(CurriculumVitaeJob curriculumVitaeJob) {
        return curriculumVitaeJobRepository.save(curriculumVitaeJob);
    }

    @Override
    public void deleteByUserCurriculumVitaeId(long curriculumVitaeId) {
        curriculumVitaeJobRepository.deleteByUserCurriculumVitaeId(curriculumVitaeId);
    }

    @Override
    public void deleteByUserCurriculumVitaeIdExceptJobIds(long userCurriculumVitaeId, int[] lstJobId) {
        curriculumVitaeJobRepository.deleteByUserCurriculumVitaeIdExceptJobIds(userCurriculumVitaeId, lstJobId);
    }

    @Override
    public List<CurriculumVitaeJob> findByJobId(Integer jobId) {
        return curriculumVitaeJobRepository.findAllByJobJobId(jobId);
    }

    @Override
    public List<CurriculumVitaeJob> findByJobIds(List<Integer> jobIds) {
        if (Objects.nonNull(jobIds) && !jobIds.isEmpty()) {
            return curriculumVitaeJobRepository.findAllByJobJobId(jobIds);
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean exists(Long id) {
        return curriculumVitaeJobRepository.existsById(id);
    }

    @Override
    public List<CurriculumVitaeJob> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds) {
        return curriculumVitaeJobRepository.findByUserCurriculumVitaeIds(userCurriculumVitaeIds);
    }
}
