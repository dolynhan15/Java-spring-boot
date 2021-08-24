package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.PataFile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface PataFileRepository extends Boot2JpaRepository<PataFile, Long> {
    List<PataFile> findByUserProfileId(Long userProfileId);

    @Transactional(readOnly = true)
    @Query("SELECT pf.userProfileId FROM PataFile pf WHERE pf.url LIKE :url AND ROWNUM = 1")
    Long findOwnerByUrl(@Param("url") String url);

    @Query("SELECT pf FROM PataFile pf WHERE pf.purpose = :purpose AND pf.createdDate < :fromDate")
    List<PataFile> findByPurposeAndCreatedDate(@Param("purpose") Integer purpose, @Param("fromDate") Date fromDate);

    @Transactional
    @Modifying
    @Query("DELETE FROM PataFile pf WHERE pf.url LIKE :url")
    void deleteByUrl(@Param("url") String url);
}
