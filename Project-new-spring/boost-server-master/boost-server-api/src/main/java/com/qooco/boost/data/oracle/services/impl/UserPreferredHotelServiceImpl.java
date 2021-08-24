package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserPreferredHotel;
import com.qooco.boost.data.oracle.reposistories.UserPreferredHotelRepository;
import com.qooco.boost.data.oracle.services.UserPreferredHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferredHotelServiceImpl implements UserPreferredHotelService {

    @Autowired
    private UserPreferredHotelRepository repository;

    @Override
    public List<UserPreferredHotel> findByUserCurriculumVitaeId(long userCurriculumVitaeId) {
        return repository.findByUserCurriculumVitae_CurriculumVitaeId(userCurriculumVitaeId);
    }

    @Override
    public List<UserPreferredHotel> save(List<UserPreferredHotel> lstUserPreferredHotel) {
        return Lists.newArrayList(repository.saveAll(lstUserPreferredHotel));
    }

    @Override
    public UserPreferredHotel save(UserPreferredHotel userDesiredHour) {
        return repository.save(userDesiredHour);
    }

    @Override
    public void deleteByUserCurriculumVitaeId(long curriculumVitaeId) {
        repository.deleteByUserCurriculumVitae_CurriculumVitaeId(curriculumVitaeId);
    }

    @Override
    public List<UserPreferredHotel> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds) {
        return repository.findAllByUserCurriculumVitae_CurriculumVitaeId(userCurriculumVitaeIds);
    }
}
