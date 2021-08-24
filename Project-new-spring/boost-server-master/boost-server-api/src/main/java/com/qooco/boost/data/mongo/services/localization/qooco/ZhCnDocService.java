package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.ZhCnDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface ZhCnDocService {
    ZhCnDoc save(ZhCnDoc zhCnDoc);

    ZhCnDoc findById(String id);

    ZhCnDoc findByLatestCollection(String collection);

    List<ZhCnDoc> save(List<ZhCnDoc> zhCnDocs);
}
