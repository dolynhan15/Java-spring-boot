package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.Education;
import com.qooco.boost.data.oracle.reposistories.EducationRepository;
import com.qooco.boost.data.oracle.services.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/4/2018 - 8:33 AM
*/
@Service
public class EducationServiceImpl implements EducationService {

    @Autowired
    private EducationRepository educationRepository;

    @Override
    public Education findById(long educationId) {
        return educationRepository.findById(educationId).orElse(null);
    }

    @Override
    public List<Education> getAll() {
        return Lists.newArrayList(educationRepository.findAll(Sort.by(Sort.Direction.ASC, "educationId")));
    }

    @Override
    public Boolean exists(Long id) {
        return educationRepository.existsById(id);
    }
}
