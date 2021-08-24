package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.MsMyDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface MsMyDocService {
    MsMyDoc save(MsMyDoc msMyDoc);

    MsMyDoc findById(String id);

    MsMyDoc findByLatestCollection(String collection);

    List<MsMyDoc> save(List<MsMyDoc> msMyDocs);
}
