package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.enumeration.doc.AppointmentDetailNotifyDocEnum;
import com.qooco.boost.data.mongo.entities.AppointmentDetailNotifyDoc;
import com.qooco.boost.data.mongo.services.AppointmentDetailNotifyDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentDetailNotifyDocServiceImpl implements AppointmentDetailNotifyDocService {
    @Autowired
    private MongoTemplate mongoTemplate;

        @Override
        public void save(AppointmentDetailNotifyDoc doc) {
            mongoTemplate.save(doc);
        }

    @Override
    public List<AppointmentDetailNotifyDoc> findByUserProfileAndCompany(Long userProfileId, Long companyId) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(AppointmentDetailNotifyDocEnum.STAFF_COMPANY_ID.getKey()).is(companyId),
                Criteria.where(AppointmentDetailNotifyDocEnum.STAFF_USER_PROFILE_USER_PROFILE_ID.getKey()).is(userProfileId)
        );
        return mongoTemplate.find(new Query(criteria), AppointmentDetailNotifyDoc.class);
    }

    @Override
    public List<AppointmentDetailNotifyDoc> findByUserProfileAndCompany(Long staffId) {
        Criteria criteria = Criteria.where(AppointmentDetailNotifyDocEnum.STAFF_ID.getKey()).is(staffId);
        return mongoTemplate.find(new Query(criteria), AppointmentDetailNotifyDoc.class);
    }
}
