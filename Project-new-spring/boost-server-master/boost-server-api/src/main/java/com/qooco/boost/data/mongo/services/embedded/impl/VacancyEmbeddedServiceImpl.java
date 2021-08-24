package com.qooco.boost.data.mongo.services.embedded.impl;

import com.qooco.boost.data.enumeration.doc.AppointmentDetailDocEnum;
import com.qooco.boost.data.enumeration.doc.MessageCenterDocEnum;
import com.qooco.boost.data.enumeration.doc.MessageDocEnum;
import com.qooco.boost.data.enumeration.doc.ViewProfileDocEnum;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import com.qooco.boost.data.mongo.services.embedded.VacancyEmbeddedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VacancyEmbeddedServiceImpl implements VacancyEmbeddedService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void update(VacancyEmbedded embedded) {
        updateInMessageCenterDoc(embedded);
        updateInAppointmentDetailDoc(embedded);
        updateInViewProfileDoc(embedded);
        updateInMessageDoc(embedded);
    }

    @Override
    public void update(List<VacancyEmbedded> embeddeds) {
        embeddeds.forEach(this::update);
    }

    private void updateInMessageCenterDoc(VacancyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            Criteria criteria = Criteria.where(MessageCenterDocEnum.VACANCY_ID.getKey()).is(embedded.getId());
            Update update = new Update().set(MessageCenterDocEnum.VACANCY.getKey(), embedded);
            mongoTemplate.updateMulti(new Query(criteria), update, MessageCenterDoc.class);
        }
    }

    private void updateInAppointmentDetailDoc(VacancyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            Criteria criteria = Criteria.where(AppointmentDetailDocEnum.VACANCY_ID.getKey()).is(embedded.getId());
            Update update = new Update().set(AppointmentDetailDocEnum.VACANCY.getKey(), embedded);
            mongoTemplate.updateMulti(new Query(criteria), update, AppointmentDetailDoc.class);
        }
    }

    private void updateInViewProfileDoc(VacancyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            Criteria criteria = Criteria.where(ViewProfileDocEnum.VACANCY_ID.getKey()).is(embedded.getId());
            Update update = new Update().set(ViewProfileDocEnum.VACANCY.getKey(), embedded);
            mongoTemplate.updateMulti(new Query(criteria), update, ViewProfileDoc.class);
        }
    }

    private void updateInMessageDoc(VacancyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MessageDoc.class);
            Criteria criteria = Criteria.where(MessageDocEnum.APPLIED_MESSAGE_VACANCY_ID.getKey()).is(embedded.getId());
            Update update = new Update().set(MessageDocEnum.APPLIED_MESSAGE_VACANCY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteria), update);

            Criteria criteriaAppointment = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_VACANCY_ID.getKey()).is(embedded.getId());
            Update updateAppointment = new Update().set(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_VACANCY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaAppointment), updateAppointment);
            bulkOps.execute();
        }
    }
}
