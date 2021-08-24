package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.reposistories.UserCurriculumVitaeRepository;
import com.qooco.boost.data.oracle.services.UserCurriculumVitaeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class UserCurriculumVitaeServiceImpl implements UserCurriculumVitaeService {

    @Autowired
    private UserCurriculumVitaeRepository repository;

    @Override
    public UserCurriculumVitae save(UserCurriculumVitae userCurriculumVitae) {
        return repository.save(userCurriculumVitae);
    }

    @Override
    public List<UserCurriculumVitae> save(List<UserCurriculumVitae> userCurriculumVitaes) {
        return repository.saveAll(userCurriculumVitaes);
    }

    @Override
    public UserCurriculumVitae findByUserProfile(UserProfile userProfile) {
        return repository.findByUserProfileId(userProfile.getUserProfileId());
    }

    @Override
    public Boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public List<UserCurriculumVitae> findByUserIds(List<Long> ids) {
        return CollectionUtils.isEmpty(ids) ? ImmutableList.of() : repository.findByUserIds(ids);
    }

    @Override
    public List<BigDecimal> findIdGreaterThan(Long id, int limit) {
        return repository.findIdGreaterThan(id, limit);
    }

    @Override
    public Page<UserCurriculumVitae> findByUserIdsWithPagination(List<Long> userCVIdCandidates, int page, int size) {
        return repository.findByUserIdsWithPagination(userCVIdCandidates, PageRequest.of(page, size));
    }

    @Override
    public UserCurriculumVitae findById(Long id) {
        return repository.findByCurriculumVitaeId(id);
    }

    @Override
    public List<UserCurriculumVitae> findByIds(Collection<Long> ids) {
        return CollectionUtils.isEmpty(ids) ? ImmutableList.of() : repository.findByCurriculumVitaeIds(ids);
    }
}
