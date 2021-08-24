package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static com.qooco.boost.data.oracle.reposistories.RepositoryUtils.ORDERBY_PAGEABLE_HOLDER;

@Transactional
@Repository
public interface VacancyRepository extends Boot2JpaRepository<Vacancy, Long> {

    String VACANCIES_USER_ACCESSIBLE = "SELECT VACANCY_ID FROM VACANCY v " +
            "JOIN STAFF s ON (s.STAFF_ID = v.CONTACT_PERSON_ID AND s.COMPANY_ID = v.COMPANY_ID) " +
            "WHERE s.COMPANY_ID = :companyId AND s.USER_PROFILE_ID = :userProfileId AND s.ROLE_ID IN (1,2,3) " +
            "UNION " +
            "SELECT VACANCY_ID FROM VACANCY v " +
            "JOIN STAFF s ON s.COMPANY_ID = v.COMPANY_ID " +
            "WHERE v.COMPANY_ID = :companyId AND s.USER_PROFILE_ID = :userProfileId AND s.ROLE_ID IN (1,2) " +
            "UNION " +
            "SELECT VACANCY_ID FROM VACANCY v " +
            "JOIN STAFF s ON (s.COMPANY_ID = v.COMPANY_ID AND s.USER_PROFILE_ID = v.CREATED_BY) " +
            "WHERE v.COMPANY_ID = :companyId AND v.CREATED_BY = :userProfileId AND s.ROLE_ID IN (1,2,3) ";

    String OPEN_VACANCY_USER_OF_COMPANY = "SELECT VACANCY_ID FROM VACANCY v " +
            "WHERE v.VACANCY_ID IN ( " + VACANCIES_USER_ACCESSIBLE + " ) AND v.STATUS = 1 AND IS_SUSPEND_RANGE(v.STATUS, v.START_SUSPEND_DATE, v.SUSPEND_DAYS, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 ";

    String OPEN_VACANCY_USER_HAVING_ACTIVE_APPOINTMENTS_OF_COMPANY = "UNION " +
            "SELECT v.VACANCY_ID FROM VACANCY v " +
            "JOIN APPOINTMENT ap ON ap.VACANCY_ID = v.VACANCY_ID " +
            "WHERE ap.STAFF_ID IN (SELECT STAFF_ID FROM STAFF s WHERE s.COMPANY_ID = :companyId AND s.CREATED_BY = :userProfileId) " +
            "AND ap.APPOINTMENT_DATE >= SYS_EXTRACT_UTC(SYSTIMESTAMP) " +
            "AND ap.IS_DELETED = 0 ";

    String SUSPENDED_VACANCIES = "SELECT * FROM VACANCY v WHERE v.VACANCY_ID IN ( " + VACANCIES_USER_ACCESSIBLE + " ) " +
            "AND IS_SUSPEND_RANGE(v.STATUS, v.START_SUSPEND_DATE, v.SUSPEND_DAYS, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 2";

    String CLOSED_VACANCIES = "SELECT * FROM VACANCY v WHERE v.VACANCY_ID IN  ( " +
            "SELECT DISTINCT vc.VACANCY_ID FROM VACANCY_CANDIDATE vc WHERE vc.CANDIDATE_STATUS = 1 AND vc.VACANCY_ID IN ( " + VACANCIES_USER_ACCESSIBLE + " ))";

    String OPEN_VACANCY_USER_IS_MANAGER = "SELECT v.VACANCY_ID FROM VACANCY v " +
            "JOIN APPOINTMENT ap ON ap.VACANCY_ID = v.VACANCY_ID " +
            "JOIN STAFF s ON s.STAFF_ID = ap.STAFF_ID " +
            "WHERE s.USER_PROFILE_ID = :userProfileId AND s.COMPANY_ID = :companyId AND v.STATUS = 1 AND s.ROLE_ID IN (1,2,3) AND IS_SUSPEND_RANGE(v.STATUS, v.START_SUSPEND_DATE, v.SUSPEND_DAYS, SYS_EXTRACT_UTC(SYSTIMESTAMP)) = 1 ";

    String VACANCY_USER_IS_MANAGER = "SELECT v.VACANCY_ID FROM VACANCY v " +
            "JOIN APPOINTMENT ap ON ap.VACANCY_ID = v.VACANCY_ID " +
            "JOIN STAFF s ON s.STAFF_ID = ap.STAFF_ID " +
            "WHERE s.USER_PROFILE_ID = :userProfileId AND s.COMPANY_ID = :companyId AND s.ROLE_ID IN (1,2,3) ";

    List<Vacancy> findAllByCompanyCompanyId(long companyId);

    @Query("select v from Vacancy v where v.id in :vacancyIds order by v.updatedDate DESC ")
    List<Vacancy> findByVacancyIds(@Param("vacancyIds") List<Long> vacancyIds);

    @Query(value = "SELECT COUNT(DISTINCT VACANCY_ID) FROM (" + OPEN_VACANCY_USER_OF_COMPANY + ")", nativeQuery = true)
    int countOpenVacancyByUserAndCompany(@Param("userProfileId") Long userProfileId, @Param("companyId") Long companyId);


