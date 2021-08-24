package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserFit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserFitRepository extends Boot2JpaRepository<UserFit, Long> {
    UserFit findByUsername(String username);

    //boolean existsById(Long userId);

    @Query("SELECT u FROM UserFit u WHERE u.userProfileId = :id AND u.isDeleted = false")
    UserFit findValidById(@Param("id") Long id);

    @Query("SELECT u FROM UserFit u WHERE u.userProfileId IN :ids AND u.isDeleted = false")
    List<UserFit> findByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE UserFit u SET u.defaultCompanyId = :companyId WHERE u.userProfileId = :id")
    void setDefaultCompany(@Param("id") Long id, @Param("companyId") Long companyId);
}