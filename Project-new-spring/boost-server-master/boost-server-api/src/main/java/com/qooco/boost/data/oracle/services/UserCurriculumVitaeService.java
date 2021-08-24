package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserProfile;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface UserCurriculumVitaeService {

    UserCurriculumVitae save(UserCurriculumVitae userCurriculumVitae);

    List<UserCurriculumVitae> save(List<UserCurriculumVitae> userCurriculumVitaes);

    UserCurriculumVitae findByUserProfile(UserProfile userProfile);

    UserCurriculumVitae findById(Long id);

    List<UserCurriculumVitae> findByIds(Collection<Long> ids);

    Boolean exists(Long id);

    List<UserCurriculumVitae> findByUserIds(List<Long> ids);

    List<BigDecimal> findIdGreaterThan(Long id, int limit);

    Page<UserCurriculumVitae> findByUserIdsWithPagination(List<Long> userCVIdCandidates, int page, int size);
}
