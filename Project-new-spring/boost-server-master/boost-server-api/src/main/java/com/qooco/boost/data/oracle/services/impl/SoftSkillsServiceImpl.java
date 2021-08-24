package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.SoftSkill;
import com.qooco.boost.data.oracle.reposistories.SoftSkillsRepository;
import com.qooco.boost.data.oracle.services.SoftSkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 6/20/2018 - 12:46 PM
*/
@Service
@Transactional
public class SoftSkillsServiceImpl implements SoftSkillsService {

    @Autowired
    private SoftSkillsRepository softSkillsRepository;

    @Override
    public List<SoftSkill> getAll() {
        return Lists.newArrayList(softSkillsRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
    }

    @Override
    public List<SoftSkill> findByIds(long[] softSkillIds) {
        if (softSkillIds != null && softSkillIds.length > 0) {
            return softSkillsRepository.findByIds(softSkillIds);
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean exist(Long[] ids) {
        int size = softSkillsRepository.countByIds(ids);
        return size == ids.length;
    }
}
