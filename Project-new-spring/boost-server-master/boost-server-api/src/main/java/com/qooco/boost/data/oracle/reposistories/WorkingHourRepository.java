package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.WorkingHour;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 10:45 AM
*/
@Repository
public interface WorkingHourRepository extends Boot2JpaRepository<WorkingHour, Long> {
    @Query("SELECT w FROM WorkingHour w WHERE w.workingHourId IN :lstWorkingHourId")
    List<WorkingHour> findByIds(@Param("lstWorkingHourId") long[] lstWorkingHourId);

    List<WorkingHour> findByWorkingType(boolean workingType);

    @Query("SELECT COUNT(l.workingHourId) FROM WorkingHour l WHERE l.workingHourId IN :ids")
    int countByIds(@Param("ids") Long[] ids);

}
