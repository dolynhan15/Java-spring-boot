package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.City;
import org.springframework.data.domain.Page;

public interface CityService extends EntryService<City, Long>{
    Page<City> getCities(int page, int size, Long provinceId);

    City findValidById(Long id);

    Boolean exists(Long id);
}