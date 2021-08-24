package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserPreferredHotel;

import java.util.List;

public interface UserPreferredHotelService {
    List<UserPreferredHotel> findByUserCurriculumVitaeId(long userCurriculumVitaeId);

    List<UserPreferredHotel> save(List<UserPreferredHotel> lstUserPreferredHotel);

    UserPreferredHotel save(UserPreferredHotel userPreferredHotel);

    void deleteByUserCurriculumVitaeId(long curriculumVitaeId);

    List<UserPreferredHotel> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds);

}
