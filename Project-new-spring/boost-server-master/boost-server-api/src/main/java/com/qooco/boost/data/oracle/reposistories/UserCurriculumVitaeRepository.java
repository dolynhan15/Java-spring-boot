package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface UserCurriculumVitaeRepository extends Boot2JpaRepository<UserCurriculumVitae, Long> {

    @Query("SELECT u FROM UserCurriculumVitae u WHERE u.userProfile.userProfileId = :userProfileId AND u.isDeleted = false")
    UserCurriculumVitae findByUserProfileId(@Param("userProfileId") Long userProfileId);

    UserCurriculumVitae findByCurriculumVitaeId(@Param("curriculumVitaeId") Long id);

    @Query("SELECT u FROM UserCurriculumVitae u WHERE u.curriculumVitaeId IN :ids AND u.isDeleted = false")
    List<UserCurriculumVitae> findByCurriculumVitaeIds(@Param("ids") Collection<Long> ids);

    @Query(value = "SELECT u FROM UserCurriculumVitae u WHERE u.userProfile.userProfileId IN :userProfileIds AND u.isDeleted = false")
    List<UserCurriculumVitae> findByUserIds(@Param("userProfileIds") List<Long> userProfileIds);

    @Query(nativeQuery = true, value = "SELECT u.CURRICULUM_VITAE_ID FROM USER_CURRICULUM_VITAE u WHERE u.CURRICULUM_VITAE_ID > :id AND u.IS_DELETED = 0 AND ROWNUM <= :limit ")
    List<BigDecimal> findIdGreaterThan(@Param("id") Long id, @Param("limit") int limit);


    @Query("SELECT u FROM UserCurriculumVitae u WHERE u.curriculumVitaeId IN :ids AND u.isDeleted = false " +
            "ORDER BY GET_DISPLAY_NAME(u.userProfile.firstName, u.userProfile.lastName, u.userProfile.username) ASC ")
    Page<UserCurriculumVitae> findByUserIdsWithPagination(@Param("ids") List<Long> ids, Pageable pageable);
}
