package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserDesiredHour;
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
public interface UserDesiredHourRepository extends Boot2JpaRepository<UserDesiredHour, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserDesiredHour u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId AND u.workingHour.workingHourId IN :lstWorkingHourId")
    void deleteByUserCurriculumVitaeIdExceptWorkingHourIds(@Param("userCurriculumVitaeId") long userCurriculumVitaeId, @Param("lstWorkingHourId") int[] lstWorkingHourId);
    @Transactional
    @Modifying
    @Query("DELETE FROM UserDesiredHour u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    void deleteByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") long userCurriculumVitaeId);

    @Query("SELECT u FROM UserDesiredHour u WHERE u.userCurriculumVitae.curriculumVitaeId = :userCurriculumVitaeId")
    List<UserDesiredHour> findByUserCurriculumVitaeId(@Param("userCurriculumVitaeId") Long userCurriculumVitaeId);

    @Query("SELECT u FROM UserDesiredHour u WHERE u.userCurriculumVitae.curriculumVitaeId IN :userCurriculumVitaeIds")
    List<UserDesiredHour> findByUserCurriculumVitaeIds(@Param("userCurriculumVitaeIds") List<Long> userCurriculumVitaeIds);
}
