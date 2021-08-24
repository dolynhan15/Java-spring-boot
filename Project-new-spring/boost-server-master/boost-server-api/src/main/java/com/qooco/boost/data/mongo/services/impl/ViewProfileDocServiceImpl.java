package com.qooco.boost.data.mongo.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.doc.ViewProfileDocEnum;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import com.qooco.boost.data.mongo.repositories.ViewProfileDocRepository;
import com.qooco.boost.data.mongo.services.ViewProfileDocService;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ViewProfileDocServiceImpl implements ViewProfileDocService {
    @Autowired
    private ViewProfileDocRepository viewProfileDocRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ViewProfileDoc save(ViewProfileDoc viewProfileDoc) {
        return viewProfileDocRepository.save(viewProfileDoc);
    }

    @Override
    public ViewProfileDoc findById(ObjectId id) {
        return viewProfileDocRepository.findById(id).orElse(null);
    }

    @Override
    public List<ViewProfileDoc> save(List<ViewProfileDoc> viewProfileDocs) {
        return Lists.newArrayList(viewProfileDocRepository.saveAll(viewProfileDocs));
    }

    @Override
    public long countViewProfileByCandidateIdAndTimestamp(Long userProfileId, Date createdDate) {
        Criteria candidateIdCriteria = Criteria.where(ViewProfileDocEnum.CANDIDATE_USER_PROFILE_ID.getKey()).is(userProfileId);
        Criteria createdDateCriteria = Criteria.where(ViewProfileDocEnum.CREATED_DATE.getKey()).gte(createdDate);
        Criteria criteria = new Criteria();
        criteria.andOperator(candidateIdCriteria, createdDateCriteria);
        Query query = new Query().addCriteria(criteria);
        return mongoTemplate.count(query, ViewProfileDoc.class);
    }

    @Override
    public void updateViewerOrCandidate(UserProfileEmbedded user) {
        switch (user.getUserType()) {
            case UserType.SELECT:
                String viewerPrefix = StringUtil.append(ViewProfileDocEnum.VIEWER.getKey(), Constants.DOT);
                Criteria viewerCriteria = Criteria.where(ViewProfileDocEnum.VIEWER_USER_PROFILE_ID.getKey()).is(user.getUserProfileId());
                Map<String, Object> viewerMap = MongoInitData.initUserProfileEmbedded(viewerPrefix, user);
                updateViewerOrCandidate(viewerCriteria, viewerMap);
                break;
            case UserType.PROFILE:
                String candidatePrefix = StringUtil.append(ViewProfileDocEnum.CANDIDATE_USER_PROFILE_ID.getKey(), Constants.DOT);
                Criteria candidateKeyCriteria = Criteria.where(ViewProfileDocEnum.CANDIDATE_USER_PROFILE_ID.getKey()).is(user.getUserProfileId());
                Map<String, Object> candidateMap = MongoInitData.initUserProfileEmbedded(candidatePrefix, user);
                updateViewerOrCandidate(candidateKeyCriteria, candidateMap);
                break;
            default:
                break;
        }
    }

    @Override
    public void updateVacancyEmbedded(VacancyEmbedded vacancyEmbedded) {
        if (Objects.nonNull(vacancyEmbedded)) {
            Criteria criteria = Criteria.where(ViewProfileDocEnum.VACANCY_ID.getKey()).is(vacancyEmbedded.getId());
            Update update = new Update();
            update.set(ViewProfileDocEnum.VACANCY.getKey(), vacancyEmbedded);
            mongoTemplate.updateMulti(new Query(criteria), update, ViewProfileDoc.class);
        }
    }

    private void updateViewerOrCandidate(Criteria criteria, Map<String, Object> candidateMap) {
        if (MapUtils.isNotEmpty(candidateMap)) {
            Update update = new Update();
            Query query = new Query(criteria);
            candidateMap.forEach(update::set);
            mongoTemplate.updateMulti(query, update, ViewProfileDoc.class);
        }
    }
}
