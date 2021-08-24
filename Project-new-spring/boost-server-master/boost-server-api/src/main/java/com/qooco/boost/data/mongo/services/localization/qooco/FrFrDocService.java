package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.FrFrDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface FrFrDocService {
    FrFrDoc save(FrFrDoc frFrDoc);

    FrFrDoc findById(String id);

    FrFrDoc findByLatestCollection(String collection);

    List<FrFrDoc> save(List<FrFrDoc> frFrDocs);
}
