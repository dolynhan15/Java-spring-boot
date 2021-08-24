package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.CurriculumVitaeJob;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 11:30 AM
*/
public interface CurriculumVitaeJobService {

    List<CurriculumVitaeJob> findByUserCurriculumVitaeId(long userCurriculumVitaeId);

    List<CurriculumVitaeJob> save(List<CurriculumVitaeJob> lstCurriculumVitaeJob);

    CurriculumVitaeJob save(CurriculumVitaeJob curriculumVitaeJob);

    void deleteByUserCurriculumVitaeId(long curriculumVitaeId);

    void deleteByUserCurriculumVitaeIdExceptJobIds(long userCurriculumVitaeId, int[] lstJobId);

    List<CurriculumVitaeJob> findByJobId(Integer jobId);

    List<CurriculumVitaeJob> findByJobIds(List<Integer> jobIds);

    Boolean exists(Long id);

    List<CurriculumVitaeJob> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds);
}
