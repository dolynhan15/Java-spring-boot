package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessLocationService;
import com.qooco.boost.constants.PaginationConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.models.dto.LocationFullDTO;
import com.qooco.boost.models.request.LocationReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Location")
@RestController
@RequestMapping(URLConstants.LOCATION_PATH)
public class LocationController extends BaseController {

    @Autowired
    private BusinessLocationService businessLocationService;

    @ApiOperation(value = "Create new location / edit location", httpMethod = "POST", response = LocationResp.class,
            notes = "Create new company location"
                    + "<br>" + "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.ID_IS_EMPTY + " : " + StatusConstants.ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_CITY + " : " + StatusConstants.NOT_FOUND_CITY_MESSAGE
                    + "<br>" + StatusConstants.IN_USING + " : " + StatusConstants.IN_USING_MESSAGE
                    + "<br>" + StatusConstants.LOCATION_IS_EXISTS + " : " + StatusConstants.LOCATION_IS_EXISTS_MESSAGE
    )
    @PostMapping(URLConstants.SAVE_METHOD)
    @PreAuthorize("isAuthenticated()")
    public Object save(@RequestBody final LocationReq request, Authentication authentication) {
        saveRequestBodyToSystemLogger(request);
		BaseResp locationResp = businessLocationService.save(request, authentication);
        return success(locationResp);
    }

    @ApiOperation(value = "Get Location", httpMethod = "GET", response = LocationListResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE
                    + "<br>" + StatusConstants.COMPANY_ID_IS_EMPTY + " : " + StatusConstants.COMPANY_ID_IS_EMPTY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_COMPANY + " : " + StatusConstants.NOT_FOUND_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_LOCATION + " : " + StatusConstants.NOT_FOUND_LOCATION_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = URLConstants.GET_METHOD, method = RequestMethod.GET)
    public Object getLocation(@RequestParam(value = "companyId") Long companyId, Authentication authentication) {
        BaseResp result = businessLocationService.getLocation(companyId, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get Location by company", httpMethod = "GET", response = LocattionPageResult.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping()
    public Object getLocationByCompany(@RequestParam(value = "companyId") long companyId,
                                       @RequestParam(value = "page", required = false, defaultValue = PaginationConstants.DEFAULT_PAGINATION) int page,
                                       @RequestParam(value = "size", required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
                                       Authentication authentication) {
        BaseResp result = businessLocationService.getLocationByCompany(companyId, page, size, authentication);
        return success(result);
    }

    @ApiOperation(value = "Get Location by company", httpMethod = "DELETE", response = BaseResp.class,
            notes = "Response code description:"
                    + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
                    + "<br>" + StatusConstants.USER_IS_NOT_JOIN_COMPANY + " : " + StatusConstants.USER_IS_NOT_JOIN_COMPANY_MESSAGE
                    + "<br>" + StatusConstants.NOT_FOUND_LOCATION + " : " + StatusConstants.NOT_FOUND_LOCATION_MESSAGE
                    + "<br>" + StatusConstants.NO_PERMISSION_TO_DELETE + " : " + StatusConstants.NO_PERMISSION_TO_DELETE_MESSAGE
    )
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping()
    public Object deleteCompany(@RequestParam(value = "id") long id,
                                       Authentication authentication) {
        BaseResp result = businessLocationService.deleteLocation(id, authentication);
        return success(result);
    }

    class LocationResp extends BaseResp<LocationDTO> {
    }

    class LocationListResp extends BaseResp<List<LocationDTO>> {
    }

    class LocattionPageResult extends BaseResp<PagedResult<LocationFullDTO>> {}
}
