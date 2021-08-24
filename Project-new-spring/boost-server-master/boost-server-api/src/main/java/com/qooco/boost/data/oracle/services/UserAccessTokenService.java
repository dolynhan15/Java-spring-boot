package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserAccessToken;

import java.util.List;

public interface UserAccessTokenService {
    UserAccessToken save(UserAccessToken userAccessToken);

    void deleteByToken(String token);

    List<UserAccessToken> findByUserProfileId(Long userProfileId);

    List<UserAccessToken> findByUserProfileIds(List<Long> userProfileIds, boolean hasChannel);

    void updateChannelIdByAccessToken(String accessToken, String channelId);

    void updatePublicKeyByAccessToken(String accessToken, String publicKey);

    void updateCompanyByAccessToken(String accessToken, Long companyId);

    List<UserAccessToken> findByUserProfileIdsAndValidPublicKey(List<Long> userProfileIds);

    List<UserAccessToken> findByUserProfileIdAndNullCompany(Long userProfileId);
}
