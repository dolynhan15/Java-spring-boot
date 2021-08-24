package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.oracle.reposistories.VacancyRepository;
import com.qooco.boost.data.oracle.services.VacancyService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class VacancyServiceImpl implements VacancyService {
    @Autowired
    private VacancyRepository repository;

    @Override
    public Vacancy findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Vacancy findValidById(Long id) {
        return repository.findValidById(id);
    }

    @Override
    public List<Vacancy> findValidByIds(Collection<Long> ids) {
        return CollectionUtils.isEmpty(ids) ? ImmutableList.of() : repository.findValidByIds(ids);
    }

    @Override
    public List<Vacancy> getByCompanyId(long companyId) {
        return repository.findAllByCompanyCompanyId(companyId);
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        return repository.save(vacancy);
    }

    @Override
    public List<Vacancy> save(List<Vacancy> vacancies) {
        return Lists.newArrayList(repository.saveAll(vacancies));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public int countOpenVacancyByUserAndCompany(Long userProfileId, Long companyId) {
        return repository.countOpenVacancyByUserAndCompany(userProfileId, companyId);
    }

    @Override
    public Vacancy findByVacancyAndUserProfileAndCompany(long id, Long userId, Long companyId) {
        return repository.findByVacancyAndUserProfileAndCompany(id, userId, companyId);
    }

    @Override
    public Vacancy findOpeningByVacancyAndUserProfileAndCompany(long id, Long userId, Long companyId) {
        return repository.findOpeningByVacancyAndUserProfileAndCompany(id, userId, companyId);
    }

    @Override
    public Vacancy findByIdAndCompanyAndUserProfile(Long id, Long companyId, Long userProfileId) {
        return repository.findByIdAndCompanyAndUserProfile(id, companyId, userProfileId);
    }

    @Override
    public Vacancy findByIdAndCompanyAndUser(Long id, Long companyId, Long userProfileId) {
        return repository.findByIdAndCompanyAndUser(id, companyId, userProfileId);
    }

    @Override
    public List<Vacancy> findOpeningVacancyByUserAndCompany(Long userProfileId, Long companyId) {
        List<Long> vacancyIds = findOpeningVacancyIdByUserAndCompany(userProfileId, companyId);
        if (Objects.nonNull(vacancyIds) && !vacancyIds.isEmpty()) {
            return repository.findByVacancyIds(vacancyIds);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Long> findOpeningVacancyIdByUserAndCompany(Long userProfileId, Long companyId) {
        List<BigDecimal> result = repository.findOpeningVacancyIdByCompanyAndUser(userProfileId, companyId);
        List<Long> ids = new ArrayList<>();
        if (Objects.nonNull(result) && !result.isEmpty()) {
            for (BigDecimal id : result) {
                ids.add(id.longValue());
            }
        }
        return ids;
    }

    @Override
    public int countOpeningByUserAndCompany(Long userId, Long companyId) {
        List<Long> vacancyIds = findOpeningVacancyIdByUserAndCompany(userId, companyId);
        return vacancyIds.size();
    }

    @Override
    public List<Vacancy> findAllByContactPersonAndCompany(Long contactPersonId, Long companyId) {
        return repository.findAllByContactPersonAndCompany(contactPersonId, companyId);
    }

    @Override
    public Page<Vacancy> findOpeningVacancyByUserAndCompany(Long userProfileId, long companyId, int page, int size) {
        Sort sort = Sort.by("UPDATED_DATE").descending().and(Sort.by("VACANCY_ID").descending());
        return repository.findByUserIdAndCompanyId(userProfileId, companyId, PageRequest.of(page, size, sort));
    }

    @Override
    public Page<Vacancy> findVacancyHavingActiveAppointmentsByUserAndCompany(Long userProfileId, long companyId, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "UPDATED_DATE"),
                new Sort.Order(Sort.Direction.DESC, "VACANCY_ID"));
        if (page < 0 || size <= 0) {
            return repository.findHavingActiveAppointmentsByUserIdAndCompanyId(userProfileId, companyId, PageRequest.of(0, Integer.MAX_VALUE, sort));
        }
        return repository.findHavingActiveAppointmentsByUserIdAndCompanyId(userProfileId, companyId, PageRequest.of(page, size, sort));
    }

    @Override
    public List<Vacancy> findVacancies(long vacancyId, int size) {

        return repository.findVacancies(vacancyId, PageRequest.of(0, size));
    }

    @Override
    public List<Vacancy> findByAppointments(List<Long> appointmentIds) {
        return CollectionUtils.isEmpty(appointmentIds)
            ? ImmutableList.of()
            : repository.findByAppointments(appointmentIds);
    }

    @Override
    public List<Vacancy> findNoneSeatVacancies(long vacancyId, int size) {

        return repository.findVacancies(vacancyId, PageRequest.of(0, size));
    }


    @Override
    public Page<Vacancy> findSuspendByCompanyAndUserProfile(Long companyId, Long userProfileId, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "UPDATED_DATE"),
                new Sort.Order(Sort.Direction.DESC, "VACANCY_ID"));
        return repository.findSuspendByCompanyAndUserProfile(companyId, userProfileId, PageRequest.of(page, size, sort));
    }

    @Override
    public Page<Vacancy> findClosedByCompanyAndUserProfile(Long companyId, Long userProfileId, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "UPDATED_DATE"),
                new Sort.Order(Sort.Direction.DESC, "VACANCY_ID"));
        return repository.findClosedByCompanyAndUserProfile(companyId, userProfileId, PageRequest.of(page, size, sort));
    }
}