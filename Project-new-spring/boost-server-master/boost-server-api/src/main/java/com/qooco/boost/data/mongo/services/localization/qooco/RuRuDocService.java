package com.qooco.boost.data.mongo.services.localization.qooco;

import com.qooco.boost.data.mongo.entities.localization.qooco.RuRuDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/26/2018 - 3:33 PM
*/
public interface RuRuDocService {
    RuRuDoc save(RuRuDoc ruRuDoc);

    RuRuDoc findById(String id);

    RuRuDoc findByLatestCollection(String collection);

    List<RuRuDoc> save(List<RuRuDoc> ruRuDocs);
}
