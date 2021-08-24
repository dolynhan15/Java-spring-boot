package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.WizardSkinDoc;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/9/2018 - 10:42 AM
*/
public interface WizardSkinDocService {
    WizardSkinDoc save(WizardSkinDoc wizardSkinDoc);

    WizardSkinDoc findById(Long id);

    List<WizardSkinDoc> save(List<WizardSkinDoc> wizardSkinDocs);

    List<WizardSkinDoc> findByTestIds(List<Long> testId);

    WizardSkinDoc findByLatestWizardSkin();
}
