package com.qooco.boost.data.mongo.services.embedded;

import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import org.springframework.stereotype.Service;

@Service
public interface CompanyEmbeddedService {
    void update(CompanyEmbedded embedded);
}
