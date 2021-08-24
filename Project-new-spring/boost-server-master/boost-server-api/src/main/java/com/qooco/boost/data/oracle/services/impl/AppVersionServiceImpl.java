package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.AppVersion;
import com.qooco.boost.data.oracle.reposistories.AppVersionRepository;
import com.qooco.boost.data.oracle.services.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppVersionServiceImpl implements AppVersionService {
    @Autowired
    private AppVersionRepository appVersionRepository;
    @Override
    public AppVersion save(AppVersion appVersion) {
        return appVersionRepository.save(appVersion);
    }

    @Override
    public AppVersion findById(Long id) {
        return appVersionRepository.findById(id).orElse(null);
    }

    @Override
    public List<AppVersion> findAll() {
        return Lists.newArrayList(appVersionRepository.findAll());
    }

    @Override
    public int countForceUpdate(Integer appVersion, String appId, String os) {
        return appVersionRepository.countForceUpdate(appVersion, appId, os);
    }

    @Override
    public Page<AppVersion> getLatestVersions(String appId, String os, Pageable pageable) {
        return appVersionRepository.getLatestVersions(appId, os, pageable);
    }

    @Override
    public List<Object[]> getAppVersion(Integer appVersion, String appId, String os) {
        return appVersionRepository.getAppVersion(appVersion, appId, os);
    }
}
