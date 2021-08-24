package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.SocketConnectionDoc;

import java.util.Date;
import java.util.List;

public interface SocketConnectionDocService {
    SocketConnectionDoc save(SocketConnectionDoc doc);

    void updateConnected(String token, String username, Long userProfileId, String appId, String sessionId);

    void updateDisconnected();

    void updateDisconnected(String token, String sessionId);

    void updateSubscribe(String token, String channelId, String channel);

    void updateUnSubscribe(String token, String channelId);

    void updateLogout(String token);

    List<SocketConnectionDoc> findByUserProfileId(Long userProfileId);

    List<SocketConnectionDoc> findByUpdatedDate(Date from, Date to);
}
