package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Province;
import com.qooco.boost.data.oracle.reposistories.CityRepository;
import com.qooco.boost.data.oracle.services.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CityServiceImpl implements CityService {
    @Autowired
    private CityRepository repository;

    @Override
    public JpaRepository getRepository() {
        return repository;
    }

    @Override
    public Page<City> getCities(int page, int size, Long provinceId) {
        return repository.findAllByProvince(new Province(provinceId), PageRequest.of(page, size, Sort.Direction.ASC, "cityName"));
    }

    @Override
    public City findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public City findValidById(Long id) {
        return repository.findValidById(id);
    }

    @Override
    public Boolean exists(Long id) {
        return repository.existsById(id);
    }
}