package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.ArSaDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface ArSaDocService {
    ArSaDoc save(ArSaDoc arSaDoc);

    ArSaDoc findById(String id);

    ArSaDoc findByLatestCollection(String collection);

    List<ArSaDoc> save(List<ArSaDoc> arSaDocs);
}
