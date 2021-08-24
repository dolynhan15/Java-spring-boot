package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Vacancy;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

public interface VacancyService {

    Vacancy findById(Long id);

    Vacancy findValidById(Long id);

    List<Vacancy> findValidByIds(Collection<Long> ids);

    List<Vacancy> getByCompanyId(long companyId);

    Vacancy save(Vacancy vacancy);

    List<Vacancy> save(List<Vacancy> vacancies);

    void delete(Long id);

    Boolean exists(Long id);

    int countOpenVacancyByUserAndCompany(Long userProfileId, Long companyId);

    int countOpeningByUserAndCompany(Long userId, Long companyId);

    Vacancy findByVacancyAndUserProfileAndCompany(long id, Long userId, Long companyId);

    Vacancy findOpeningByVacancyAndUserProfileAndCompany(long id, Long userId, Long companyId);

    Vacancy findByIdAndCompanyAndUserProfile(Long id, Long companyId, Long userProfileId);

    Vacancy findByIdAndCompanyAndUser(Long id, Long companyId, Long userProfileId);

    List<Long> findOpeningVacancyIdByUserAndCompany(Long userProfileId, Long companyId);

    List<Vacancy> findOpeningVacancyByUserAndCompany(Long userProfileId, Long companyId);

    List<Vacancy> findAllByContactPersonAndCompany(Long contactPersonId, Long companyId);

    Page<Vacancy> findSuspendByCompanyAndUserProfile(Long companyId, Long userProfileId, int page, int size);

    Page<Vacancy> findClosedByCompanyAndUserProfile(Long companyId, Long userProfileId, int page, int size);

    Page<Vacancy> findOpeningVacancyByUserAndCompany(Long userProfileId, long companyId, int page, int size);

    Page<Vacancy> findVacancyHavingActiveAppointmentsByUserAndCompany(Long userProfileId, long companyId, int page, int size);

    List<Vacancy> findVacancies(long vacancyId, int size);

    List<Vacancy> findByAppointments(List<Long> appointmentIds);

    List<Vacancy> findNoneSeatVacancies(long vacancyId, int size);
}
