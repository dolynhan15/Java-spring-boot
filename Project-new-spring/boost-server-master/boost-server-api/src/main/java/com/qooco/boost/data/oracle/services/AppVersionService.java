package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.AppVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppVersionService {
    AppVersion save(AppVersion appVersion);

    AppVersion findById(Long id);

    List<AppVersion> findAll();

    int countForceUpdate(Integer appVersion, String appId, String os);

    Page<AppVersion> getLatestVersions(String appId, String os, Pageable pageable);

    List<Object[]> getAppVersion(Integer appVersion, String appId, String os);
}
