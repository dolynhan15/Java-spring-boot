package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.reposistories.CurrencyRepository;
import com.qooco.boost.data.oracle.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository repository;

    @Override
    public JpaRepository<Currency, Long> getRepository() {
        return repository;
    }

    @Override
    public Currency findByCode(String currencyCode) {
        return repository.findByCode(currencyCode);
    }

    @Override
    public Page<Currency> getCurrencies(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.Direction.ASC, "name"));
    }
}