    @Query(value = "SELECT * FROM VACANCY v WHERE v.VACANCY_ID = :id AND v.VACANCY_ID IN (" + VACANCIES_USER_ACCESSIBLE + ") ", nativeQuery = true)
    Vacancy findByIdAndCompanyAndUserProfile(@Param("id") Long id, @Param("companyId") Long companyId, @Param("userProfileId") Long userProfileId);

    @Query(value = "SELECT DISTINCT VACANCY_ID FROM (" + OPEN_VACANCY_USER_OF_COMPANY + ")", nativeQuery = true)
    List<BigDecimal> findOpeningVacancyIdByCompanyAndUser(@Param("userProfileId") Long userProfileId, @Param("companyId") Long companyId);


    @Query(value = "SELECT * FROM VACANCY v WHERE v.VACANCY_ID IN (" + OPEN_VACANCY_USER_OF_COMPANY + ")", nativeQuery = true)
    Page<Vacancy> findByUserIdAndCompanyId(@Param("userProfileId") Long userProfileId, @Param("companyId") long companyId, Pageable pageable);

    @Query(value = "SELECT * FROM VACANCY v WHERE v.VACANCY_ID = :id AND v.VACANCY_ID IN (" + VACANCIES_USER_ACCESSIBLE + "UNION " + VACANCY_USER_IS_MANAGER + ") ", nativeQuery = true)
    Vacancy findByVacancyAndUserProfileAndCompany(@Param("id") long id, @Param("userProfileId") Long userProfileId, @Param("companyId") long companyId);

    @Query(value = "SELECT * FROM VACANCY v WHERE v.VACANCY_ID = :id AND v.VACANCY_ID IN (" + OPEN_VACANCY_USER_OF_COMPANY + "UNION " + OPEN_VACANCY_USER_IS_MANAGER + ") ", nativeQuery = true)
    Vacancy findOpeningByVacancyAndUserProfileAndCompany(@Param("id") long id, @Param("userProfileId") Long userProfileId, @Param("companyId") long companyId);

    @Query(value = "SELECT * FROM Vacancy v WHERE v.VACANCY_ID = :id AND v.VACANCY_ID IN (" + VACANCIES_USER_ACCESSIBLE + ") ", nativeQuery = true)
    Vacancy findByIdAndCompanyAndUser(@Param("id") Long id,
                                      @Param("companyId") Long companyId,
                                      @Param("userProfileId") Long userProfileId);

    @Query(value = "SELECT * FROM VACANCY v WHERE v.VACANCY_ID IN ("
            + OPEN_VACANCY_USER_OF_COMPANY
            + OPEN_VACANCY_USER_HAVING_ACTIVE_APPOINTMENTS_OF_COMPANY + ") " + ORDERBY_PAGEABLE_HOLDER, nativeQuery = true)
    Page<Vacancy> findHavingActiveAppointmentsByUserIdAndCompanyId(@Param("userProfileId") Long userProfileId, @Param("companyId") long companyId, Pageable pageable);

    @Query("SELECT v FROM Vacancy v WHERE v.contactPerson.staffId = :contactPersonId AND v.company.companyId = :companyId AND v.isDeleted = false")
    List<Vacancy> findAllByContactPersonAndCompany(@Param("contactPersonId") Long contactPersonId, @Param("companyId") Long companyId);

    @Query(value = SUSPENDED_VACANCIES + ORDERBY_PAGEABLE_HOLDER, nativeQuery = true)
    Page<Vacancy> findSuspendByCompanyAndUserProfile(@Param("companyId") Long companyId, @Param("userProfileId") Long userProfileId, Pageable pageable);

    @Query(value = CLOSED_VACANCIES + ORDERBY_PAGEABLE_HOLDER, nativeQuery = true)
    Page<Vacancy> findClosedByCompanyAndUserProfile(@Param("companyId") Long companyId, @Param("userProfileId") Long userProfileId, Pageable pageable);

    @Query("SELECT v FROM Vacancy v WHERE v.id = :id AND v.isDeleted = false ")
    Vacancy findValidById(@Param("id") Long id);

    @Query("SELECT v FROM Vacancy v WHERE v.id IN :ids AND v.isDeleted = false ")
    List<Vacancy> findValidByIds(@Param("ids") Collection<Long> ids);

    @Query("SELECT v FROM Vacancy v WHERE v.id > :id AND v.isDeleted = false ORDER BY v.id ASC ")
    List<Vacancy> findVacancies(@Param("id")long id, Pageable pageable);

    @Query("SELECT v FROM Vacancy v JOIN v.vacancyAppointments aps WHERE aps.id IN :appointmentIds AND v.isDeleted = false ")
    List<Vacancy> findByAppointments(@Param("appointmentIds") List<Long> appointmentIds);

    @Query("SELECT v FROM Vacancy v WHERE v.id > :id AND (v.vacancySeats IS NULL OR  size(v.vacancySeats) = 0) AND v.isDeleted = false ")
    List<Vacancy> findNoneSeatVacancies(@Param("id")long id, Pageable pageable);

}
