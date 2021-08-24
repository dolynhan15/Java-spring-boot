package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Province;
import org.springframework.data.domain.Page;

public interface ProvinceService {
    Page<Province> getProvinces(int page, int size, Long countryId);

    Province findById(Long provinceId);
}