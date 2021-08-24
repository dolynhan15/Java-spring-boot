package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.AppointmentDetailNotifyDoc;

import java.util.List;

public interface AppointmentDetailNotifyDocService {
    void save(AppointmentDetailNotifyDoc doc);
    List<AppointmentDetailNotifyDoc> findByUserProfileAndCompany(Long userProfileId, Long companyId);
    List<AppointmentDetailNotifyDoc> findByUserProfileAndCompany(Long staffId);
}
