package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.DeDeDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface DeDeDocService {
    DeDeDoc save(DeDeDoc deDeDoc);

    DeDeDoc findById(String id);

    DeDeDoc findByLatestCollection(String collection);

    List<DeDeDoc> save(List<DeDeDoc> deDeDocs);
}
