package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.entities.Province;
import com.qooco.boost.data.oracle.reposistories.ProvinceRepository;
import com.qooco.boost.data.oracle.services.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    @Autowired
    private ProvinceRepository repository;

    @Override
    public Page<Province> getProvinces(int page, int size, Long countryId) {
        return repository.findAllByCountry(new Country(countryId), PageRequest.of(page, size, Sort.Direction.ASC, "name"));
    }

    @Override
    public Province findById(Long provinceId) {
        return repository.findByProvinceId(provinceId);
    }
}