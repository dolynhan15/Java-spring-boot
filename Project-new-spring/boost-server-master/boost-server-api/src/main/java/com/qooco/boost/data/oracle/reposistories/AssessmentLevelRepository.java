package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.AssessmentLevel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/10/2018 - 10:59 AM
 */
@Repository
public interface AssessmentLevelRepository extends Boot2JpaRepository<AssessmentLevel, Long> {
    @Query("SELECT al FROM AssessmentLevel al WHERE al.id IN :ids")
    List<AssessmentLevel> findByIds(@Param("ids") Long[] ids);

    @Query("SELECT al FROM AssessmentLevel al WHERE al.mappingId = :mappingId AND al.scaleId = :scaleId AND al.isDeleted = false")
    List<AssessmentLevel> findByMappingIdAndScaleId(@Param("mappingId") String mappingId, @Param("scaleId") String scaleId);

    @Query("SELECT al FROM AssessmentLevel al WHERE al.scaleId IN :scaleIds AND al.isDeleted = false")
    List<AssessmentLevel> findByScaleIds(@Param("scaleIds") List<String> scaleIds);
}
