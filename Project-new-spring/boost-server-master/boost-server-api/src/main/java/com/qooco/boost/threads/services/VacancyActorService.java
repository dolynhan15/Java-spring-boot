package com.qooco.boost.threads.services;

import com.qooco.boost.data.oracle.entities.Vacancy;

public interface VacancyActorService {
    Vacancy updateLazyValue(Vacancy vacancy);
}
