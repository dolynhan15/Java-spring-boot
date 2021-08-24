package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.model.count.CountByCompany;
import com.qooco.boost.data.oracle.entities.Location;
import com.qooco.boost.data.oracle.reposistories.LocationRepository;
import com.qooco.boost.data.oracle.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository repository;

    @Override
    public Location findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Location save(Location location) {
        return repository.save(location);
    }

    @Override
    public List<Location> save(List<Location> locations) {
        return Lists.newArrayList(repository.saveAll(locations));
    }

    @Override
    public Location findByCompanyIdAndCityIdAndNullAddress(Long companyId, Long cityId) {
        return repository.findByCompanyIdAndCityIdAndNullAddress(companyId, cityId);
    }

    @Override
    public Location findByCompanyIdAndCityIdAndAddress(Long companyId, Long cityId, String address) {
        return repository.findByCompanyIdAndCityIdAndAddress(companyId, cityId, address);
    }

    @Override
    public List<Location> findByCompanyIdAndCityId(Long companyId, List<Long> cityIds) {
        return repository.findByCompanyIdAndCityId(companyId, cityIds);
    }

    @Override
    public List<Location> findByIds(List<Long> id) {
        return Lists.newArrayList(repository.findAllById(id));
    }

    @Override
    public List<Location> findByIdsAndCompanyId(Long companyId, List<Long> ids) {
        return repository.findByIdsAndCompanyId(companyId, ids);
    }

    @Override
    public List<Location> findByCompanyId(Long companyId) {
        return repository.findAllByCompanyCompanyId(companyId);
    }

    @Override
    public int countByCompany(Long companyId) {
        return repository.countByCompany(companyId);
    }

    @Override
    public List<CountByCompany> findAllByCompanyCompanyIds(List<Long> companyIds) {
        return repository.findAllByCompanyCompanyIds(companyIds);
    }

    @Override
    public Page<Location> findByCompanyId(long companyId, int page, int size) {
        PageRequest pageRequest;
        if (page < 0 || size <= 0) {
            pageRequest  = PageRequest.of(0, Integer.MAX_VALUE);
        } else {
            pageRequest  = PageRequest.of(page, size);
        }
        return repository.findByCompany(companyId, pageRequest);
    }

    @Override
    public void updateNonePrimaryForCompany(long companyId) {
        repository.updateNonePrimaryForCompany(companyId);
    }

}
