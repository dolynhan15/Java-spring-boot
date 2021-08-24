package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.SoftSkill;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 6/20/2018 - 11:59 AM
*/
public interface SoftSkillsService {

    List<SoftSkill> getAll();

    List<SoftSkill> findByIds(long[] softSkillId);

    Boolean exist(Long[] ids);

}
