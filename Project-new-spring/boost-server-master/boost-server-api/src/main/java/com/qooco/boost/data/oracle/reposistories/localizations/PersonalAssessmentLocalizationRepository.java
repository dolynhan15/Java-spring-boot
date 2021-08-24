package com.qooco.boost.data.oracle.reposistories.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentLocalization;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalAssessmentLocalizationRepository extends Boot2JpaRepository<PersonalAssessmentLocalization, Long> {
}
