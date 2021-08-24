package com.example.demo.service.impl;

import com.example.demo.constant.ResponseStatus;
import com.example.demo.exception.InvalidParamException;
import com.example.demo.model.BaseResp;
import com.example.demo.model.dto.WorkDTO;
import com.example.demo.model.entity.Work;
import com.example.demo.model.request.WorkRequest;
import com.example.demo.model.result.PagedResult;
import com.example.demo.repository.WorkRepository;
import com.example.demo.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.demo.constant.ResponseStatus.*;
import static com.example.demo.constant.WorkStatus.*;

@Transactional
@Service
public class WorkServiceImpl implements WorkService {
    @Autowired
    private WorkRepository workRepository;

    public WorkServiceImpl(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Override
    public BaseResp addWork(WorkRequest workRequest) {
        validateWork(workRequest);
        Work work = Work.builder().workName(workRequest.getWorkName()).startingDate(workRequest.getStartingDate()).endingDate(workRequest.getEndingDate()).status(workRequest.getStatus()).isDeleted(false).build();
        Work result = workRepository.save(work);
        return new BaseResp<>(new WorkDTO(result));
    }

    @Override
    public BaseResp editWork(Integer id, WorkRequest workRequest) {
        if (Objects.isNull(id)) throw new InvalidParamException(ResponseStatus.REQUIRED_ID);
        validateWork(workRequest);
        Work oldWork = workRepository.findExistedById(id);
        if (Objects.isNull(oldWork)) throw new InvalidParamException(ResponseStatus.NOT_FOUND_WORK);
        Work editedWork = Work.builder().id(oldWork.getId()).workName(workRequest.getWorkName()).startingDate(workRequest.getStartingDate()).endingDate(workRequest.getEndingDate()).status(workRequest.getStatus()).build();
        Work result = workRepository.save(editedWork);
        return new BaseResp<>(new WorkDTO(result));
    }

    @Override
    public BaseResp deleteWork(Integer id) {
        if (Objects.isNull(id)) throw new InvalidParamException(ResponseStatus.REQUIRED_ID);
        Work work = workRepository.findExistedById(id);
        if (Objects.isNull(work)) throw new InvalidParamException(ResponseStatus.NOT_FOUND_WORK);
        work.setDeleted(true);
        workRepository.save(work);
        return new BaseResp<>();
    }

    @Override
    public BaseResp getWorks(int page, int size) {
        Page<Work> works =  workRepository.getWorks(PageRequest.of(page, size, Sort.Direction.ASC, Work.Fields.workName));
        List<WorkDTO> workDTOs = works.getContent().stream().map(WorkDTO::new).collect(Collectors.toList());
        PagedResult<WorkDTO> workPage = new PagedResult<>(workDTOs, page, works.getSize(), works.getTotalPages(), works.getTotalElements(), works.hasNext(), works.hasPrevious());
        return new BaseResp<>(workPage);
    }

    private void validateWork(WorkRequest workRequest) {
        if (StringUtils.isEmpty(workRequest.getWorkName())) throw new InvalidParamException(REQUIRED_WORK_NAME);
        if (Objects.isNull(workRequest.getStartingDate())) throw new InvalidParamException(REQUIRED_STARTING_DATE);
        if (Objects.isNull(workRequest.getEndingDate())) throw new InvalidParamException(REQUIRED_ENDING_DATE);
        if (workRequest.getStartingDate().after(workRequest.getEndingDate())) throw new InvalidParamException(REQUIRED_START_DATE_BEFORE_END_DATE);
        if (workRequest.getStatus() == 0) throw new InvalidParamException(REQUIRED_STATUS);
        if (!Arrays.asList(PLANNING, DOING, COMPLETE).contains(workRequest.getStatus())) throw new InvalidParamException(STATUS_INVALID);
    }
}
