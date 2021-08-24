package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessAppVersionService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.AppVersion;
import com.qooco.boost.data.oracle.services.AppVersionService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.AppVersionDTO;
import com.qooco.boost.models.request.AppVersionReq;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BusinessAppVersionServiceImpl implements BusinessAppVersionService {

    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private AppVersionService appVersionService;

    @Override
    public BaseResp getLatestVersion(Integer appVersion, String appId, String os) {
        if (Objects.isNull(appVersion)) {
            throw new InvalidParamException(ResponseStatus.GET_LATEST_APP_VERSION_INVALID_APP_VERSION);
        }
        if (StringUtil.isEmpty(appId)) {
            throw new InvalidParamException(ResponseStatus.GET_LATEST_APP_VERSION_INVALID_APP_ID);
        }
        if (StringUtil.isEmpty(os)) {
            throw new InvalidParamException(ResponseStatus.GET_LATEST_APP_VERSION_INVALID_OS);
        }
        AppVersionDTO appVersionDTO = new AppVersionDTO(appVersion, appId, os);
        List<Object[]> appVer = appVersionService.getAppVersion(appVersion, appId, os);
        final int COUNT_VERSION_INDEX = 0;
        final int SUM_OF_IS_FORCED_INDEX = 1;
        final int LASTED_VERSION_INDEX = 2;
        final int GET_FIRST_ITEM_INDEX = 0;

        if (CollectionUtils.isNotEmpty(appVer)) {
            Integer lastedVersion = (Integer) appVer.get(GET_FIRST_ITEM_INDEX)[LASTED_VERSION_INDEX];
            if (Objects.nonNull(lastedVersion)) {
                appVersionDTO.setAppVersion(lastedVersion);
            }
            long countVersion = (long) appVer.get(GET_FIRST_ITEM_INDEX)[COUNT_VERSION_INDEX];
            Boolean sumOfIsForced = (Boolean) appVer.get(GET_FIRST_ITEM_INDEX)[SUM_OF_IS_FORCED_INDEX];
            if (countVersion > 0) {
                appVersionDTO.setHasNewVersion(true);
                appVersionDTO.setForceUpdate(Objects.nonNull(sumOfIsForced) && sumOfIsForced);
            }
        }
        return new BaseResp<>(appVersionDTO);
    }

    @Override
    public BaseResp saveAppVersion(AppVersionReq request, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        businessValidatorService.checkUserProfileIsRootAdmin(user.getId());
        AppVersion appVersion = null;
        if (Objects.nonNull(request.getId())) {
            appVersion = appVersionService.findById(request.getId());
            if (Objects.isNull(appVersion)) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND);
            }
        }
        appVersion = request.updateEntity(appVersion);
        appVersion = appVersionService.save(appVersion);
        return new BaseResp<>(appVersion);
    }

    @Override
    public BaseResp getAll(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        businessValidatorService.checkUserProfileIsRootAdmin(user.getId());
        List<AppVersion> result = appVersionService.findAll();
        List<AppVersionDTO> appVersionDTOS = result.stream().map(AppVersionDTO::new).collect(Collectors.toList());
        return new BaseResp<>(appVersionDTOS);
    }
}
