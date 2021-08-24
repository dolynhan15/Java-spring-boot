package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.SoftSkill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 6/20/2018 - 11:14 AM
*/
@Repository
public interface SoftSkillsRepository extends Boot2JpaRepository<SoftSkill, Long> {

    @Query("SELECT s FROM SoftSkill s WHERE s.softSkillId IN :ids ORDER BY s.name asc")
    List<SoftSkill> findByIds(@Param("ids") long[] ids);

    @Query("SELECT COUNT(s.softSkillId) FROM SoftSkill s WHERE s.softSkillId IN :ids")
    int countByIds(@Param("ids") Long[] ids);
}
