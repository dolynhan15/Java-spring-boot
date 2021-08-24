package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.UserAccessToken;
import com.qooco.boost.data.oracle.reposistories.UserAccessTokenRepository;
import com.qooco.boost.data.oracle.services.UserAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccessTokenServiceImpl implements UserAccessTokenService {

    @Autowired
    private UserAccessTokenRepository repository;

    @Override
    public UserAccessToken save(UserAccessToken userAccessToken) {
        return repository.save(userAccessToken);
    }

    @Override
    public void deleteByToken(String token) {
       repository.deleteByToken(token);
    }

    @Override
    public List<UserAccessToken> findByUserProfileId(Long userProfileId) {
        return repository.findByUserProfileId(userProfileId);
    }

    @Override
    public List<UserAccessToken> findByUserProfileIds(List<Long> userProfileIds, boolean hasChannel) {
        if (hasChannel){
            return repository.findByUserProfileIdsWithValidChannel(userProfileIds);
        }
        return repository.findByUserProfileIds(userProfileIds);
    }

    @Override
    public void updateChannelIdByAccessToken(String accessToken, String channelId) {
        repository.updateChannelIdByAccessToken(accessToken, channelId);
    }

    @Override
    public void updatePublicKeyByAccessToken(String accessToken, String publicKey) {
        repository.updatePublicKeyByAccessToken(accessToken, publicKey);
    }

    @Override
    public void updateCompanyByAccessToken(String accessToken, Long companyId) {
        repository.updateCompanyByAccessToken(accessToken, companyId);
    }

    @Override
    public List<UserAccessToken> findByUserProfileIdsAndValidPublicKey(List<Long> userProfileIds) {
        return repository.findByUserProfileIdsAndValidPublicKey(userProfileIds);
    }

    @Override
    public List<UserAccessToken> findByUserProfileIdAndNullCompany(Long userProfileId) {
        return repository.findByUserProfile_UserProfileIdAndCompanyIdNull(userProfileId);
    }
}
