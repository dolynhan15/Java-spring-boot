package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Job;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 10:07 AM
*/
@Repository
public interface JobRepository extends Boot2JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE j.id = :id AND j.isDeleted = false ")
    Job findValidById(@Param("id") Long id);

    @Query("SELECT j FROM Job j WHERE j.jobId IN :lstJobId AND j.isDeleted = false ORDER BY j.jobName ASC")
    List<Job> findByIds(@Param("lstJobId") long[] lstJobId);

    @Query("SELECT j FROM Job j WHERE j.company.companyId =:companyId OR j.company.companyId is null AND j.isDeleted = false ORDER BY j.jobName ASC")
    List<Job> findByCompanyCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT j FROM Job j WHERE (j.company.companyId =:companyId OR j.company IS NULL) AND LOWER(j.jobName) like :jobName AND j.isDeleted = false ORDER BY j.jobName ASC")
    List<Job> findByCompanyCompanyIdAndJobName(@Param("companyId") Long companyId, @Param("jobName") String jobName);

    @Query("SELECT j FROM Job j WHERE j.company IS NULL AND j.isDeleted = false ORDER BY j.jobName ASC")
    List<Job> findByCompanyIsNull();
}
