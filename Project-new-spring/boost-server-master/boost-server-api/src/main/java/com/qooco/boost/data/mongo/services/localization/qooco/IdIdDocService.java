package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.IdIdDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface IdIdDocService {
    IdIdDoc save(IdIdDoc idIdDoc);

    IdIdDoc findById(String id);

    IdIdDoc findByLatestCollection(String collection);

    List<IdIdDoc> save(List<IdIdDoc> idIdDocs);
}
