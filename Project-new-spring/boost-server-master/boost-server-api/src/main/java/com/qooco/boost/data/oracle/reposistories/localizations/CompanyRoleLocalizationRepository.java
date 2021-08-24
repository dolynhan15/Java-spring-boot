package com.qooco.boost.data.oracle.reposistories.localizations;

import com.qooco.boost.data.oracle.entities.localizations.CompanyRoleLocalization;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRoleLocalizationRepository extends Boot2JpaRepository<CompanyRoleLocalization, Long> {
}
