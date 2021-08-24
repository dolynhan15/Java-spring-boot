package com.qooco.boost.data.mongo.services.impl;

import com.mongodb.util.JSON;
import com.qooco.boost.data.enumeration.doc.SystemLoggerDocEnum;
import com.qooco.boost.data.mongo.entities.SystemLoggerDoc;
import com.qooco.boost.data.mongo.repositories.SystemLoggerDocRepository;
import com.qooco.boost.data.mongo.services.SystemLoggerDocService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SystemLoggerDocServiceImpl implements SystemLoggerDocService {
    @Autowired
    private SystemLoggerDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public SystemLoggerDoc save(SystemLoggerDoc doc) {
        return repository.save(doc);
    }

    @Override
    public void deleteOldLoggerByCreatedDate(Date date) {
        Criteria createdDate = Criteria.where(SystemLoggerDocEnum.CREATED_DATE.getKey()).lt(date);
        Query query = new Query(createdDate);
        mongoTemplate.remove(query, SystemLoggerDoc.class);
    }

    @Override
    public SystemLoggerDoc findByUrlAndBoostToken(String url, String boostToken) {
        Query query = getUrlAndResponseQuery(url, boostToken);
        return mongoTemplate.findOne(query, SystemLoggerDoc.class);
    }

    @Override
    public void updateRequestBodyByUrlAndBoostToken(String url, String boostToken, Object body) {
        Query query = getUrlAndResponseQuery(url, boostToken);
        Update updater = new Update();
        Map dbObject = (Map) JSON.parse(StringUtil.convertToJsonByJackson(body));
        updater.set(SystemLoggerDocEnum.REQUEST_DATA.getKey(), dbObject);
        mongoTemplate.updateMulti(query, updater, SystemLoggerDoc.class);
    }

    @Override
    public void updateResponseByUrlAndBoostToken(String url, String boostToken, BaseResp response) {
        Query query = getUrlAndResponseQuery(url, boostToken);
        Update updater = new Update();
        Map dbObject = (Map) JSON.parse(StringUtil.convertToJsonByJackson(response));
        updater.set(SystemLoggerDocEnum.RESPONSE.getKey(), dbObject);
        updater.set(SystemLoggerDocEnum.RESPONSE_DATE.getKey(), new Date());
        mongoTemplate.updateMulti(query, updater, SystemLoggerDoc.class);
    }

    @Override
    public void updateResponseByUrlAndBoostToken(String url, String boostToken, BaseResp response, String stackTrace) {
        Query query = getUrlAndResponseQuery(url, boostToken);
        Update updater = new Update();
        Map dbObject = (Map) JSON.parse(StringUtil.convertToJsonByJackson(response));

        updater.set(SystemLoggerDocEnum.RESPONSE.getKey(), dbObject);
        updater.set(SystemLoggerDocEnum.STACK_TRACE.getKey(), stackTrace);
        updater.set(SystemLoggerDocEnum.RESPONSE_DATE.getKey(), new Date());
        mongoTemplate.updateMulti(query, updater, SystemLoggerDoc.class);
    }

    @Override
    public void updateUserInfoByUrlAndBoostToken(String url, String boostToken, String username, String appId, Long companyId, String roles) {
        Query query = getUrlAndResponseQuery(url, boostToken);
        Update updater = new Update();
        updater.set(SystemLoggerDocEnum.APP_ID.getKey(), appId);
        updater.set(SystemLoggerDocEnum.USERNAME.getKey(), username);
        updater.set(SystemLoggerDocEnum.COMPANY_ID.getKey(), companyId);
        updater.set(SystemLoggerDocEnum.ROLES.getKey(), roles);
        mongoTemplate.updateMulti(query, updater, SystemLoggerDoc.class);
    }

    @Override
    public List<SystemLoggerDoc> findByCreatedDate(Date from, Date to) {
        Criteria createdDateCriteria = new Criteria().andOperator(
                Criteria.where(SystemLoggerDocEnum.CREATED_DATE.getKey()).gte(from),
                Criteria.where(SystemLoggerDocEnum.CREATED_DATE.getKey()).lte(to)
        );
        return mongoTemplate.find(new Query(createdDateCriteria), SystemLoggerDoc.class);
    }

    private Query getUrlAndResponseQuery(String url, String boostToken) {
        Criteria urlCriteria = Criteria.where(SystemLoggerDocEnum.URL.getKey()).is(url.trim());
        Criteria boostTokenCriteria = Criteria.where(SystemLoggerDocEnum.BOOST_TOKEN.getKey()).is(boostToken);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, SystemLoggerDocEnum.CREATED_DATE.getKey()));
        Criteria criteria = new Criteria().andOperator(urlCriteria, boostTokenCriteria);
        return new Query(criteria).with(sort);
    }
}
