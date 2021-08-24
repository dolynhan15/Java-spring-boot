package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.HotelType;
import org.springframework.data.domain.Page;

public interface HotelTypeService {
    Page<HotelType> getHotelTypes(int page, int size);

    HotelType findById(long id);
}