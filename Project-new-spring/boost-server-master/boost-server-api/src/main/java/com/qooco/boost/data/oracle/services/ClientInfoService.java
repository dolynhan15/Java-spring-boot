package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ClientInfo;

import java.util.List;

public interface ClientInfoService {
    List<ClientInfo> findClientInfo(String appId, String platform, Long userProfileId, String channelId);

    List<ClientInfo> findClientInfoWithNullChannelId(String appId, String platform, Long userProfileId);

    void updateLogoutTime(String token);

    ClientInfo save(ClientInfo clientInfo);

    ClientInfo findByToken(String token);
}