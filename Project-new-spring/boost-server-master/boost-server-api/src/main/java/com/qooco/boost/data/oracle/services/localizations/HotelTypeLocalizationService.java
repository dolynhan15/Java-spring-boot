package com.qooco.boost.data.oracle.services.localizations;

import com.qooco.boost.data.oracle.entities.localizations.HotelTypeLocalization;

import java.util.List;

public interface HotelTypeLocalizationService {
    Iterable<HotelTypeLocalization> saveAll(Iterable<HotelTypeLocalization> hotelTypeLocalizations);

    List<HotelTypeLocalization> findAll();
}
