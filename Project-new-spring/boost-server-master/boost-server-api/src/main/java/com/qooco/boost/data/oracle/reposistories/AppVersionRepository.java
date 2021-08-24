package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.AppVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppVersionRepository extends Boot2JpaRepository<AppVersion, Long> {

    //@Query("SELECT a FROM AppVersion a WHERE a.id = :id")
    //Optional<AppVersion> findById(@Param("id") Long id);

    @Query("SELECT count(a.id) FROM AppVersion a WHERE a.appId = :appId AND a.os = :os AND a.isForceUpdate = true AND a.id > (SELECT av.id FROM AppVersion av WHERE av.appVersion = :appVersion AND av.appId = :appId AND av.os = :os)")
    int countForceUpdate(@Param("appVersion") Integer appVersion, @Param("appId") String appId, @Param("os") String os);

    @Query("SELECT a FROM AppVersion a WHERE a.appId = :appId AND a.os = :os ORDER BY a.id DESC")
    Page<AppVersion> getLatestVersions(@Param("appId") String appId, @Param("os") String os, Pageable pageable);

    @Query("SELECT COUNT(av.appVersion), SUM(av.isForceUpdate), MAX(av.appVersion) FROM AppVersion av WHERE av.appVersion > :appVersion AND av.appId = :appId AND av.os = :os ORDER BY av.id ASC ")
    List<Object[]> getAppVersion(@Param("appVersion") Integer appVersion, @Param("appId") String appId, @Param("os") String os);
}
