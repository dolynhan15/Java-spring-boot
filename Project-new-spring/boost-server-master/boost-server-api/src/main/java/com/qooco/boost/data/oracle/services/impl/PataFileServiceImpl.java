package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.PataFile;
import com.qooco.boost.data.oracle.reposistories.PataFileRepository;
import com.qooco.boost.data.oracle.services.PataFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PataFileServiceImpl implements PataFileService {
    @Autowired
    private PataFileRepository repository;

    @Override
    public List<PataFile> findByUserProfile(Long userProfileId) {
        return repository.findByUserProfileId(userProfileId);
    }

    @Override
    public Long findOwnerByUrl(String url) {
        return repository.findOwnerByUrl(url);
    }

    @Override
    public PataFile findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<PataFile> findByPurposeAndCreatedDate(Integer purpose, Date fromDate) {
        return repository.findByPurposeAndCreatedDate(purpose, fromDate);
    }

    @Override
    public PataFile save(PataFile pataFile) {
        return repository.save(pataFile);
    }

    @Override
    @Transactional
    public Iterable<PataFile> save(Iterable<PataFile> pataFileList) {
        return repository.saveAll(pataFileList);
    }

    @Override
    public void delete(Long id){
        repository.deleteById(id);
    }

    @Override
    public void delete(String url) {
        repository.deleteByUrl(url);
    }
}
