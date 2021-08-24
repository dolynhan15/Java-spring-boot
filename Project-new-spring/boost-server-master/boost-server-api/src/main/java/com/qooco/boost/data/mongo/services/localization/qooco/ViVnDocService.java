package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.ViVnDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface ViVnDocService {
    ViVnDoc save(ViVnDoc viVnDoc);

    ViVnDoc findById(String id);

    ViVnDoc findByLatestCollection(String collection);

    List<ViVnDoc> save(List<ViVnDoc> viVnDocs);
}
