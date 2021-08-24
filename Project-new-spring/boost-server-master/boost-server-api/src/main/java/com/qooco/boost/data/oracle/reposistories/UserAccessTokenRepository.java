package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserAccessToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserAccessTokenRepository extends Boot2JpaRepository<UserAccessToken, Long> {
    @Modifying
    @Query("UPDATE UserAccessToken uat SET uat.isDeleted = :isDelete WHERE uat.token = :token")
    @Transactional
    int setIsDeleteByToken(@Param("token") String token, @Param("isDelete") boolean isDelete);

    @Modifying
    @Transactional
    void deleteByToken(String token);

    @Modifying
    @Query("UPDATE UserAccessToken uat SET uat.channelId = :channelId WHERE uat.token = :token")
    @Transactional
    void updateChannelIdByAccessToken(@Param("token") String token, @Param("channelId") String channelId);

    @Modifying
    @Query("UPDATE UserAccessToken uat SET uat.publicKey = :publicKey WHERE uat.token = :token")
    @Transactional
    void updatePublicKeyByAccessToken(@Param("token") String token, @Param("publicKey") String publicKey);

    @Modifying
    @Query("UPDATE UserAccessToken uat SET uat.companyId = :companyId WHERE uat.token = :token")
    @Transactional
    void updateCompanyByAccessToken(@Param("token") String token, @Param("companyId") Long companyId);

    @Query("SELECT uat FROM UserAccessToken uat WHERE uat.userProfile.userProfileId = :userProfileId AND uat.isDeleted = false")
    List<UserAccessToken> findByUserProfileId(@Param("userProfileId") Long userProfileId);

    @Query("SELECT uat FROM UserAccessToken uat WHERE uat.userProfile.userProfileId IN :userProfileIds AND uat.isDeleted = false")
    List<UserAccessToken> findByUserProfileIds(@Param("userProfileIds") List<Long> userProfileIds);

    @Query("SELECT uat FROM UserAccessToken uat WHERE uat.userProfile.userProfileId IN :userProfileIds AND uat.isDeleted = false AND uat.channelId IS NOT NULL")
    List<UserAccessToken> findByUserProfileIdsWithValidChannel(@Param("userProfileIds") List<Long> userProfileIds);

    @Query("SELECT uat FROM UserAccessToken uat WHERE uat.userProfile.userProfileId IN :userProfileIds AND uat.isDeleted = false AND uat.publicKey IS NOT NULL")
    List<UserAccessToken> findByUserProfileIdsAndValidPublicKey(@Param("userProfileIds") List<Long> userProfileIds);

    List<UserAccessToken> findByUserProfile_UserProfileIdAndCompanyIdNull(Long userProfileId);
}
