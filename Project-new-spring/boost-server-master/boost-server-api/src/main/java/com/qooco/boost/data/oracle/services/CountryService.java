package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Country;
import org.springframework.data.domain.Page;

public interface CountryService {
    Page<Country> getCountries(int page, int size);
    Country findById(Long id);
    Country findValidById(Long id);
}
