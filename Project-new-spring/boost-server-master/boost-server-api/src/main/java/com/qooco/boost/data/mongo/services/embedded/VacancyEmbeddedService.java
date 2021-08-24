package com.qooco.boost.data.mongo.services.embedded;

import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;

import java.util.List;

public interface VacancyEmbeddedService {
    void update(VacancyEmbedded embedded);
    void update(List<VacancyEmbedded> embeddeds);
}
