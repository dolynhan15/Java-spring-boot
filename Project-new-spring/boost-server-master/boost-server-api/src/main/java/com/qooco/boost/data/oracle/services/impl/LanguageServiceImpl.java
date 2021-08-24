package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.Language;
import com.qooco.boost.data.oracle.reposistories.LanguageRepository;
import com.qooco.boost.data.oracle.services.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    private LanguageRepository repository;

    @Override
    public Page<Language> getLanguages(int page, int size) {
        if (page < 0 || size <= 0) {
            return repository.findAll(PageRequest.of(0, Integer.MAX_VALUE));
        }
        return repository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Boolean exist(Long[] ids) {
        int size = repository.countByIds(ids);
        return size == ids.length;
    }

    @Override
    public List<Language> findByIds(long[] ids) {
        if (ids != null && ids.length > 0) {
            return repository.findByIds(ids);
        }
        return new ArrayList<>();
    }
}
