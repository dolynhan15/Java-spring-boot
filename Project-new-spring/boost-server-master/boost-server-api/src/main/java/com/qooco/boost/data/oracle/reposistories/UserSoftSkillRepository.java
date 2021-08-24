package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserSoftSkill;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 4:48 PM
*/
@Repository
public interface UserSoftSkillRepository extends Boot2JpaRepository<UserSoftSkill, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserSoftSkill u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId AND u.softSkill.softSkillId IN :softSkillIds")
    void deleteByUserCurriculumVitaeIdExceptSoftSkillIds(@Param("userCurriculumVitaeId") long userCurriculumVitaeId, @Param("softSkillIds") int[] softSkillIds);
    @Transactional
    @Modifying
    @Query("DELETE FROM UserSoftSkill u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    void deleteByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") long userCurriculumVitaeId);

    @Query("SELECT u FROM UserSoftSkill u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    List<UserSoftSkill> findByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") Long userCurriculumVitaeId);

    @Query("SELECT u FROM UserSoftSkill u WHERE u.userCurriculumVitae.curriculumVitaeId IN :userCurriculumVitaeIds")
    List<UserSoftSkill> findByUserCurriculumVitaeIds(@Param("userCurriculumVitaeIds") List<Long> userCurriculumVitaeIds);
}
