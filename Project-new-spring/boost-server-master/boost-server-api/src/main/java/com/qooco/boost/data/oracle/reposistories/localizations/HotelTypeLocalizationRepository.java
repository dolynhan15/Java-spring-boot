package com.qooco.boost.data.oracle.reposistories.localizations;

import com.qooco.boost.data.oracle.entities.localizations.HotelTypeLocalization;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelTypeLocalizationRepository extends Boot2JpaRepository<HotelTypeLocalization, Long> {
}
