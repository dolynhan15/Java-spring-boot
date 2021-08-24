package com.qooco.boost.data.oracle.reposistories.localizations;

import com.qooco.boost.data.oracle.entities.localizations.SoftSkillLocalization;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftSkillLocalizationRepository extends Boot2JpaRepository<SoftSkillLocalization, Long> {
}
