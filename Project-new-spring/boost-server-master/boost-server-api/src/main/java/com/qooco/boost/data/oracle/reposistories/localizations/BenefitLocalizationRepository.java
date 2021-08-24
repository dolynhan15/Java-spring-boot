package com.qooco.boost.data.oracle.reposistories.localizations;

import com.qooco.boost.data.oracle.entities.localizations.BenefitLocalization;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitLocalizationRepository extends Boot2JpaRepository<BenefitLocalization, Long> {
}
