package com.qooco.boost.business.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qooco.boost.business.QoocoSyncService;
import com.qooco.boost.constants.Api;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.models.qooco.sync.BaseQoocoSyncReq;
import com.qooco.boost.models.qooco.sync.levelTestHistory.GetLevelTestHistoryReq;
import com.qooco.boost.models.qooco.sync.levelTestHistory.GetLevelTestHistoryResponse;
import com.qooco.boost.models.qooco.sync.levelTestScales.GetLevelTestScalesResponse;
import com.qooco.boost.models.qooco.sync.leveltest.GetLevelTestReq;
import com.qooco.boost.models.qooco.sync.leveltest.GetLevelTestResponse;
import com.qooco.boost.models.qooco.sync.leveltestdata.GetLevelTestDataReq;
import com.qooco.boost.models.qooco.sync.leveltestdata.GetLevelTestDataResponse;
import com.qooco.boost.models.qooco.sync.leveltestwizards.GetLevelTestWizardsReq;
import com.qooco.boost.models.qooco.sync.leveltestwizards.GetLevelTestWizardsResponse;
import com.qooco.boost.models.qooco.sync.localization.GetLocalizationReq;
import com.qooco.boost.models.qooco.sync.localization.GetLocalizationResponse;
import com.qooco.boost.models.qooco.sync.localization.LocaleData;
import com.qooco.boost.models.qooco.sync.ownedPackage.OwnedPackageResponse;
import com.qooco.boost.utils.HttpHelper;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 9/24/2018 - 4:51 PM
*/
@Service
public class QoocoSyncServiceImpl implements QoocoSyncService {

    protected Logger log = LogManager.getLogger(QoocoSyncServiceImpl.class);

    private HttpHeaders headers;

    //Service link from other system
    @Value(ApplicationConstant.BOOST_PATA_QOOCO_SYNC_SERVICE_PATH)
    private String qoocoSyncServicePath = "";
    @Value(ApplicationConstant.CAREER_FEATURE_ID)
    private String careerFeatureId = "";
    @Value(ApplicationConstant.HOTEL_FEATURE_ID)
    private String hotelFeatureId = "";
    @Value(ApplicationConstant.SPID)
    private String spid = "";

