package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.SoftSkill;
import com.qooco.boost.data.oracle.reposistories.SoftSkillsRepository;
import com.qooco.boost.data.oracle.services.SoftSkillsService;
import com.qooco.boost.data.oracle.services.impl.SoftSkillsServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 7/16/2018 - 3:24 PM
 */
@RunWith(PowerMockRunner.class)
public class SoftSkillsServiceImplTest {

    @InjectMocks
    private SoftSkillsService softSkillsService = new SoftSkillsServiceImpl();

    @Mock
    private SoftSkillsRepository softSkillsRepository = Mockito.mock(SoftSkillsRepository.class);

    @Test
    public void getAll() {
        List<SoftSkill> softSkills = new ArrayList<>();
        softSkills.add(new SoftSkill(1L, "Teamwork"));
        softSkills.add(new SoftSkill(2L, "Creativity"));

        Mockito.when(softSkillsRepository.findAll(new Sort(Sort.Direction.ASC, "name")))
                .thenReturn(softSkills);

        List<SoftSkill> actualResp = softSkillsService.getAll();

        Assert.assertEquals(softSkills.size(), actualResp.size());
    }

    @Test
    public void getAll_whenListIsEmpty_thenReturnEmptyList() {
        Mockito.when(softSkillsRepository.findAll(new Sort(Sort.Direction.ASC, "name"))).thenReturn(new ArrayList<>());
        Assert.assertEquals(new ArrayList<>(), softSkillsService.getAll());
    }

    @Test
    public void getAll_whenNoParamRequest_thenReturnAllSoftSkillList() {
        List<SoftSkill> softSkillList = new ArrayList<>();
        softSkillList.add(new SoftSkill(1L));
        softSkillList.add(new SoftSkill(2L));

        Mockito.when(softSkillsRepository.findAll(new Sort(Sort.Direction.ASC, "name"))).thenReturn(softSkillList);
        Assert.assertEquals(softSkillList, softSkillsService.getAll());
    }
}