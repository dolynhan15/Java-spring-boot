package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Benefit;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BenefitService {
    Page<Benefit> getBenefits(int page, int size);

    List<Benefit> findByIds(long[] ids);

    Boolean exist(Long[] ids);
}
