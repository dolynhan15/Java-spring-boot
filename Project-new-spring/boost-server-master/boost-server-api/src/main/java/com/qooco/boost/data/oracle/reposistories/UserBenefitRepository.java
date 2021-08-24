package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserBenefit;
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
public interface UserBenefitRepository extends Boot2JpaRepository<UserBenefit, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserBenefit u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId AND u.benefit.benefitId IN :benefitIds")
    void deleteByUserCurriculumVitaeIdExceptBenefitIds(@Param("userCurriculumVitaeId") long userCurriculumVitaeId, @Param("benefitIds") int[] benefitIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserBenefit u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    void deleteByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") long userCurriculumVitaeId);

    @Query("SELECT u FROM UserBenefit u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    List<UserBenefit> findByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") Long userCurriculumVitaeId);

    @Query("SELECT u FROM UserBenefit u WHERE u.userCurriculumVitae.curriculumVitaeId IN :userCurriculumVitaeIds")
    List<UserBenefit> findByUserCurriculumVitaeIds(@Param("userCurriculumVitaeIds") List<Long> userCurriculumVitaeIds);
}
