package com.qooco.boost.configurations;

import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.utils.ServletUriUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.qooco.boost.enumeration.ResponseStatus.NO_PERMISSION_TO_ACCESS;

@Component("boostSecurity")
public class BoostSecurityExpression {


    @Value(ApplicationConstant.BOOST_HELPER_RASA_TOKEN)
    private String RASAToken;

    private final UserPreviousPositionService userPreviousPositionService;
    private final PataFileService pataFileService;
    private final StaffService staffService;
    private final CompanyService companyService;

    public BoostSecurityExpression(UserPreviousPositionService userPreviousPositionService,
                                   PataFileService pataFileService,
                                   StaffService staffService,
                                   CompanyService companyService) {
        this.userPreviousPositionService = userPreviousPositionService;
        this.pataFileService = pataFileService;
        this.staffService = staffService;
        this.companyService = companyService;
    }

    public boolean isPreviousPositionOwner(Authentication authentication, Long id) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long ownerId = userPreviousPositionService.findOwnerById(id);
        if (Objects.isNull(ownerId)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND);
        }
        return Objects.equals(ownerId, authenticatedUser.getId());
    }

    public boolean isSystemAdmin(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (!user.isSystemAdmin()) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        return true;
    }

    public boolean hasCompanyRoles(Authentication authentication, long companyId, String... roles) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        if (Objects.isNull(roles)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        List<String> listRole = Arrays.asList(roles);
        Boolean checkedRoles = staffService.checkStaffRoles(companyId, authenticatedUser.getId(), listRole);
        if (!checkedRoles) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        return true;
    }

    public boolean hasCompanyRoles(Authentication authentication, String... roles) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        boolean result;
        if (Objects.isNull(roles) || CollectionUtils.isEmpty(user.getAuthorities())) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        List<String> acceptedRoles = Arrays.asList(roles);
        List<String> userRoles = new ArrayList<>();
        user.getAuthorities().forEach(ur -> userRoles.add(ur.toString()));
        result = acceptedRoles.stream().anyMatch(userRoles::contains);
        return result;
    }

    public boolean isStaff(Authentication authentication, Long companyId) {
        if (Objects.isNull(companyId)) {
            throw new InvalidParamException(ResponseStatus.COMPANY_ID_IS_EMPTY);
        }
        Company foundCompany = companyService.findById(companyId);
        if (Objects.isNull(foundCompany)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_COMPANY);
        }
        if (foundCompany.getStatus().equals(ApprovalStatus.PENDING)) {
            throw new InvalidParamException(ResponseStatus.PENDING_COMPANY);
        }
        if (foundCompany.getStatus().equals(ApprovalStatus.DISAPPROVED)) {
            throw new InvalidParamException(ResponseStatus.DISAPPROVED_COMPANY);
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        List<Staff> foundStaffs = staffService.findByUserProfileAndCompanyApproval(authenticatedUser.getId(), companyId);
        if (CollectionUtils.isNotEmpty(foundStaffs)) {
            throw new InvalidParamException(ResponseStatus.JOINED_COMPANY_REQUEST);
        }
        return true;
    }

    public boolean isPataFileOwner(Authentication authentication, String absolutePath) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        String relatePath = ServletUriUtils.getRelativePath(absolutePath);
        Long ownerId = pataFileService.findOwnerByUrl(relatePath);
        return Objects.equals(ownerId, authenticatedUser.getId());
    }

    public boolean isRasaCallback(String token) {
        if (!RASAToken.equals(token)) throw new InvalidParamException(NO_PERMISSION_TO_ACCESS);
        return true;
    }
}
