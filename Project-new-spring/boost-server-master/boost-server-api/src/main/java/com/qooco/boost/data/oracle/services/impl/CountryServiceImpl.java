package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.reposistories.CountryRepository;
import com.qooco.boost.data.oracle.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Copyright: Falcon Team - AxonActive
 * User: trungmhv
 * Date: 7/11/2018 - 9:22 PM
 */
@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository repository;

    @Override
    public Page<Country> getCountries(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.Direction.ASC, "countryName"));
    }

    @Override
    public Country findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Country findValidById(Long id) {
        return repository.findValidById(id);
    }
}
