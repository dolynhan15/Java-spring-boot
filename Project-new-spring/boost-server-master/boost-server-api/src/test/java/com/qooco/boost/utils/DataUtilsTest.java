package com.qooco.boost.utils;

import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.models.request.CompanyReq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataUtilsTest {

    public static Staff initStaffIsAdmin() {
        Staff staff = new Staff();
        staff.setRole(new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.name()));
        staff.setUserFit(new UserFit(1L));
        staff.setCompany(initCompany());
        staff.setCreatedBy(1L);
        staff.setUpdatedBy(1L);
        return staff;
    }

    public static Staff initStaffIsHeadRecruiter() {
        Staff staff = new Staff();
        staff.setRole(new RoleCompany(CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.HEAD_RECRUITER.name()));
        staff.setUserFit(new UserFit(2L));
        staff.setCompany(initCompany());
        staff.setCreatedBy(2L);
        staff.setUpdatedBy(2L);
        return staff;
    }

    public static Staff initStaffIsRecruiter() {
        Staff staff = new Staff();
        staff.setRole(new RoleCompany(CompanyRole.RECRUITER.getCode(), CompanyRole.RECRUITER.name()));
        staff.setUserFit(new UserFit(3L));
        staff.setCompany(initCompany());
        staff.setCreatedBy(3L);
        staff.setUpdatedBy(3L);
        return staff;
    }

    public static List<Staff> initStaffs() {
        List<Staff> staffs = new ArrayList<>();
        Staff staff = new Staff();
        staff.setRole(new RoleCompany(CompanyRole.RECRUITER.getCode(), CompanyRole.RECRUITER.name()));
        staff.setUserFit(new UserFit(3L));
        staff.setCompany(initCompany());
        staff.setCreatedBy(3L);
        staff.setUpdatedBy(3L);
        staffs.add(staff);
        return staffs;
    }


    public static Company initCompany() {
        Company company = new Company();
        company.setAddress("123 Cong Hoa, Tan Binh, HCM");
        company.setAmadeus("ABC 1234");
        company.setCity(initCity());
        company.setDescription("Food center");
        company.setEmail("pax@axonactive.com");
        company.setGalileo("ABC 2222");
        company.setHotelType(initHotelType());
        company.setLogo("/company12345.png");
        company.setCompanyName("Food");
        company.setPhone("+840101012");
        company.setSabre("NV 123123");
        company.setWeb("http://basd.com");
        company.setWorldspan("NVN 123123");
        return company;
    }

    public static HotelType initHotelType() {
        HotelType hotelType = new HotelType();
        hotelType.setHotelTypeId(1L);
        hotelType.setHotelTypeName("Resort");
        return hotelType;
    }

    public static City initCity() {
        City city = new City();
        city.setCityId(1L);
        city.setCityName("da nang");
        city.setLatitude(0d);
        city.setLongitude(0d);
        Province province = new Province();
        Country country = new Country();
        country.setCountryId(1L);
        country.setCountryName("Vietnam");
        country.setCountryCode("VN");
        province.setCountry(country);
        province.setCode("DN");
        province.setType(1);
        province.setName("Da nang");
        city.setProvince(province);
        return city;
    }

    public static CompanyReq initValidSaveCompanyRequest() {
        CompanyReq request = new CompanyReq();
        request.setAddress("123 Cong Hoa, Tan Binh, HCM");
        request.setAmadeus("ABC 1234");
        request.setCityId(1L);
        request.setDescription("Food center");
        request.setEmail("pax@axonactive.com");
        request.setGalileo("ABC 2222");
        request.setHotelTypeId(1L);
        request.setLogo("http://company12345.png");
        request.setName("Food");
        request.setPhone("+840101012");
        request.setSabre("NV 123123");
        request.setWeb("http://basd.com");
        request.setWorldspan("NVN 123123");
        return request;
    }

    public static Appointment initAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentDate(DateUtils.addDays(new Date(), 2));
        appointment.setManager(DataUtilsTest.initStaffIsAdmin());
        appointment.setLocation(DataUtilsTest.initLocation());
        appointment.setVacancy(DataUtilsTest.initVacancy());
        appointment.setCreatedBy(1L);
        return appointment;
    }

    private static Vacancy initVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setLogo("/image/vacancy/test/123.png");
        vacancy.setCompany(DataUtilsTest.initCompany());
        vacancy.getCompany().setCompanyId(1L);
        vacancy.setJob(DataUtilsTest.initJob());
        vacancy.setJobLocation(DataUtilsTest.initLocation());
        vacancy.setSearchLocation(DataUtilsTest.initLocation());
        vacancy.setContactPerson(DataUtilsTest.initStaffIsHeadRecruiter());
        vacancy.setHourSalary(true);
        vacancy.setWorkingType(true);
        vacancy.setStatus(1);
        vacancy.setSearchRange(1);
        return vacancy;
    }

    private static Job initJob() {
        Job job = new Job();
        job.setJobId(1L);
        job.setJobName("Bellman");
        job.setJobDescription("Bellman");
        job.setCompany(DataUtilsTest.initCompany());
        return job;
    }

    private static Location initLocation() {
        Location location = new Location();
        location.setLocationId(1L);
        location.setAddress("11 Van Cu, Cam Le, Da Nang");
        location.setCity(DataUtilsTest.initCity());
        location.setCompany(DataUtilsTest.initCompany());
        return location;

    }
}
