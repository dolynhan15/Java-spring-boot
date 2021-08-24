package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.mongo.entities.CompanyDoc;
import com.qooco.boost.data.mongo.services.CompanyDocService;
import com.qooco.boost.data.mongo.services.embedded.CompanyEmbeddedService;
import com.qooco.boost.data.mongo.services.embedded.StaffEmbeddedService;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.threads.services.UserProfileActorService;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateCompanyInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(UpdateCompanyInMongoActor.class);
    public static final String ACTOR_NAME = "updateCompanyInMongoActor";

    private CompanyDocService companyDocService;
    private StaffService staffService;
    private UserProfileActorService userProfileActorService;
    private CompanyEmbeddedService companyEmbeddedService;
    private StaffEmbeddedService staffEmbeddedService;

    public UpdateCompanyInMongoActor(CompanyDocService companyDocService,
                                     StaffService staffService,
                                     UserProfileActorService userProfileActorService,
                                     CompanyEmbeddedService companyEmbeddedService,
                                     StaffEmbeddedService staffEmbeddedService) {
        this.companyDocService = companyDocService;
        this.staffService = staffService;
        this.userProfileActorService = userProfileActorService;
        this.companyEmbeddedService = companyEmbeddedService;
        this.staffEmbeddedService= staffEmbeddedService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Company) {
            Company company = ((Company) message);
            Long companyId = company.getCompanyId();
            if (companyId != null) {
                List<Staff> staffs = staffService.findStaffOfCompanyByRole(companyId, CompanyRole.ADMIN.getCode());
                List<UserProfileEmbedded> admins = new ArrayList<>();
                for(Staff staff : staffs){
                    UserFit userFit = userProfileActorService.updateLazyValue(staff.getUserFit());
                    UserProfileEmbedded embedded = MongoConverters.convertToUserProfileEmbedded(userFit);
                    admins.add(embedded);
                }

                CompanyDoc companyDoc = MongoConverters.convertToCompanyDoc(company);
                if (Objects.nonNull(companyDoc)){
                    companyDoc.setAdmins(admins);
                    List<StaffShortEmbedded> staffShortEmbeddeds = new ArrayList<>();
                    staffs.forEach(staff -> {
                        StaffShortEmbedded staffShortEmbedded = MongoConverters.convertToStaffShortEmbedded(staff);
                        staffShortEmbeddeds.add(staffShortEmbedded);
                    });
                    companyDoc.setStaffs(staffShortEmbeddeds);
                }
                companyDocService.save(companyDoc);
                companyEmbeddedService.update(MongoConverters.convertToCompanyEmbedded(company));
                logger.info(StringUtil.append("Update Company Id =", company.getCompanyId().toString()));
            }
        } else if (message instanceof Staff) {
            long companyId = ((Staff) message).getCompany().getCompanyId();
            StaffShortEmbedded staffShortEmbedded = MongoConverters.convertToStaffShortEmbedded((Staff) message);
            companyDocService.addStaffToCompany(companyId, staffShortEmbedded);
            logger.info(String.format("Add Staff to company Id = %d", companyId));
        } else if (message instanceof StaffShortEmbedded) {
            staffEmbeddedService.update((StaffShortEmbedded) message);
            logger.info(String.format("Update Staff Id = %d", ((StaffShortEmbedded) message).getId()));
        }
    }

}
