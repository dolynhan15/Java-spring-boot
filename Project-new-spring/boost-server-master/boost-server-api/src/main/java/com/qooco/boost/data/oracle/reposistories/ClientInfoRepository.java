package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.ClientInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ClientInfoRepository extends Boot2JpaRepository<ClientInfo, String> {
    @Query(value = "SELECT c FROM ClientInfo c WHERE c.appId = :appId AND c.channelId = :channelId AND c.platform = :platform AND c.userProfileId = :userProfileId AND c.logoutTime IS NULL")
    List<ClientInfo> findByAppIdAndPlatformAndUserProfileIdAndChannelId(@Param("appId") String appId, @Param("platform") String platform, @Param("userProfileId") Long userProfileId, @Param("channelId") String channelId);

    @Query(value = "SELECT c FROM ClientInfo c WHERE c.appId = :appId AND c.channelId IS NULL AND c.platform = :platform AND c.userProfileId = :userProfileId AND c.logoutTime IS NULL")
    List<ClientInfo> findByAppIdAndPlatformAndUserProfileId(@Param("appId") String appId, @Param("platform") String platform, @Param("userProfileId") Long userProfileId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE ClientInfo c SET c.logoutTime = SYS_EXTRACT_UTC(SYSTIMESTAMP) WHERE c.token = :token AND c.logoutTime IS NULL")
    void updateLogoutTime(@Param("token") String token);
}