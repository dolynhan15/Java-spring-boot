package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.ClientInfo;
import com.qooco.boost.data.oracle.reposistories.ClientInfoRepository;
import com.qooco.boost.data.oracle.services.ClientInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientInfoServiceImpl implements ClientInfoService {
    @Autowired
    private ClientInfoRepository clientInfoRepository;

    @Override
    public List<ClientInfo> findClientInfo(String appId, String platform, Long userProfileId, String channelId) {
        return clientInfoRepository.findByAppIdAndPlatformAndUserProfileIdAndChannelId(appId, platform, userProfileId, channelId);
    }

    @Override
    public List<ClientInfo> findClientInfoWithNullChannelId(String appId, String platform, Long userProfileId) {
        return clientInfoRepository.findByAppIdAndPlatformAndUserProfileId(appId, platform, userProfileId);
    }

    @Override
    public void updateLogoutTime(String token) {
        clientInfoRepository.updateLogoutTime(token);
    }

    @Override
    public ClientInfo save(ClientInfo clientInfo) {
        return clientInfoRepository.save(clientInfo);
    }

    @Override
    public ClientInfo findByToken(String token) {
        return clientInfoRepository.findById(token).orElse(null);
    }
}