package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.EsMxDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface EsMxDocService {
    EsMxDoc save(EsMxDoc esMxDoc);

    EsMxDoc findById(String id);

    EsMxDoc findByLatestCollection(String collection);

    List<EsMxDoc> save(List<EsMxDoc> esMxDocs);
}
