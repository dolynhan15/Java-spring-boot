package com.qooco.boost.business;

import com.qooco.boost.models.qooco.sync.levelTestHistory.GetLevelTestHistoryResponse;
import com.qooco.boost.models.qooco.sync.levelTestScales.GetLevelTestScalesResponse;
import com.qooco.boost.models.qooco.sync.leveltest.GetLevelTestResponse;
import com.qooco.boost.models.qooco.sync.leveltestdata.GetLevelTestDataResponse;
import com.qooco.boost.models.qooco.sync.leveltestwizards.GetLevelTestWizardsResponse;
import com.qooco.boost.models.qooco.sync.localization.GetLocalizationResponse;
import com.qooco.boost.models.qooco.sync.localization.LocaleData;
import com.qooco.boost.models.qooco.sync.ownedPackage.OwnedPackageResponse;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/24/2018 - 4:48 PM
*/
public interface QoocoSyncService {
    OwnedPackageResponse getOwnedPackages(String userId, long timestamp);
    GetLevelTestResponse getLevelTest(String userId, long timestamp);
    String getQoocoApiPath(String servicrPath);
    GetLocalizationResponse getLocalization(List<LocaleData> localeData);
    GetLevelTestWizardsResponse getLevelTestWizards(long timestamp);
    GetLevelTestScalesResponse getLevelTestScales(long timestamp);
    GetLevelTestDataResponse getLevelTestData(long timestamp);
    GetLevelTestHistoryResponse getLevelTestHistory(String userId, long timestamp);
}
