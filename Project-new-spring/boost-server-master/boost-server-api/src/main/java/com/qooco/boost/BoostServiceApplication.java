package com.qooco.boost;

import com.qooco.boost.business.BusinessAdminService;
import com.qooco.boost.configurations.BoostRequestFilter;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.CompanyDoc;
import com.qooco.boost.data.mongo.services.AppointmentDetailDocService;
import com.qooco.boost.data.mongo.services.CompanyDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.services.AppointmentDetailService;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.SetupScheduleActorManager;
import com.qooco.boost.utils.MongoConverters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.qooco.boost.data.constants.Constants.DEFAULT_LIMITED_ITEM;

//import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"com.qooco.boost"})
@EnableAsync
public class BoostServiceApplication extends SpringBootServletInitializer {
    protected static Logger boostLogger = LogManager.getLogger(BoostServiceApplication.class);
    @Autowired
    private SetupScheduleActorManager setupScheduleActorManager;
    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(BoostServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BoostServiceApplication.class);
    }

    @PostConstruct
    public void setSchedule() {

//        boostLogger.info("BoostServiceApplication - addDefaultValueForHasPersonality");
//        context.getBean(UserCvDocService.class).addDefaultValueForHasPersonality();
//
//        boostLogger.info("BoostServiceApplication - syncIsDeletedMessageInMessageDoc");
//        context.getBean(MessageDocService.class).syncIsDeletedMessageInMessageDoc();
//        boostLogger.info("BoostServiceApplication - syncIsDeletedMessageInConversationDoc");
//        context.getBean(ConversationDocService.class).syncIsDeletedMessageInConversationDoc();
//        boostLogger.info("BoostServiceApplication - syncIsDeletedMessageInMessageCenterDoc");
//        context.getBean(MessageCenterDocService.class).syncIsDeletedMessageInMessageCenterDoc();
//
//        boostLogger.info("BoostServiceApplication - updateDisconnected");
//        context.getBean(SocketConnectionDocService.class).updateDisconnected();

//        boostLogger.info("BoostServiceApplication - syncAppointmentDetailDoc");
//        syncAppointmentDetailDoc(context);
//        boostLogger.info("BoostServiceApplication - syncStaffInCompany");
//        syncStaffInCompany(context);
        context.getBean(UserCvDocService.class).addDefaultArrayForPreferredHotels();
        context.getBean(BusinessAdminService.class).patchLocaleDataInMongo("all");
        if (Objects.nonNull(setupScheduleActorManager)) {
            boostLogger.info("BoostServiceApplication -  startSchedule");
            setupScheduleActorManager.startSchedule();
        }
    }

    private static void syncAppointmentDetailDoc(ApplicationContext context) {
        List<Long> syncAppointmentId = new ArrayList<>();
        List<AppointmentDetailDoc> appointmentDetailDocs = context.getBean(AppointmentDetailDocService.class)
                .findNoDateTimeRange(DEFAULT_LIMITED_ITEM, syncAppointmentId);
        int size = appointmentDetailDocs.size();
        while (size > 0) {
            List<Long> appointmentDetailIds = appointmentDetailDocs.stream()
                    .map(AppointmentDetailDoc::getId).collect(Collectors.toList());
            syncAppointmentId.addAll(appointmentDetailIds);
            List<AppointmentDetail> appointmentDetails = context.getBean(AppointmentDetailService.class)
                    .findByIds(appointmentDetailIds);
            context.getBean(BoostActorManager.class).updateAppointmentDetailsToMongo(appointmentDetails);
            appointmentDetailDocs = context.getBean(AppointmentDetailDocService.class)
                    .findNoDateTimeRange(DEFAULT_LIMITED_ITEM, syncAppointmentId);
            size = appointmentDetailDocs.size();
        }
    }

    private static void syncStaffInCompany(ApplicationContext context) {
        List<CompanyDoc> companyDocs = context.getBean(CompanyDocService.class)
                .findNoneStaffCompany(DEFAULT_LIMITED_ITEM);
        int size = companyDocs.size();
        while (size > 0) {
            List<Long> companyIds = companyDocs.stream().map(CompanyDoc::getId).collect(Collectors.toList());
            List<Staff> staffs = context.getBean(StaffService.class).findByCompanyIds(companyIds);
            List<Long> noStaffCompanyId = new ArrayList<>();
            companyDocs.forEach(companyDoc -> {
                List<Staff> staffOfCompany = staffs.stream().filter(
                        s -> s.getCompany().getCompanyId().equals(companyDoc.getId())).collect(Collectors.toList());
                if (staffOfCompany.isEmpty()) {
                    noStaffCompanyId.add(companyDoc.getId());
                }
                List<StaffShortEmbedded> staffShortEmbeddeds = new ArrayList<>();
                staffOfCompany.forEach(staff -> {
                    StaffShortEmbedded staffShortEmbedded = MongoConverters.convertToStaffShortEmbedded(staff);
                    staffShortEmbeddeds.add(staffShortEmbedded);
                });
                companyDoc.setStaffs(staffShortEmbeddeds);
            });
            context.getBean(CompanyDocService.class).updateStaffsOfCompanies(companyDocs);
            context.getBean(CompanyDocService.class).deleteByIds(noStaffCompanyId);
            companyDocs = context.getBean(CompanyDocService.class)
                    .findNoneStaffCompany(DEFAULT_LIMITED_ITEM);
            size = companyDocs.size();
        }
        boostLogger.info("Finish syncStaffInCompany");
    }

    @PreDestroy
    public void onStop() {
        if (Objects.nonNull(setupScheduleActorManager)) {
            setupScheduleActorManager.stopSchedule();
        }
    }

    //@Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new BoostRequestFilter());

        registration.addUrlPatterns("/pata/*");
        registration.addUrlPatterns("/auth/*");
        registration.addUrlPatterns("/app-version/*");
        registration.addUrlPatterns("/boost-chat");
        registration.addUrlPatterns("/boost-chat/*");
        return registration;
    }
}
