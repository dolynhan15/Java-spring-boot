package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.CurriculumVitaeJob;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 1:49 PM
*/
@Repository
@Transactional
public interface CurriculumVitaeJobRepository extends Boot2JpaRepository<CurriculumVitaeJob, Long> {

    @Modifying
    @Query("DELETE FROM CurriculumVitaeJob c WHERE c.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId AND c.job.jobId IN :lstJobId")
    void deleteByUserCurriculumVitaeIdExceptJobIds(@Param("userCurriculumVitaeId") long userCurriculumVitaeId, @Param("lstJobId") int[] lstJobId);

    @Modifying
    @Query("DELETE FROM CurriculumVitaeJob c WHERE c.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    void deleteByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") long userCurriculumVitaeId);

    List<CurriculumVitaeJob> findAllByJobJobId(Integer jobId);

    @Query("SELECT c FROM CurriculumVitaeJob c WHERE c.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    List<CurriculumVitaeJob> findByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") long userCurriculumVitaeId);

    @Query("SELECT c FROM CurriculumVitaeJob c WHERE c.job.jobId in :jobIds")
    List<CurriculumVitaeJob> findAllByJobJobId(@Param("jobIds") List<Integer> jobIds);

    @Query("SELECT c FROM CurriculumVitaeJob c WHERE c.userCurriculumVitae.curriculumVitaeId IN :userCurriculumVitaeIds")
    List<CurriculumVitaeJob> findByUserCurriculumVitaeIds(@Param("userCurriculumVitaeIds") List<Long> userCurriculumVitaeIds);
}
