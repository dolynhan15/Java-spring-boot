package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Language;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LanguageService {

    Page<Language> getLanguages(int page, int size);

    List<Language> findByIds(long[] ids);

    Boolean exist(Long[] ids);
}
