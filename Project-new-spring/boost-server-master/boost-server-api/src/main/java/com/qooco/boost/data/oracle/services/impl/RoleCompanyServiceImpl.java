package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.RoleCompany;
import com.qooco.boost.data.oracle.reposistories.RoleCompanyRepository;
import com.qooco.boost.data.oracle.services.RoleCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/21/2018 - 11:35 AM
 */
@Service
public class RoleCompanyServiceImpl implements RoleCompanyService {
    @Autowired
    private RoleCompanyRepository repository;

    @Override
    public RoleCompany findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<RoleCompany> findAll() {
        return Lists.newArrayList(repository.findAll().iterator());
    }

    @Override
    public List<RoleCompany> findByAuthorization(List<String> roles) {
        return repository.findByNames(roles);
    }
}
