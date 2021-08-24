package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserPreferredHotel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserPreferredHotelRepository extends Boot2JpaRepository<UserPreferredHotel, Long> {
    @Transactional
    @Modifying
    void deleteByUserCurriculumVitae_CurriculumVitaeId(@Param("userCvId") long userCvId);

    List<UserPreferredHotel> findByUserCurriculumVitae_CurriculumVitaeId(@Param("userCvId") Long userCvId);

    List<UserPreferredHotel> findAllByUserCurriculumVitae_CurriculumVitaeId(@Param("userCvIds") List<Long> userCvIds);
}
