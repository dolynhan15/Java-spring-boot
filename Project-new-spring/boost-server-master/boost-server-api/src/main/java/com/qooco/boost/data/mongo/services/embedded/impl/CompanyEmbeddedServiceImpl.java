package com.qooco.boost.data.mongo.services.embedded.impl;

import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.enumeration.doc.AppointmentDetailDocEnum;
import com.qooco.boost.data.enumeration.doc.MessageCenterDocEnum;
import com.qooco.boost.data.enumeration.doc.MessageDocEnum;
import com.qooco.boost.data.enumeration.doc.VacancyDocEnum;
import com.qooco.boost.data.enumeration.embedded.CompanyEmbeddedEnum;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.embedded.CompanyEmbeddedService;
import com.qooco.boost.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CompanyEmbeddedServiceImpl implements CompanyEmbeddedService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void update(CompanyEmbedded embedded) {
        updateInVacancyDoc(embedded);
        updateInMessageCenterDoc(embedded);
        updateInAppointmentDetailDoc(embedded);
        updateInMessageDoc(embedded);
    }

    private void updateInVacancyDoc(CompanyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            Criteria criteria = Criteria.where(VacancyDocEnum.COMPANY_ID.key()).is(embedded.getId());
            Update update = new Update().set(VacancyDocEnum.COMPANY.key(), embedded);
            mongoTemplate.updateMulti(new Query(criteria), update, VacancyDoc.class);
        }
    }

    private void updateInMessageCenterDoc(CompanyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MessageCenterDoc.class);
            Criteria criteriaCompany = Criteria.where(MessageCenterDocEnum.COMPANY_ID.getKey()).is(embedded.getId());
            Update updateCompany = new Update().set(MessageCenterDocEnum.COMPANY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaCompany), updateCompany);

            String vacancyCompanyId = StringUtil.append(MessageCenterDocEnum.VACANCY_COMPANY.getKey(), Constants.DOT, CompanyEmbeddedEnum.ID.getKey());
            Criteria criteriaVacancyCompany = Criteria.where(vacancyCompanyId).is(embedded.getId());
            Update updateAppointmentCompany = new Update().set(MessageCenterDocEnum.VACANCY_COMPANY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaVacancyCompany), updateAppointmentCompany);
            bulkOps.execute();
        }
    }

    private void updateInMessageDoc(CompanyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MessageDoc.class);
            String vacancyCompanyId = StringUtil.append(MessageDocEnum.APPLIED_MESSAGE_VACANCY_COMPANY.getKey(), Constants.DOT, CompanyEmbeddedEnum.ID.getKey());
            Criteria criteriaVacancyCompany = Criteria.where(vacancyCompanyId).is(embedded.getId());
            Update updateVacancyCompany = new Update().set(MessageDocEnum.APPLIED_MESSAGE_VACANCY_COMPANY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaVacancyCompany), updateVacancyCompany);

            String authorizationCompanyId = StringUtil.append(MessageDocEnum.AUTHORIZATION_MESSAGE_COMPANY.getKey(), Constants.DOT, CompanyEmbeddedEnum.ID.getKey());
            Criteria criteriaAuthorizationCompanyId = Criteria.where(authorizationCompanyId).is(embedded.getId());
            Update updateAuthorizationCompanyId = new Update().set(MessageDocEnum.AUTHORIZATION_MESSAGE_COMPANY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaAuthorizationCompanyId), updateAuthorizationCompanyId);

            String staffCompanyId = StringUtil.append(MessageDocEnum.STAFF_EMBEDDED_COMPANY.getKey(), Constants.DOT, CompanyEmbeddedEnum.ID.getKey());
            Criteria criteriaStaffCompanyId = Criteria.where(staffCompanyId).is(embedded.getId());
            Update updateStaffCompanyId = new Update().set(MessageDocEnum.STAFF_EMBEDDED_COMPANY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaStaffCompanyId), updateStaffCompanyId);

            String eventVacancyCompanyId = StringUtil.append(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_VACANCY_COMPANY.getKey(),
                    Constants.DOT, CompanyEmbeddedEnum.ID.getKey());
            Criteria criteriaEventVacancyCompanyId = Criteria.where(eventVacancyCompanyId).is(embedded.getId());
            Update updateEventVacancyCompanyId = new Update().set(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_VACANCY_COMPANY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaEventVacancyCompanyId), updateEventVacancyCompanyId);

            String locationCompanyId = StringUtil.append(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_LOCATION_COMPANY.getKey(),
                    Constants.DOT, CompanyEmbeddedEnum.ID.getKey());
            Criteria criteriaLocationCompanyId = Criteria.where(locationCompanyId).is(embedded.getId());
            Update updateLocationCompanyId = new Update().set(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_LOCATION_COMPANY.getKey(), embedded);
            bulkOps.updateMulti(new Query(criteriaLocationCompanyId), updateLocationCompanyId);

            bulkOps.execute();
        }
    }

    private void updateInAppointmentDetailDoc(CompanyEmbedded embedded) {
        if (Objects.nonNull(embedded)) {
            Criteria criteria = Criteria.where(AppointmentDetailDocEnum.VACANCY_COMPANY_ID.getKey()).is(embedded.getId());
            Update update = new Update().set(AppointmentDetailDocEnum.VACANCY_COMPANY.getKey(), embedded);
            mongoTemplate.updateMulti(new Query(criteria), update, AppointmentDetailDoc.class);
        }
    }
}
