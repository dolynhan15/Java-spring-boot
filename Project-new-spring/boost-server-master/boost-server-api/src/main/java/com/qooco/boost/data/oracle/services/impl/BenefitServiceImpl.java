package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.Benefit;
import com.qooco.boost.data.oracle.reposistories.BenefitRepository;
import com.qooco.boost.data.oracle.services.BenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BenefitServiceImpl implements BenefitService {

    @Autowired
    private BenefitRepository repository;

    @Override
    public Page<Benefit> getBenefits(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.Direction.ASC, "name"));
    }

    @Override
    public List<Benefit> findByIds(long[] ids) {
        if (ids != null && ids.length > 0) {
            return repository.findByIds(ids);
        }
        return new ArrayList<>();
    }

    @Override
    public Boolean exist(Long[] ids) {
        int size = repository.countByIds(ids);
        return size == ids.length;
    }
}
