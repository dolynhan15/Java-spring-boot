package com.qooco.boost.data.oracle.services.impl.localizations;

import com.qooco.boost.data.oracle.entities.localizations.HotelTypeLocalization;
import com.qooco.boost.data.oracle.reposistories.localizations.HotelTypeLocalizationRepository;
import com.qooco.boost.data.oracle.services.localizations.HotelTypeLocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelTypeLocalizationServiceImpl implements HotelTypeLocalizationService {

    @Autowired
    private HotelTypeLocalizationRepository repository;
    @Override
    public Iterable<HotelTypeLocalization> saveAll(Iterable<HotelTypeLocalization> hotelTypeLocalizations) {
        return repository.saveAll(hotelTypeLocalizations);
    }

    @Override
    public List<HotelTypeLocalization> findAll() {
        return repository.findAll();
    }
}
