package com.qooco.boost.data.oracle.reposistories.views;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/3/2018 - 1:27 PM
 */

import com.qooco.boost.data.oracle.entities.views.ViewAccessTokenRoles;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewAccessTokenRolesRepository extends Boot2JpaRepository<ViewAccessTokenRoles, Long> {
    @Query("SELECT vr FROM ViewAccessTokenRoles vr WHERE vr.token = :token")
    ViewAccessTokenRoles findByToken(@Param("token") String token);

    @Query("SELECT vr FROM ViewAccessTokenRoles vr WHERE vr.userProfileId = :userProfileIds AND vr.appId = :appId")
    List<ViewAccessTokenRoles> findByUserProfileIds(@Param("userProfileIds") List<Long> userProfileIds, @Param("appId") String appId);

    @Query("SELECT vr FROM ViewAccessTokenRoles vr WHERE vr.userProfileId = :userProfileIds AND vr.channelId IS NOT NULL AND vr.appId = :appId")
    List<ViewAccessTokenRoles> findByUserProfileIdsAndValidChannelId(@Param("userProfileIds") List<Long> userProfileIds, @Param("appId") String appId);
}
