package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.enumeration.doc.SocketConnectionDocEnum;
import com.qooco.boost.data.mongo.entities.SocketConnectionDoc;
import com.qooco.boost.data.mongo.repositories.SocketConnectionDocRepository;
import com.qooco.boost.data.mongo.services.SocketConnectionDocService;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SocketConnectionDocServiceImpl implements SocketConnectionDocService {
    @Autowired
    private SocketConnectionDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public SocketConnectionDoc save(SocketConnectionDoc doc) {
        return repository.save(doc);
    }

    @Override
    public void updateConnected(String token, String username, Long userProfileId, String appId, String sessionId) {
        Query query = getSocketConnectionQuery(token);
        Date now = DateUtils.toServerTimeForMongo();

        Update updater = new Update();
        updater.set(SocketConnectionDocEnum.TOKEN.getKey(), token);
        updater.set(SocketConnectionDocEnum.USERNAME.getKey(), username);
        updater.set(SocketConnectionDocEnum.USER_PROFILE_ID.getKey(), userProfileId);
        updater.set(SocketConnectionDocEnum.APP_ID.getKey(), appId);

        updater.set(SocketConnectionDocEnum.LASTED_ONLINE_DATE.getKey(), now);
        updater.set(SocketConnectionDocEnum.UPDATED_DATE.getKey(), now);
        mongoTemplate.upsert(query, updater, SocketConnectionDoc.class);
    }

    @Override
    public void updateDisconnected() {
        Criteria existsCriteria = Criteria.where(SocketConnectionDocEnum.CHANNELS.getKey()).exists(true);
        Query query = new Query(existsCriteria);

        Update updater = new Update();
        updater.unset(SocketConnectionDocEnum.CHANNELS.getKey());
        updater.set(SocketConnectionDocEnum.UPDATED_DATE.getKey(), DateUtils.toServerTimeForMongo());
        mongoTemplate.updateMulti(query, updater, SocketConnectionDoc.class);
    }

    @Override
    public void updateDisconnected(String token, String sessionId) {
        Query query = getSocketConnectionQuery(token);
        Update updater = new Update();

        updater.unset(SocketConnectionDocEnum.CHANNELS.getKey());
        updater.set(SocketConnectionDocEnum.UPDATED_DATE.getKey(), DateUtils.toServerTimeForMongo());
        mongoTemplate.updateMulti(query, updater, SocketConnectionDoc.class);
    }

    @Override
    public void updateSubscribe(String token, String channelId, String channel) {
        Query query = getSocketConnectionQuery(token);
        Update updater = new Update();
        String channelKey = StringUtil.append(SocketConnectionDocEnum.CHANNELS.getKey(), Constants.DOT, channelId);
        updater.set(channelKey, channel);
        updater.set(SocketConnectionDocEnum.UPDATED_DATE.getKey(), DateUtils.toServerTimeForMongo());
        mongoTemplate.updateMulti(query, updater, SocketConnectionDoc.class);
    }

    @Override
    public void updateUnSubscribe(String token, String channelId) {
        Query query = getSocketConnectionQuery(token);
        Update updater = new Update();
        String channelKey = StringUtil.append(SocketConnectionDocEnum.CHANNELS.getKey(), Constants.DOT, channelId);
        updater.unset(channelKey);
        updater.set(SocketConnectionDocEnum.UPDATED_DATE.getKey(), DateUtils.toServerTimeForMongo());
        mongoTemplate.updateMulti(query, updater, SocketConnectionDoc.class);
    }

    @Override
    public List<SocketConnectionDoc> findByUserProfileId(Long userProfileId) {
        Criteria userProfileIdCriteria = Criteria.where(SocketConnectionDocEnum.USER_PROFILE_ID.getKey()).is(userProfileId);
        Query query = new Query(userProfileIdCriteria);
        return mongoTemplate.find(query, SocketConnectionDoc.class);
    }

    @Override
    public void updateLogout(String token) {
        Query query = getSocketConnectionQuery(token);
        Update updater = new Update();
        updater.set(SocketConnectionDocEnum.IS_LOGOUT.getKey(), true);
        updater.set(SocketConnectionDocEnum.UPDATED_DATE.getKey(), DateUtils.toServerTimeForMongo());
        mongoTemplate.updateMulti(query, updater, SocketConnectionDoc.class);
    }

    @Override
    public List<SocketConnectionDoc> findByUpdatedDate(Date from, Date to) {
        Criteria createdDateCriteria = new Criteria().andOperator(
                Criteria.where(SocketConnectionDocEnum.UPDATED_DATE.getKey()).gte(from),
                Criteria.where(SocketConnectionDocEnum.UPDATED_DATE.getKey()).lte(to)
        );
        return mongoTemplate.find(new Query(createdDateCriteria), SocketConnectionDoc.class);
    }

    private Query getSocketConnectionQuery(String token) {
        Criteria tokenCriteria = Criteria.where(SocketConnectionDocEnum.TOKEN.getKey()).is(token);
        return new Query(tokenCriteria);
    }
}
