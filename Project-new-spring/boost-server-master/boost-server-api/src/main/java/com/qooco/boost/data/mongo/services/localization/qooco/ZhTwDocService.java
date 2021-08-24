package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.ZhTwDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface ZhTwDocService {
    ZhTwDoc save(ZhTwDoc zhTwDoc);

    ZhTwDoc findById(String id);

    ZhTwDoc findByLatestCollection(String collection);

    List<ZhTwDoc> save(List<ZhTwDoc> zhTwDocs);
}
