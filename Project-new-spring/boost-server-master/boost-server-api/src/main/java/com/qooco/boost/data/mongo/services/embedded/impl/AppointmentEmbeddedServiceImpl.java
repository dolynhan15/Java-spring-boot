package com.qooco.boost.data.mongo.services.embedded.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.enumeration.doc.AppointmentDetailDocEnum;
import com.qooco.boost.data.enumeration.doc.MessageDocEnum;
import com.qooco.boost.data.enumeration.doc.VacancyDocEnum;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.mongo.services.embedded.AppointmentEmbeddedService;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AppointmentEmbeddedServiceImpl implements AppointmentEmbeddedService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private VacancyDocService vacancyDocService;

    @Override
    public void update(Long vacancyId, AppointmentEmbedded appointment) {
        updateVacancyDoc(vacancyId, appointment);
        updateMessageDoc(appointment);
        updateAppointmentDetailDoc(appointment);
    }

    private void updateVacancyDoc(Long vacancyId, AppointmentEmbedded embedded) {
        VacancyDoc vacancyDoc = vacancyDocService.findById(vacancyId);
        List<AppointmentEmbedded> appointmentEmbeddeds = vacancyDoc.getAppointments();
        Update updater = new Update();
        if(CollectionUtils.isNotEmpty(appointmentEmbeddeds)){
            Set<AppointmentEmbedded> appointments = new HashSet<>(appointmentEmbeddeds);
            appointments.add(embedded);
            updater.set(VacancyDocEnum.APPOINTMENTS.key(), Lists.newArrayList(appointments));
        } else {
            updater.set(VacancyDocEnum.APPOINTMENTS.key(),Lists.newArrayList(embedded));
        }

        Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(vacancyId);
        mongoTemplate.updateMulti( new Query(criteria), updater, VacancyDoc.class);
    }

    private void updateMessageDoc(AppointmentEmbedded embedded) {
        String prefix = StringUtil.append(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT.getKey(), Constants.DOT);
        Criteria criteria = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_ID.getKey()).is(embedded.getId());
        multiUpdateEmbedded(criteria, prefix, embedded, MessageDoc.class);
    }

    private void updateAppointmentDetailDoc(AppointmentEmbedded embedded) {
        String prefix = StringUtil.append(AppointmentDetailDocEnum.APPOINTMENT.getKey(), Constants.DOT);
        Criteria criteria = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_ID.getKey()).is(embedded.getId());
        multiUpdateEmbedded(criteria, prefix, embedded, AppointmentDetailDoc.class);
    }

    @Override
    public void delete(List<Long> ids) {
        deleteInVacancyDoc(ids);
        deleteInMessageDoc(ids);
        deleteInAppointmentDetailDoc(ids);
    }

    private void deleteInVacancyDoc(List<Long> ids) {
        Criteria criteria = Criteria.where(VacancyDocEnum.APPOINTMENTS_ID.key()).in(ids);
        Update updater = new Update().set(VacancyDocEnum.APPOINTMENTS_IS_DELETED.key(), true);
        mongoTemplate.updateMulti(new Query(criteria), updater, VacancyDoc.class);
    }

    private void deleteInMessageDoc(List<Long> ids) {
        Criteria criteria = Criteria.where(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_ID.getKey()).in(ids);
        Update updater = new Update().set(MessageDocEnum.APPOINTMENT_DETAIL_MESSAGE_APPOINTMENT_IS_DELETED.getKey(), true);
        mongoTemplate.updateMulti(new Query(criteria), updater, MessageDoc.class);
    }

    private void deleteInAppointmentDetailDoc(List<Long> ids) {
        Criteria criteria = Criteria.where(AppointmentDetailDocEnum.APPOINTMENT_ID.getKey()).in(ids);
        Update updater = new Update().set(AppointmentDetailDocEnum.APPOINTMENT_IS_DELETED.getKey(), true);
        mongoTemplate.updateMulti(new Query(criteria), updater, AppointmentDetailDoc.class);
    }

    private void multiUpdateEmbedded(Criteria criteria, String prefix, AppointmentEmbedded embedded, Class<?> clazz) {
        Map<String, Object> map = MongoInitData.initAppointmentEmbedded(prefix, embedded);
        Optional.ofNullable(map).filter(MapUtils::isNotEmpty).ifPresent(it -> {
            Update update = new Update();
            it.forEach(update::set);
            mongoTemplate.updateMulti( new Query(criteria), update, clazz);
        });

    }
}