    @Override
    public OwnedPackageResponse getOwnedPackages(String userId, long timestamp) {
        if (StringUtils.isNotBlank(userId)) {
            String userOwnedPackageUrl = Api.SERVICE_SYNC_DATA.replace(QoocoApiConstants.SPID_DATA, spid)
                    .replace(QoocoApiConstants.USER_ID_DATA, userId)
                    .concat(QoocoApiConstants.OWNED_PACKAGES_TIMESTAMP).concat(String.valueOf(timestamp));
            try {
                return doGet(getQoocoApiPath(userOwnedPackageUrl), OwnedPackageResponse.class);
            } catch (RestClientException ex) {
                log.error("getOwnedPackages = "+ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public GetLevelTestResponse getLevelTest(String userId, long timestamp) {
        if (StringUtils.isNotBlank(userId)) {
            String getLevelTestServicePath = Api.SERVICE_SYNC_DATA.replace(QoocoApiConstants.SPID_DATA, spid)
                    .replace(QoocoApiConstants.USER_ID_DATA, userId);
            GetLevelTestReq request = new GetLevelTestReq();
            BaseQoocoSyncReq reqLocale = new BaseQoocoSyncReq();
            Map<String, Long> localeMap = new HashMap<>();
            localeMap.put(careerFeatureId, timestamp);
            reqLocale.setEnLocale(localeMap);
            request.setGetLevelTests(reqLocale);
            try {
                return doPost(getQoocoApiPath(getLevelTestServicePath), request, GetLevelTestResponse.class);
            } catch (RestClientException ex) {
                log.error("getLevelTest = "+ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public String getQoocoApiPath(String servicePath) {
        return StringUtil.append(qoocoSyncServicePath, servicePath);
    }

    @Override
    public GetLocalizationResponse getLocalization(List<LocaleData> localeData) {
        if (CollectionUtils.isNotEmpty(localeData)) {
            GetLocalizationReq request = new GetLocalizationReq();
            request.setGetLocalizationStrings2(initLocalization(localeData));
            Gson gson = new Gson();
            String localizationReport = gson.toJson(request);
            String getLocalizationPath = Api.SERVICE_SYNC_DATA.replace(QoocoApiConstants.SPID_DATA, spid)
                    .replace(QoocoApiConstants.USER_ID_DATA, QoocoApiConstants.DEFAULT_USER_ID)
                    .concat(QoocoApiConstants.REPORT).concat(localizationReport);

            try {
                return doGet(getQoocoApiPath(getLocalizationPath), GetLocalizationResponse.class);
            } catch (RestClientException ex) {
                log.error("getLocalization = "+ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public GetLevelTestWizardsResponse getLevelTestWizards(long timestamp) {
        GetLevelTestWizardsReq request = new GetLevelTestWizardsReq();
        Gson gson = new Gson();
        BaseQoocoSyncReq reqLocale = new BaseQoocoSyncReq();
        Map<String, Long> localeMap = new HashMap<>();
        localeMap.put(careerFeatureId, timestamp);
        reqLocale.setEnLocale(localeMap);
        request.setGetLevelTestWizards(reqLocale);
        String levelTestWizardsReport = gson.toJson(request);
        String getLevelTestWizardsPath = Api.SERVICE_SYNC_DATA.replace(QoocoApiConstants.SPID_DATA, spid)
                .replace(QoocoApiConstants.USER_ID_DATA, QoocoApiConstants.DEFAULT_USER_ID)
                .concat(QoocoApiConstants.REPORT).concat(levelTestWizardsReport);
        try {
            return doGet(getQoocoApiPath(getLevelTestWizardsPath), GetLevelTestWizardsResponse.class);
        } catch (RestClientException ex) {
            log.error("getLevelTestWizards = "+ex.getMessage());
        }
        return null;
    }

    @Override
    public GetLevelTestScalesResponse getLevelTestScales(long timestamp) {
        String getLevelTestScalesUrl = Api.SERVICE_SYNC_DATA.replace(QoocoApiConstants.SPID_DATA, spid)
                .replace(QoocoApiConstants.USER_ID_DATA, QoocoApiConstants.DEFAULT_USER_ID)
                .concat(QoocoApiConstants.LEVEL_TEST_SCALES_TIMESTAMP).concat(String.valueOf(timestamp));
        try {
            return doGet(getQoocoApiPath(getLevelTestScalesUrl), GetLevelTestScalesResponse.class);
        } catch (RestClientException ex) {
            log.error("getLevelTestScales = "+ex.getMessage());
        }
        return null;
    }

    @Override
    public GetLevelTestDataResponse getLevelTestData(long timestamp) {
        String getLevelTestDataServicePath = Api.SERVICE_SYNC_DATA.replace(QoocoApiConstants.SPID_DATA, spid)
                .replace(QoocoApiConstants.USER_ID_DATA, QoocoApiConstants.DEFAULT_USER_ID);
        GetLevelTestDataReq request = new GetLevelTestDataReq();
        BaseQoocoSyncReq reqLocale = new BaseQoocoSyncReq();
        Map<String, Long> localeMap = new HashMap<>();
        localeMap.put(careerFeatureId, timestamp);
        reqLocale.setEnLocale(localeMap);
        request.setGetLevelTests(reqLocale);
        try {
            return doPost(getQoocoApiPath(getLevelTestDataServicePath), request, GetLevelTestDataResponse.class);
        } catch (RestClientException ex) {
            log.error("getLevelTestData = "+ex.getMessage());
        }
        return null;
    }

    @Override
    public GetLevelTestHistoryResponse getLevelTestHistory(String userId, long timestamp) {
        if (StringUtils.isNotBlank(userId)) {
            String getLevelTestHistoryPath = Api.SERVICE_SYNC_DATA.replace(QoocoApiConstants.SPID_DATA, spid)
                    .replace(QoocoApiConstants.USER_ID_DATA, userId);
            GetLevelTestHistoryReq request = new GetLevelTestHistoryReq();
            BaseQoocoSyncReq reqLocale = new BaseQoocoSyncReq();
            Map<String, Long> localeMap = new HashMap<>();
            localeMap.put(careerFeatureId, timestamp);
            reqLocale.setEnLocale(localeMap);
            request.setGetLevelTestHistory(reqLocale);
            try {
                return doPost(getQoocoApiPath(getLevelTestHistoryPath), request, GetLevelTestHistoryResponse.class);
            } catch (RestClientException ex) {
                log.error("getLevelTestHistory = "+ex.getMessage());
            }
        }
        return null;
    }

    private Map<String, Map<String, Long>> initLocalization(List<LocaleData> localeData) {
        Map<String, Map<String, Long>> getLocalizationStrings = new HashMap<>();
        for (LocaleData locale : localeData) {
            Map<String, Long> map = getLocalizationStrings.get(locale.getCollection());
            if (MapUtils.isEmpty(map)) {
                map = new HashMap<>();
            }
            map.put(locale.getCollection(), locale.getTimestamp());
            getLocalizationStrings.put(locale.getLocale(), map);
        }
        return getLocalizationStrings;
    }

    private <T> T doPost(String url, Object request, Class<T> clazz) {
        if (Objects.isNull(headers)) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        return doPost(url, request, headers, clazz);
    }

    private <T> T doPost(String url, Object request, HttpHeaders headers, Class<T> clazz) {
        Gson gson = new GsonBuilder().create();
        String mapBody = QoocoApiConstants.REPORT_DATA.replace(QoocoApiConstants.REPLACEMENT_REQUEST_BODY, gson.toJson(request));
        return HttpHelper.doPost(url, mapBody, headers, clazz, true);
    }

    private <T> T doGet(String url, Class<T> clazz) {
        if (Objects.isNull(headers)) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        return HttpHelper.doGetParseString(url, clazz, true);
    }
}
