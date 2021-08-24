package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.enumeration.doc.AppointmentDetailDocEnum;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.repositories.AppointmentDetailDocRepository;
import com.qooco.boost.data.mongo.services.AppointmentDetailDocService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.qooco.boost.data.mongo.services.impl.Boot2MongoUtils.wasAcknowledged;

@Service
public class AppointmentDetailDocServiceImpl implements AppointmentDetailDocService {
    @Autowired
    private AppointmentDetailDocRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoRepository<AppointmentDetailDoc, Long> getRepository() {
        return repository;
    }

    @Override
    public List<AppointmentDetailDoc> findByAppointmentId(Long appointmentId) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, AppointmentDetailDocEnum.ID.getKey()));
        Criteria deletedCriteria = Criteria.where(AppointmentDetailDocEnum.IS_DELETED.getKey()).is(false);
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_ID.getKey()).is(appointmentId), deletedCriteria);
        return mongoTemplate.find(new Query().addCriteria(criteria).with(sort), AppointmentDetailDoc.class);
    }

    @Override
    public void updateStatusOfAppointmentDetailDoc(long id, Integer status) {
        Criteria criteria = createCriteriaId(id);
        Update update = new Update().set(AppointmentDetailDocEnum.STATUS.getKey(), status);
        mongoTemplate.updateMulti(new Query(criteria), update, AppointmentDetailDoc.class);
    }

    @Override
    public void updateAppointmentTimeAndStatus(long id, Date time, int status) {
        Criteria criteria = createCriteriaId(id);
        Update update = new Update();
        update.set(AppointmentDetailDocEnum.APPOINTMENT_TIME.getKey(), time);
        update.set(AppointmentDetailDocEnum.STATUS.getKey(), status);
        mongoTemplate.updateMulti(new Query(criteria), update, AppointmentDetailDoc.class);
    }

    private Criteria createCriteriaId(long id) {
        Criteria deletedCriteria = Criteria.where(AppointmentDetailDocEnum.IS_DELETED.getKey()).is(false);
        return new Criteria().andOperator(Criteria.where(AppointmentDetailDocEnum.ID.getKey()).is(id), deletedCriteria);
    }

    @Override
    public List<AppointmentDetailDoc> findByVacancyIdAndCandidateCVId(Long vacancyId, List<Long> userProfileCvIds) {
        Criteria deletedCriteria = Criteria.where(AppointmentDetailDocEnum.IS_DELETED.getKey()).is(false);
        Criteria vacancyIdCriteria = Criteria.where(AppointmentDetailDocEnum.VACANCY_ID.getKey()).is(vacancyId);
        Criteria candidateCVIdCriteria = Criteria.where(AppointmentDetailDocEnum.CANDIDATE_USER_PROFILE_CV_ID.getKey()).in(userProfileCvIds);
        Criteria criteria = new Criteria().andOperator(deletedCriteria, vacancyIdCriteria, candidateCVIdCriteria);
        Query query = new Query(criteria);
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, AppointmentDetailDocEnum.APPOINTMENT_FROM_DATE.getKey()));
        query.with(sort);
        return mongoTemplate.find(query, AppointmentDetailDoc.class);
    }

    @Override
    public List<AppointmentDetailDoc> findNoDateTimeRange(int limit, List<Long> exceptId) {
        Criteria dateRange = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_DATE_RANGE.getKey()).exists(false);
        Criteria timeRange = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_TIME_RANGE.getKey()).exists(false);
        Criteria toDate = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_TO_DATE.getKey()).exists(false);
        Criteria fromDate = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_FROM_DATE.getKey()).exists(false);
        Criteria criteria = new Criteria();
        if (CollectionUtils.isEmpty(exceptId)) {
            criteria.orOperator(dateRange, timeRange, toDate, fromDate);
        } else {
            Criteria rangeCriteria = new Criteria().orOperator(dateRange, timeRange, toDate, fromDate);
            criteria.andOperator(Criteria.where(AppointmentDetailDocEnum.ID.getKey()).nin(exceptId), rangeCriteria);
        }
        return mongoTemplate.find(new Query(criteria).limit(limit), AppointmentDetailDoc.class);
    }

    @Override
    public void updateDateTimeRangeAndType(long id, List<Date> dateRanges, List<Date> timeRanges, int type, Date fromDate, Date toDate) {
        Criteria criteria = createCriteriaId(id);
        Update update = new Update();
        update.set(AppointmentDetailDocEnum.APPOINTMENT_DATE_RANGE.getKey(), dateRanges);
        update.set(AppointmentDetailDocEnum.APPOINTMENT_TIME_RANGE.getKey(), timeRanges);
        update.set(AppointmentDetailDocEnum.APPOINTMENT_TYPE.getKey(), type);
        update.set(AppointmentDetailDocEnum.APPOINTMENT_FROM_DATE.getKey(), fromDate);
        update.set(AppointmentDetailDocEnum.APPOINTMENT_TO_DATE.getKey(), toDate);
        mongoTemplate.updateMulti(new Query(criteria), update, AppointmentDetailDoc.class);
    }

    @Override
    public List<AppointmentDetailDoc> findNoneVacancy(int limit, List<Long> exceptId) {
        Criteria vacancyCriteria = Criteria.where(AppointmentDetailDocEnum.VACANCY.getKey()).exists(false);
        Criteria criteria = new Criteria();
        if (CollectionUtils.isEmpty(exceptId)) {
            criteria.andOperator(vacancyCriteria);
        } else {
            criteria.andOperator(Criteria.where(AppointmentDetailDocEnum.ID.getKey()).nin(exceptId), vacancyCriteria);
        }
        return mongoTemplate.find(new Query(criteria).limit(limit), AppointmentDetailDoc.class);
    }

    @Override
    public boolean updateBatchVacancies(List<AppointmentDetailDoc> appointmentDetailDocs) {
        if (CollectionUtils.isNotEmpty(appointmentDetailDocs)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AppointmentDetailDoc.class);
            appointmentDetailDocs.forEach(appointmentDetailDoc -> {
                Update updater = new Update();
                updater.set(AppointmentDetailDocEnum.VACANCY.getKey(), appointmentDetailDoc.getVacancy());
                Criteria criteria = Criteria.where(AppointmentDetailDocEnum.ID.getKey()).is(appointmentDetailDoc.getId());
                bulkOps.updateMulti(new Query(criteria), updater);
            });
            return wasAcknowledged(bulkOps.execute());
        }
        return false;
    }
}
