package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class BusinessValidatorServiceImpl implements BusinessValidatorService {
    @Autowired
    private VacancyService vacancyService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private JobService jobService;
    @Autowired
    private CityService cityService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private EducationService educationService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private AppointmentDetailService appointmentDetailService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private AssessmentService assessmentService;

    @Override
    public UserProfile checkExistsUserProfile(Long id) {
        UserProfile userProfile = userProfileService.findById(id);
        if (Objects.isNull(userProfile)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }
        return userProfile;
    }

    @Override
    public UserProfile checkUserProfileIsRootAdmin(Long id) {
        UserProfile userProfile = userProfileService.checkUserProfileIsRootAdmin(id);
        if(Objects.isNull(userProfile)){
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        return userProfile;
    }

    @Override
    public UserFit checkExistsUserFit(Long id) {
        UserFit fitUserDoc = userFitService.findById(id);
        if (Objects.isNull(fitUserDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }
        return fitUserDoc;
    }

    @Override
    public UserCurriculumVitae checkExistsUserCurriculumVitae(Long id) {
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findById(id);
        if (Objects.isNull(userCurriculumVitae)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_CURRICULUM_VITAE);
        }
        return userCurriculumVitae;
    }

    @Override
    public List<UserProfile> checkExistsUserProfile(List<Long> ids) {
        List<UserProfile> userProfiles = userProfileService.findByIds(ids);
        if (CollectionUtils.isEmpty(userProfiles)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }
        return userProfiles;
    }

    @Override
    public List<UserCurriculumVitae> checkExistsUserCurriculumVitae(List<Long> ids) {
        List<UserCurriculumVitae> userCurriculumVitaes = userCurriculumVitaeService.findByIds(ids);
        if (CollectionUtils.isEmpty(userCurriculumVitaes) || ids.size() > userCurriculumVitaes.size()) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_CURRICULUM_VITAE);
        }
        return userCurriculumVitaes;
    }

    @Override
    public UserCvDoc checkExistsUserCvDoc(Long userProfileId) {
        UserCvDoc userCvDoc = userCvDocService.findByUserProfileId(userProfileId);
        if (Objects.isNull(userCvDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_CURRICULUM_VITAE);
        }
        return userCvDoc;
    }

    @Override
    public List<UserCvDoc> checkExistsUserCvDoc(List<Long> ids) {
        List<UserCvDoc> userCvDocs = userCvDocService.findAllById(ids);
        if (CollectionUtils.isEmpty(userCvDocs) || ids.size() > userCvDocs.size()) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_CURRICULUM_VITAE);
        }
        return userCvDocs;
    }

    @Override
    public Currency checkExistsCurrency(Long id) {
        return checkExistsCurrency(id, ResponseStatus.NOT_FOUND_CURRENCY);
    }

    @Override
    public Currency checkExistsCurrency(Long id, ResponseStatus errorCode) {
        Currency currency = currencyService.findById(id);
        if (Objects.isNull(currency)) {
            throw new EntityNotFoundException(errorCode);
        }
        return currency;
    }

    @Override
    public Education checkExistsEducation(Long id) {
        return checkExistsEducation(id, ResponseStatus.NOT_FOUND_EDUCATION);
    }

    @Override
    public Education checkExistsEducation(Long id, ResponseStatus errorCode) {
        Education education = educationService.findById(id);
        if (Objects.isNull(education)) {
            throw new EntityNotFoundException(errorCode);
        }
        return education;
    }

    @Override
    public Staff checkExistsStaff(Long id) {
        return checkExistsStaff(id, ResponseStatus.NOT_FOUND_STAFF);
    }

    @Override
    public Staff checkExistsStaff(Long id, ResponseStatus errorCode) {
        Staff staff = staffService.findById(id);
        if (Objects.isNull(staff)) {
            throw new EntityNotFoundException(errorCode);
        }
        return staff;
    }

    @Override
    public Staff checkExistsStaffInApprovedCompany(Long companyId, Long userProfileId) {
        return checkExistsStaffInApprovedCompany(companyId, userProfileId, ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
    }

    @Override
    public Staff checkExistsStaffInApprovedCompany(Long companyId, Long userProfileId, ResponseStatus errorCode) {
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId);
        if (CollectionUtils.isEmpty(staffs)) {
            throw new EntityNotFoundException(errorCode);
        }
        return staffs.get(0);
    }

    @Override
    public Staff checkExistsStaffInCompany(Long companyId, Long userProfileId) {
        return checkExistsStaffInCompany(companyId, userProfileId, ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
    }

    @Override
    public Staff checkExistsStaffInCompany(Long companyId, Long userProfileId, ResponseStatus errorCode) {
        List<Staff> staffs = staffService.findByUserProfileAndCompany(companyId, userProfileId);
        if (CollectionUtils.isEmpty(staffs)) {
            throw new EntityNotFoundException(errorCode);
        }
        return staffs.get(0);
    }

    @Override
    public City checkExistsCity(Long id) {
        return checkExistsCity(id, ResponseStatus.NOT_FOUND_CITY);
    }

    @Override
    public City checkExistsCity(Long id, ResponseStatus errorCode) {
        City city = cityService.findValidById(id);
        if (Objects.isNull(city)) {
            throw new EntityNotFoundException(errorCode);
        }
        return city;
    }

    @Override
    public Country checkExistsCountry(Long id) {
        if (Objects.nonNull(id)) {
            Country country = countryService.findValidById(id);
            if (Objects.isNull(country)) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_COUNTRY);
            }
            return country;
        }
        return null;
    }

    @Override
    public Job checkExistsJob(Long id) {
        return checkExistsJob(id, ResponseStatus.NOT_FOUND_JOB);
    }

    @Override
    public Job checkExistsJob(Long id, ResponseStatus errorCode) {
        Job job = jobService.findValidById(id);
        if (Objects.isNull(job)) {
            throw new EntityNotFoundException(errorCode);
        }
        return job;
    }

    @Override
    public Vacancy checkExistsValidVacancy(Long id) {
        Vacancy vacancy = vacancyService.findValidById(id);
        if (Objects.isNull(vacancy)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_VACANCY);
        }
        return vacancy;
    }

    @Override
    public Vacancy checkOpeningVacancy(Long id) {
        Vacancy vacancy = checkExistsValidVacancy(id);
        if (Const.Vacancy.Status.INACTIVE == vacancy.getVacancyStatus()) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_INACTIVE);
        } else if (Const.Vacancy.Status.SUSPEND == vacancy.getVacancyStatus()) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_SUSPENDED);
        }
        return vacancy;
    }


    @Override
    public Assessment checkExistsAssessment(Long id) {
        Assessment assessment = assessmentService.findById(id);
        if (Objects.isNull(assessment)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_ASSESSMENT);
        }
        return assessment;
    }

    @Override
    public boolean existsAssessment(Long id) {
        return assessmentService.exists(id);
    }

    @Override
    public Location checkExistsLocation(Long id) {
        return checkExistsLocation(id, ResponseStatus.NOT_FOUND_LOCATION);
    }

    @Override
    public Location checkExistsLocation(Long id, ResponseStatus errorCode) {
        Location location = locationService.findById(id);
        if (Objects.isNull(location)) {
            throw new EntityNotFoundException(errorCode);
        }
        return location;
    }

    @Override
    public List<Location> checkExistsLocations(Long companyId, List<Long> ids) {
        List<Location> locations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ids)) {
            locations = locationService.findByIdsAndCompanyId(companyId, ids);
            if (CollectionUtils.isEmpty(locations) || ids.size() > locations.size()) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LOCATION);
            }
        }
        return locations;
    }

    @Override
    public Appointment checkExistsAppointment(Long id) {
        return checkExistsAppointment(id, ResponseStatus.NOT_FOUND_APPOINTMENT);
    }

    @Override
    public boolean existsAppointment(List<Long> ids) {
        long count = appointmentService.countValid(ids);
        if (count < ids.size()) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT);
        }
        return true;
    }

    @Override
    public Appointment checkExistsAppointment(Long id, ResponseStatus errorCode) {
        Appointment appointment = appointmentService.findValidById(id);
        if (Objects.isNull(appointment)) {
            throw new EntityNotFoundException(errorCode);
        }
        return appointment;
    }

    @Override
    public boolean existsCompany(Long id) {
        return companyService.exists(id);
    }

    @Override
    public Company checkExistsCompany(Long id) {
        return checkExistsCompany(id, ResponseStatus.NOT_FOUND_COMPANY);
    }

    @Override
    public Company checkExistsCompany(Long id, ResponseStatus errorCode) {
        Company company = companyService.findById(id);
        if (Objects.isNull(company)) {
            throw new EntityNotFoundException(errorCode);
        }
        return company;
    }

    @Override
    public Staff checkHasPermissionToCreateVacancy(Long companyId, Long userProfileId) {
        Staff staff = checkExistsStaffInApprovedCompany(companyId, userProfileId);

        List<String> roleNames = CompanyRole.ADMIN.getRolesEqualOrLessNoAnalyst();
        if (!roleNames.contains(staff.getRole().getName())) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        return staff;
    }

    @Override
    public Staff checkPermissionOnVacancy(Vacancy vacancy, Staff staff) {
        if (Objects.nonNull(staff) && Objects.nonNull(vacancy)) {
            Long companyId = vacancy.getCompany().getCompanyId();
            Long contactPersonId = vacancy.getContactPerson().getStaffId();
            Long createdBy = vacancy.getCreatedBy();
            return checkPermissionOnVacancy(companyId, contactPersonId, createdBy, staff);
        }
        throw new NoPermissionException(ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
    }

    @Override
    public Staff checkPermissionOnVacancy(VacancyDoc vacancy, Staff staff) {
        if (Objects.nonNull(staff) && Objects.nonNull(vacancy)) {
            Long companyId = vacancy.getCompany().getId();
            Long contactPersonId = vacancy.getContactPerson().getId();
            Long createdBy = null;
            if (Objects.nonNull(vacancy.getCreatedByStaff())) {
                createdBy = vacancy.getCreatedByStaff().getUserProfile().getUserProfileId();
            }
            return checkPermissionOnVacancy(companyId, contactPersonId, createdBy, staff);
        }
        throw new NoPermissionException(ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
    }

    @Override
    public Staff checkPermissionOnVacancy(Long companyIdOfVacancy, Long contactPersonId, Long createdByOfVacancy, Staff staff) {
        if (Objects.isNull(staff.getRole())) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_VACANCY);
        }
        Long roleId = staff.getRole().getRoleId();
        Long staffId = staff.getStaffId();
        Long userProfileId = staff.getUserFit().getUserProfileId();

        boolean hasPermission = companyIdOfVacancy.equals(staff.getCompany().getCompanyId())
                && (roleId == CompanyRole.ADMIN.getCode()
                || roleId == CompanyRole.HEAD_RECRUITER.getCode()
                || roleId == CompanyRole.RECRUITER.getCode()
                && (staffId.equals(contactPersonId)
                || userProfileId.equals(createdByOfVacancy)));

        if (!hasPermission) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_VACANCY);
        }
        return staff;
    }

    @Override
    public Staff checkPermissionOnVacancy(Vacancy vacancy, Long userProfileId) {
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApproval(userProfileId, vacancy.getCompany().getCompanyId());
        if (CollectionUtils.isNotEmpty(staffs)) {
            Optional<Staff> optionalStaff = staffs.stream()
                    .filter(st -> Objects.nonNull(checkPermissionOnVacancy(vacancy, st)))
                    .findFirst();
            if (optionalStaff.isPresent()) {
                return optionalStaff.get();
            }
        }
        throw new NoPermissionException(ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
    }

    @Override
    public Staff checkPermissionOnVacancy(VacancyDoc vacancy, Long userProfileId) {
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApproval(userProfileId, vacancy.getCompany().getId());
        if (CollectionUtils.isNotEmpty(staffs)) {
            Optional<Staff> optionalStaff = staffs.stream()
                    .filter(st -> Objects.nonNull(checkPermissionOnVacancy(vacancy, st)))
                    .findFirst();
            if (optionalStaff.isPresent()) {
                return optionalStaff.get();
            }
        }
        throw new NoPermissionException(ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
    }

    @Override
    public Staff checkPermissionForSaveAppointmentOnVacancy(Vacancy vacancy, Long userProfileId) {
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApproval(userProfileId, vacancy.getCompany().getCompanyId());
        if (CollectionUtils.isNotEmpty(staffs)) {
            Optional<Staff> optionalStaff = staffs.stream()
                    .filter(st -> checkPermissionForSaveAppointmentOnVacancy(vacancy, st))
                    .findFirst();
            if (optionalStaff.isPresent()) {
                return optionalStaff.get();
            } else {
                int countAppointment = appointmentService.countAppointmentInVacancy(vacancy.getId(), userProfileId);
                if (countAppointment <= 0) {
                    throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
                }
                return staffs.get(0);
            }
        }
        throw new NoPermissionException(ResponseStatus.USER_IS_NOT_JOIN_COMPANY);
    }

    @Override
    public Vacancy checkPermissionOnVacancy(Long vacancyId, Long companyId, Long userProfileId) {
        Vacancy vacancy = vacancyService.findByIdAndCompanyAndUserProfile(vacancyId, companyId, userProfileId);
        if (Objects.isNull(vacancy)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_VACANCY);
        }
        return vacancy;
    }
    @Override
    public String checkValidTimeZone(String timeZone) {
        LocalDateTime dt = LocalDateTime.now();
        if (StringUtils.isBlank(timeZone)) {
            throw new InvalidParamException(ResponseStatus.TIME_ZONE_INVALID);
        }
        try {
            ZoneId zone = ZoneId.of(timeZone);
            ZonedDateTime zdt = dt.atZone(zone);
            ZoneOffset offset = zdt.getOffset();
            String timeOffset = offset.toString();
            if ("Z".equals(timeOffset)) {
                return "00:00";
            }
            return timeOffset;
        } catch (DateTimeException dateTimeException) {
            throw new InvalidParamException(ResponseStatus.TIME_ZONE_INVALID);
        }
    }

    @Override
    public Appointment checkPermissionOnAppointment(Long id, Long userProfileId, List<String> roleNames) {
        Appointment appointment = checkExistsAppointment(id);
        checkStaffPermissionOnAppointment(appointment, userProfileId, roleNames);
        return appointment;
    }

    @Override
    public Staff checkStaffPermissionOnAppointment(Appointment appointment, Long userProfileId, List<String> roleNames) {
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApprovalAndRoles(userProfileId, appointment.getVacancy().getCompany().getCompanyId(), roleNames);
        Staff staff = null;
        if(CollectionUtils.isNotEmpty(staffs)){
            staff = staffs.get(0);
        }
        checkPermissionOnAppointment(appointment, userProfileId, staff);
        return staff;
    }

    private void checkPermissionOnAppointment(Appointment appointment, Long userProfileId, Staff staff) {
        if (Objects.isNull(staff) || !(CompanyRole.ADMIN.getName().equals(staff.getRole().getName())
                || CompanyRole.HEAD_RECRUITER.getName().equals(staff.getRole().getName())
                || userProfileId.equals(appointment.getVacancy().getUpdatedBy())
                || userProfileId.equals(appointment.getUpdatedBy())
                || userProfileId.equals(appointment.getManager().getUserFit().getUserProfileId()))) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_APPOINTMENT);
        }
    }

    @Override
    public AppointmentDetail checkExistAppointmentDetail(Long id) {
        AppointmentDetail detail = appointmentDetailService.findById(id);
        if (Objects.isNull(detail)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT_DETAIL);
        }
        return detail;
    }

    @Override
    public List<AppointmentDetail> checkExistAppointmentDetail(List<Long> ids) {
        List<AppointmentDetail> details = appointmentDetailService.findByIds(ids);
        if (CollectionUtils.isEmpty(details) || ids.size() > details.size()) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_APPOINTMENT_DETAIL);
        }
        return details;
    }

    @Override
    public MessageDoc checkExistsMessageDoc(String id) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(id));
        if (Objects.isNull(messageDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_MESSAGE);
        }
        return messageDoc;
    }

    @Override
    public boolean isValidSalaryRange(Currency currency, double minSalary, double maxSalary) {
        if (Objects.isNull(currency)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_CURRENCY);
        }
        if (minSalary >= currency.getMinSalary() && maxSalary <= currency.getMaxSalary()) {
            return true;
        }
        throw new InvalidParamException(ResponseStatus.INVALID_SALARY_RANGE);
    }

    @Override
    public boolean isCurrentDateRange(Date fromDate, Date toDate) {
        Date now = new Date();
        if (Objects.isNull(fromDate) || Objects.isNull(toDate)) {
            throw new InvalidParamException(ResponseStatus.INVALID_TIME_RANGE);
        } else if (!fromDate.before(toDate)) {
            throw new InvalidParamException(ResponseStatus.INVALID_TIME_RANGE);
        } else if (now.after(toDate)) {
            throw  new InvalidParamException(ResponseStatus.CURRENT_DATE_TIME_IS_OVER);
        }
        return true;
    }

    private boolean checkPermissionForSaveAppointmentOnVacancy(Vacancy vacancy, Staff staff) {
        boolean hasPermission = false;
        if (Objects.nonNull(staff) && Objects.nonNull(vacancy)) {
            Long companyId = vacancy.getCompany().getCompanyId();
            Long contactPersonId = vacancy.getContactPerson().getStaffId();
            Long createdBy = vacancy.getCreatedBy();
            if (Objects.nonNull(staff.getRole())) {
                Long roleId = staff.getRole().getRoleId();
                Long staffId = staff.getStaffId();
                Long userProfileId = staff.getUserFit().getUserProfileId();
                hasPermission = companyId.equals(staff.getCompany().getCompanyId())
                        && (roleId == CompanyRole.ADMIN.getCode()
                        || roleId == CompanyRole.HEAD_RECRUITER.getCode()
                        || roleId == CompanyRole.RECRUITER.getCode()
                        && (staffId.equals(contactPersonId) || userProfileId.equals(createdBy)));
            }

        }
        return hasPermission;
    }
}
