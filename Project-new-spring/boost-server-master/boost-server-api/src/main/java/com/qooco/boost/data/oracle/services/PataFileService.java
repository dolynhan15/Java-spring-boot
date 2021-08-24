package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.PataFile;

import java.util.Date;
import java.util.List;

public interface PataFileService {
    PataFile save(PataFile pataFile);

    Iterable<PataFile> save(Iterable<PataFile> pataFileList);

    List<PataFile> findByUserProfile(Long userProfileId);

    PataFile findById(Long id);

    List<PataFile> findByPurposeAndCreatedDate(Integer purpose, Date fromDate);

    Long findOwnerByUrl(String url);

    void delete(Long id);

    void delete(String url);
}
