package com.qooco.boost.data.oracle.reposistories.localizations;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentQuestionLocalization;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalAssessmentQuestionLocalizationRepository extends Boot2JpaRepository<PersonalAssessmentQuestionLocalization, Long> {
}
