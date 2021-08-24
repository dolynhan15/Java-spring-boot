package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.VacancyCandidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface VacancyCandidateRepository extends Boot2JpaRepository<VacancyCandidate, Long> {
    @Query("SELECT vc FROM VacancyCandidate vc " +
            "WHERE vc.vacancy.id IN :vacancyIds " +
            "AND vc.status IN :statuses " +
            "ORDER BY GET_DISPLAY_NAME(vc.candidate.userProfile.firstName, vc.candidate.userProfile.lastName, vc.candidate.userProfile.username) ASC ")
    Page<VacancyCandidate> findByVacancyAndStatus(@Param("vacancyIds") List<Long> vacancyIds, @Param("statuses") List<Integer> statuses, Pageable pageable);

    @Query("SELECT vc FROM VacancyCandidate vc " +
            "WHERE vc.vacancy.id = :vacancyId " +
            "AND vc.status IN :status " +
            "AND vc.candidate.userProfile.userProfileId = :userProfileId " +
            "AND ROWNUM <= 1")
    VacancyCandidate findByVacancyAndUserProfileAndStatus(@Param("vacancyId") Long vacancyId,
                                                          @Param("userProfileId") Long userProfileId,
                                                          @Param("status") List<Integer> status);

    @Query("SELECT vc FROM VacancyCandidate vc WHERE vc.vacancy.id IN :vacancyIds AND vc.status IN :statuses AND vc.createdDate >= :startDate AND vc.createdDate <= :endDate AND vc.isDeleted = FALSE ")
    List<VacancyCandidate> findAllByVacancyIdAndStatusAndCreatedDateBetween(@Param("vacancyIds") List<Long> vacancyIds, @Param("statuses") List<Integer> statuses, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
