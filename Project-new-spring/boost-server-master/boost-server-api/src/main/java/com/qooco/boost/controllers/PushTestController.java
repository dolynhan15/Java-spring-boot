package com.qooco.boost.controllers;

import akka.actor.ActorSystem;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.mongo.services.ViewProfileDocService;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Api(tags = "PushNotification", description = "Push Notification Test Controller")
@RestController
@RequestMapping("/push")
public class PushTestController extends BaseController {

    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private ViewProfileDocService viewProfileDocService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    private ActorSystem system;

    @ApiOperation(value = "Push Chat Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/chatMessage/{messageId}", method = RequestMethod.GET)
    public Object pushChatMessage(@PathVariable String messageId) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        pushNotificationService.notifyNewMessage(messageDoc, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push Applicant Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/applicantMessage/{messageId}", method = RequestMethod.GET)
    public Object pushApplicantMessage(@PathVariable String messageId) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        pushNotificationService.notifyApplicantMessage(messageDoc, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push Interested/Not Interested Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/responseApplicantMessage/{messageId}", method = RequestMethod.GET)
    public Object pushResponseApplicantMessage(@PathVariable String messageId) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        pushNotificationService.notifyApplicantResponseMessage(messageDoc, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push Appointment Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/appointmentMessage/{appointmentDetailId}", method = RequestMethod.GET)
    public Object pushAppointmentMessage(@PathVariable Long appointmentDetailId) {
        List<MessageDoc> messageDocs = messageDocService.findByAppointmentDetailId(appointmentDetailId);
        pushNotificationService.notifyAppointmentMessage(messageDocs, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push Response Appointment Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/responseAppointmentMessage/{messageId}", method = RequestMethod.GET)
    public Object pushResponseAppointmentMessage(@PathVariable String messageId) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        pushNotificationService.notifyAppointmentResponseMessage(messageDoc, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push Interested/Not Interested Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/joinCompanyRequestMessage/{messageId}", method = RequestMethod.GET)
    public Object pushJoinCompanyRequestMessage(@PathVariable String messageId) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        pushNotificationService.notifyJoinRequestCompanyMessage(Arrays.asList(messageDoc), true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push Join Company Approval Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/joinCompanyApproval/{messageId}", method = RequestMethod.GET)
    public Object pushJoinCompanyApproval(@PathVariable String messageId) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        pushNotificationService.notifyJoinCompanyApproval(messageDoc, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push Interested/Not Interested Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/assignRole/{messageId}", method = RequestMethod.GET)
    public Object pushAssignRole(@PathVariable String messageId) {
        MessageDoc messageDoc = messageDocService.findById(new ObjectId(messageId));
        pushNotificationService.notifyAssignRoleMessage(messageDoc, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push View Candidate Profile Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/viewProfile/{id}", method = RequestMethod.GET)
    public Object pushViewCandidateProfile(@PathVariable String id) {
        ViewProfileDoc viewProfileDoc = viewProfileDocService.findById(new ObjectId(id));
        pushNotificationService.notifyViewCandidateProfile(viewProfileDoc, true);
        return success(ResponseStatus.SUCCESS);
    }

    @ApiOperation(value = "Push New Company Approval Message", httpMethod = "GET", response = BaseResp.class)
    @RequestMapping(value = "/companyApproval/{companyId}", method = RequestMethod.GET)
    public Object pushNewCompanyApproval(@PathVariable Long companyId) {
        Company company = companyService.findById(companyId);
        pushNotificationService.notifyCompanyApproval(company, true);
        return success(ResponseStatus.SUCCESS);
    }
}
