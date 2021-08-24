package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Education;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/4/2018 - 8:32 AM
*/
public interface EducationService {
    Education findById(long educationId);

    List<Education> getAll();

    Boolean exists(Long id);
}
