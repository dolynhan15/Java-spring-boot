package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.HotelType;
import com.qooco.boost.data.oracle.reposistories.HotelTypeRepository;
import com.qooco.boost.data.oracle.services.HotelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HotelTypeServiceImpl implements HotelTypeService {
    @Autowired
    private HotelTypeRepository repository;

    @Override
    public Page<HotelType> getHotelTypes(int page, int size) {
        return repository.findAll(PageRequest.of(page, size, Sort.Direction.ASC, "hotelTypeName"));
    }

    @Override
    public HotelType findById(long id) {
        return repository.findById(id).orElse(null);
    }
}