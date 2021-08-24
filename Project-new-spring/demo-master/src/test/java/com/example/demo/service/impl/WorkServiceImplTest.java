package com.example.demo.service.impl;

import com.example.demo.exception.InvalidParamException;
import com.example.demo.model.entity.Work;
import com.example.demo.model.request.WorkRequest;
import com.example.demo.repository.WorkRepository;
import com.example.demo.service.WorkService;
import org.assertj.core.util.DateUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;

import static com.example.demo.constant.ResponseStatus.*;

@RunWith(SpringRunner.class)
class WorkServiceImplTest {
    @Mock
    private WorkRepository workRepository = Mockito.mock(WorkRepository.class);
    @InjectMocks
    private WorkService workService = new WorkServiceImpl(workRepository);
    @Rule
    private ExpectedException thrown = ExpectedException.none();

    //add work
    @Test
    void addWork_whenWorkNameIsEmpty_thenReturnRequiredWorkNameException() {
        try {
            workService.addWork(new WorkRequest());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_WORK_NAME.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void addWork_whenStartingDateIsEmpty_thenReturnRequiredStartingDateException() {
        try {
            workService.addWork(new WorkRequest().builder().workName("Web dev").build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_STARTING_DATE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void addWork_whenEndingDateIsEmpty_thenReturnRequiredEndingDateException() {
        try {
            workService.addWork(new WorkRequest().builder().workName("Web dev").startingDate(new Date()).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_ENDING_DATE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void addWork_whenEndingDateIsBeforeStartingDate_thenReturnRequiredStartingDateBeforeEndingDateException() {
        try {
            workService.addWork(new WorkRequest().builder().workName("Web dev").startingDate(new Date()).endingDate(DateUtil.yesterday()).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_START_DATE_BEFORE_END_DATE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void addWork_whenStatusIsZero_thenReturnRequiredStatusException() {
        try {
            workService.addWork(new WorkRequest().builder().workName("Web dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_STATUS.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void addWork_whenStatusIsInvalid_thenReturnStatusIsInvalidException() {
        try {
            workService.addWork(new WorkRequest().builder().workName("Web dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).status(4).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(STATUS_INVALID.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void addWork_whenWorkRequestIsValid_thenReturnSuccess() {
        WorkRequest workRequest = new WorkRequest().builder().workName("Web dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).status(1).build();
        mockitoAddWork(workRequest);
        Assert.assertEquals(SUCCESS.getCode(), workService.addWork(workRequest).getCode());
    }

    //edit work
    @Test
    void editWork_whenIdIsEmpty_thenReturnRequiredIdException() {
        try {
            workService.editWork(null, new WorkRequest());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_ID.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenWorkNameIsEmpty_thenReturnRequiredWorkNameException() {
        try {
            workService.editWork(1, new WorkRequest());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_WORK_NAME.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenStartingDateIsEmpty_thenReturnRequiredStartingDateException() {
        try {
            workService.editWork(1, new WorkRequest().builder().workName("Web dev").build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_STARTING_DATE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenEndingDateIsEmpty_thenReturnRequiredEndingDateException() {
        try {
            workService.editWork(1, new WorkRequest().builder().workName("Web dev").startingDate(new Date()).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_ENDING_DATE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenEndingDateIsBeforeStartingDate_thenReturnRequiredStartingDateBeforeEndingDateException() {
        try {
            workService.editWork(1, new WorkRequest().builder().workName("Web dev").startingDate(new Date()).endingDate(DateUtil.yesterday()).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_START_DATE_BEFORE_END_DATE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenStatusIsZero_thenReturnRequiredStatusException() {
        try {
            workService.editWork(1, new WorkRequest().builder().workName("Web dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_STATUS.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenStatusIsInvalid_thenReturnStatusIsInvalidException() {
        try {
            workService.editWork(1, new WorkRequest().builder().workName("Web dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).status(4).build());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(STATUS_INVALID.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenNotFoundOldWork_thenReturnNotFoundWorkException() {
        WorkRequest workRequest = new WorkRequest().builder().workName("Web angular dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).status(2).build();
        Mockito.when(workRepository.findExistedById(1)).thenReturn(null);
        try {
            workService.editWork(1, workRequest);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(NOT_FOUND_WORK.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void editWork_whenWorkRequestIsValid_thenReturnSuccess() {
        Integer id = 1;
        WorkRequest workRequest = new WorkRequest().builder().workName("Web angular dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).status(2).build();
        mockitoEditWork(id, workRequest);
        Assert.assertEquals(SUCCESS.getCode(), workService.editWork(id, workRequest).getCode());
    }

    //delete work
    @Test
    void deleteWork_whenIdIsEmpty_thenReturnRequiredIdException() {
        try {
            workService.deleteWork(null);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(REQUIRED_ID.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void deleteWork_whenNotFoundOldWork_thenReturnNotFoundWorkException() {
        WorkRequest workRequest = new WorkRequest().builder().workName("Web angular dev").startingDate(new Date()).endingDate(DateUtil.tomorrow()).status(2).build();
        Mockito.when(workRepository.findExistedById(1)).thenReturn(null);
        try {
            workService.deleteWork(2);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(NOT_FOUND_WORK.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    void deleteWork_whenWorkRequestIsValid_thenReturnSuccess() {
        Integer id = 2;
        mockitoDeleteWork(id);
        Assert.assertEquals(SUCCESS.getCode(), workService.deleteWork(id).getCode());
    }

    //get work
    @Test
    void getWorks_whenPageAndSizeAreValid_thenReturnSuccess() {
        int page = 0;
        int size = 10;
        mockitoGetWork(page, size);
        Assert.assertEquals(SUCCESS.getCode(), workService.getWorks(page, size).getCode());
    }

    /* ============================ Prepare for testing ================================== */
    private void mockitoAddWork(WorkRequest workRequest) {
        Work work = Work.builder()
                .workName(workRequest.getWorkName())
                .startingDate(workRequest.getStartingDate())
                .endingDate(workRequest.getEndingDate())
                .status(workRequest.getStatus())
                .isDeleted(false)
                .build();
        Work workSaved = new Work(1, work.getWorkName(), work.getStartingDate(), work.getEndingDate(), work.getStatus(), work.isDeleted());
        Mockito.when(workRepository.save(work)).thenReturn(workSaved);
    }

    private void mockitoEditWork(Integer id, WorkRequest workRequest) {
        Work oldWork = Work.builder().id(1).build();
        Work work = Work.builder()
                .id(id)
                .workName(workRequest.getWorkName())
                .startingDate(workRequest.getStartingDate())
                .endingDate(workRequest.getEndingDate())
                .status(workRequest.getStatus())
                .isDeleted(false)
                .build();
        Mockito.when(workRepository.findExistedById(id)).thenReturn(oldWork);
        Mockito.when(workRepository.save(work)).thenReturn(work);
    }

    private void mockitoDeleteWork(Integer id) {
        Work oldWork = Work.builder().id(2).build();
        Mockito.when(workRepository.findExistedById(id)).thenReturn(oldWork);
        oldWork.setDeleted(true);
        Mockito.when(workRepository.save(oldWork)).thenReturn(oldWork);
    }

    private void mockitoGetWork(int page, int size) {
        Page<Work> works = new PageImpl<>(Arrays.asList(new Work(1)));
        Mockito.when(workRepository.getWorks(PageRequest.of(page, size, Sort.Direction.ASC, Work.Fields.workName))).thenReturn(works);
    }

}