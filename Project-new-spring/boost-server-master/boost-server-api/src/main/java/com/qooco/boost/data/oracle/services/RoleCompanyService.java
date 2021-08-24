package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.RoleCompany;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/21/2018 - 11:35 AM
 */
public interface RoleCompanyService {
    RoleCompany findByName(String name);

    List<RoleCompany> findAll();

    List<RoleCompany> findByAuthorization(List<String> roles);
}
