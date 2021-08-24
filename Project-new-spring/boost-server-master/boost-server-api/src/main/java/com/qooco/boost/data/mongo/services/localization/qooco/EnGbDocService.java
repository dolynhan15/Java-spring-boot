package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.EnGbDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface EnGbDocService {
    EnGbDoc save(EnGbDoc enGbDoc);

    EnGbDoc findById(String id);

    EnGbDoc findByLatestCollection(String collection);

    List<EnGbDoc> save(List<EnGbDoc> enGbDocs);

    List<EnGbDoc> findByIds(List<String> id);
}
