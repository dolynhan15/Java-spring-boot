//package com.qooco.boost.business.impl;
//
//import com.google.common.collect.Lists;
//import com.qooco.boost.business.BusinessAppointmentService;
//import com.qooco.boost.business.BusinessValidatorService;
//import com.qooco.boost.core.model.authentication.AuthenticatedUser;
//import com.qooco.boost.data.enumeration.AppointmentStatus;
//import com.qooco.boost.data.enumeration.CompanyRole;
//import com.qooco.boost.data.oracle.entities.Appointment;
//import com.qooco.boost.data.oracle.entities.AppointmentDetail;
//import com.qooco.boost.data.oracle.entities.Staff;
//import com.qooco.boost.data.oracle.services.AppointmentDetailService;
//import com.qooco.boost.data.oracle.services.AppointmentService;
//import com.qooco.boost.enumeration.ResponseStatus;
//import com.qooco.boost.exception.EntityNotFoundException;
//import com.qooco.boost.utils.DataUtilsTest;
//import com.qooco.boost.utils.ServletUriUtils;
//import org.junit.Assert;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//import org.springframework.security.core.Authentication;
//
//import java.util.List;
//
//@PrepareForTest({ServletUriUtils.class})
//@RunWith(PowerMockRunner.class)
//public class BusinessAppointmentServiceImplTest extends BaseUserService {
//    @InjectMocks
//    private BusinessAppointmentService businessAppointmentService = new BusinessAppointmentServiceImpl();
//
//    @Mock
//    private AppointmentService appointmentService = Mockito.mock(AppointmentService.class);
//
//    @Mock
//    private AppointmentDetailService appointmentDetailService = Mockito.mock(AppointmentDetailService.class);
//
//    @Mock
//    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);
//
//    @Mock
//    private Authentication authentication = Mockito.mock(Authentication.class);
//
//    @Rule
//    public ExpectedException thrown = ExpectedException.none();
//
////    AuthenticatedUser authenticatedUser = new AuthenticatedUser((long) 1, "trungmhv", "1234", Const.ApplicationId.CAREER_PROFILE_APP, null);
//
//    @Test
//    public void getAppointment_whenIdIsValid_thenReturnNotFoundAppointmentError() {
//        Authentication authentication = initAuthentication();
//        Long userId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
//        Appointment appointment = DataUtilsTest.initAppointment();
//        Staff staff = DataUtilsTest.initStaffIsAdmin();
//        Mockito.when(businessValidatorService.checkExistsAppointment(userId)).thenReturn(appointment);
//        Mockito.when(businessValidatorService.checkStaffPermissionOnAppointment(appointment, userId, CompanyRole.ADMIN.getRolesEqualOrLessNoAnalyst())).thenReturn(staff);
//        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAppointmentService.getAppointment(1L, authentication).getCode());
//    }
//
//    @Test
//    public void getAppointment_whenIdIsInvalid_thenReturnNullValue() {
//        thrown.expect(EntityNotFoundException.class);
//        Authentication authentication = initAuthentication();
//        Long userId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
//        Long appointmentId = 1L;
//        Mockito.when(appointmentService.findValidById(appointmentId)).thenReturn(null);
//        Mockito.when(businessValidatorService.checkExistsAppointment(userId)).thenReturn(null);
//        Mockito.when(businessValidatorService.checkStaffPermissionOnAppointment(new Appointment(), userId, CompanyRole.ADMIN.getRolesEqualOrLessNoAnalyst())).thenReturn(null);
//        businessAppointmentService.getAppointment(appointmentId, authentication);
//    }
//
//    private void mockitoGetAppointment(Long appointmentId, Long userProfileId) {
//        Appointment appointment = DataUtilsTest.initAppointment();
//        Staff staff = DataUtilsTest.initStaffIsHeadRecruiter();
//        List<String> roles = CompanyRole.ADMIN.getRolesEqualOrLessNoAnalyst();
//        List<AppointmentDetail> appointmentDetails = Lists.newArrayList(new AppointmentDetail(1L));
//        List<Object[]> idGroup = null;
//        Mockito.when(businessValidatorService.checkExistsAppointment(appointmentId)).thenReturn(appointment);
//        Mockito.when(businessValidatorService.checkStaffPermissionOnAppointment(appointment, userProfileId, roles)).thenReturn(staff);
//        Mockito.when(appointmentDetailService.findByAppointmentIds(Lists.newArrayList(appointment.getId()))).thenReturn(appointmentDetails);
//        Mockito.when(appointmentDetailService.findIdByUpdaterWithStatusAndRoles(1L, Lists.newArrayList(appointment.getId()), AppointmentStatus.getAvailableStatus(), CompanyRole.valueOf(staff.getRole().getName()).getRolesLarger())).thenReturn(idGroup);
//    }
//}