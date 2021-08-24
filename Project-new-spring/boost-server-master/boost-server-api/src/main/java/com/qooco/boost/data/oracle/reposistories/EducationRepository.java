package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Education;
import org.springframework.stereotype.Repository;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/4/2018 - 8:33 AM
*/
@Repository
public interface EducationRepository extends Boot2JpaRepository<Education, Long> {
}
