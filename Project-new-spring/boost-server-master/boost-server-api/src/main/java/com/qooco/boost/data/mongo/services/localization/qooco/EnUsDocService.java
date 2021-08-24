package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.EnUsDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface EnUsDocService {

    List<EnUsDoc> save(List<EnUsDoc> enUsDocs);

    EnUsDoc save(EnUsDoc enUsDoc);

    EnUsDoc findById(String id);

    EnUsDoc findByLatestCollection(String collection);

    List<EnUsDoc> findByIds(List<String> id);
}
