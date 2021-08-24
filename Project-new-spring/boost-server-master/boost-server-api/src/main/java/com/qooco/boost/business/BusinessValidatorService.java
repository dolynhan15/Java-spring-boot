package com.qooco.boost.business;

import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.enumeration.ResponseStatus;

import java.util.Date;
import java.util.List;

public interface BusinessValidatorService {

    UserProfile checkExistsUserProfile(Long id);

    UserProfile checkUserProfileIsRootAdmin(Long id);

    UserFit checkExistsUserFit(Long id);

    List<UserProfile> checkExistsUserProfile(List<Long> ids);

    UserCurriculumVitae checkExistsUserCurriculumVitae(Long id);

    List<UserCurriculumVitae> checkExistsUserCurriculumVitae(List<Long> ids);

    UserCvDoc checkExistsUserCvDoc(Long userProfileId);

    List<UserCvDoc> checkExistsUserCvDoc(List<Long> ids);

    Currency checkExistsCurrency(Long id);

    Currency checkExistsCurrency(Long id, ResponseStatus errorCode);

    Education checkExistsEducation(Long id);

    Education checkExistsEducation(Long id, ResponseStatus errorCode);

    Staff checkHasPermissionToCreateVacancy(Long companyId, Long userProfileId);

    Staff checkExistsStaff(Long id);

    Staff checkExistsStaffInApprovedCompany(Long companyId, Long userProfileId);

    Staff checkExistsStaffInApprovedCompany(Long companyId, Long userProfileId, ResponseStatus errorCode);

    Staff checkExistsStaffInCompany(Long companyId, Long userProfileId);

    Staff checkExistsStaffInCompany(Long companyId, Long userProfileId, ResponseStatus errorCode);

    Staff checkExistsStaff(Long id, ResponseStatus errorCode);

    City checkExistsCity(Long id);

    City checkExistsCity(Long id, ResponseStatus errorCode);

    Country checkExistsCountry(Long id);

    Job checkExistsJob(Long id);

    Job checkExistsJob(Long id, ResponseStatus errorCode);

    Vacancy checkExistsValidVacancy(Long id);

    Vacancy checkOpeningVacancy(Long id);

    Assessment checkExistsAssessment(Long id);

    boolean existsAssessment(Long id);

    Location checkExistsLocation(Long id);

    List<Location> checkExistsLocations(Long companyId, List<Long> ids);

    Location checkExistsLocation(Long id, ResponseStatus errorCode);

    Appointment checkExistsAppointment(Long id);

    boolean existsAppointment(List<Long> ids);

    Appointment checkExistsAppointment(Long id, ResponseStatus errorCode);

    boolean existsCompany(Long id);

    Staff checkPermissionOnVacancy(Vacancy vacancy, Staff staff);

    Staff checkPermissionOnVacancy(Long companyIdOfVacancy, Long contactPersonId, Long createdByOfVacancy, Staff staff);

    Staff checkPermissionOnVacancy(Vacancy vacancy, Long userProfileId);

    Staff checkPermissionOnVacancy(VacancyDoc vacancy, Staff staff);

    Staff checkPermissionOnVacancy(VacancyDoc vacancy, Long userProfileId);

    Staff checkPermissionForSaveAppointmentOnVacancy(Vacancy vacancy, Long userProfileId);

    Company checkExistsCompany(Long id);

    Company checkExistsCompany(Long id, ResponseStatus errorCode);

    Vacancy checkPermissionOnVacancy(Long vacancyId, Long companyId, Long userProfileId);

    String checkValidTimeZone(String timeZone);

    Appointment checkPermissionOnAppointment(Long id, Long userProfileId, List<String> roleNames);

    Staff checkStaffPermissionOnAppointment(Appointment appointment, Long userProfileId, List<String> roleNames);

    List<AppointmentDetail> checkExistAppointmentDetail(List<Long> ids);

    AppointmentDetail checkExistAppointmentDetail(Long id);

    MessageDoc checkExistsMessageDoc(String id);

    boolean isValidSalaryRange(Currency currency, double minSalary, double maxSalary);

    boolean isCurrentDateRange(Date fromDate, Date toDate);
}
