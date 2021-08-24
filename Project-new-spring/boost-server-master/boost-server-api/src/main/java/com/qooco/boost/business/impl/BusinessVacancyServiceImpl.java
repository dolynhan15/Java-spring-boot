package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessAppointmentDetailService;
import com.qooco.boost.business.BusinessAppointmentService;
import com.qooco.boost.business.BusinessVacancyService;
import com.qooco.boost.business.impl.abstracts.BusinessVacancyServiceAbstract;
import com.qooco.boost.constants.Const;
import com.qooco.boost.constants.Const.Vacancy.CancelAppointmentReason;
import com.qooco.boost.constants.Const.Vacancy.CandidateStatus;
import com.qooco.boost.constants.Const.Vacancy.Status;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.model.VacancyHasAppointment;
import com.qooco.boost.data.mongo.embedded.RejectedUserCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.user.CandidateInfoDTO;
import com.qooco.boost.models.dto.vacancy.VacancyDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import com.qooco.boost.models.request.ClassifyCandidateReq;
import com.qooco.boost.models.request.EditVacancyRequest;
import com.qooco.boost.models.request.VacancyBaseReq;
import com.qooco.boost.models.request.VacancyV2Req;
import com.qooco.boost.models.request.appointment.AppointmentBaseReq;
import com.qooco.boost.models.response.VacancyCandidateResp;
import com.qooco.boost.models.sdo.VacancyCandidateSDO;
import com.qooco.boost.threads.models.CancelAppointmentDetailInMongo;
import com.qooco.boost.threads.models.EditVacancyInMongo;
import com.qooco.boost.threads.models.messages.AppliedVacancyMessage;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.transform;
import static com.qooco.boost.constants.Const.Vacancy.CandidateStatus.*;
import static com.qooco.boost.data.enumeration.AppointmentStatus.getAvailableStatus;
import static com.qooco.boost.enumeration.ResponseStatus.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.collections4.ListUtils.union;

@Service
public class BusinessVacancyServiceImpl extends BusinessVacancyServiceAbstract implements BusinessVacancyService {
    protected Logger logger = LogManager.getLogger(BusinessVacancyServiceImpl.class);

    @Autowired
    private VacancyBenefitService vacancyBenefitService;
    @Autowired
    private VacancyLanguageService vacancyLanguageService;
    @Autowired
    private VacancySoftSkillService vacancySoftSkillService;
    @Autowired
    private VacancyDesiredHourService vacancyDesiredHourService;
    @Autowired
    private VacancyAssessmentLevelService vacancyAssessmentLevelService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    private BusinessAppointmentService businessAppointmentService;
    @Autowired
    private BusinessAppointmentDetailService businessAppointmentDetailService;
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private AppointmentDetailService appointmentDetailService;
    @Autowired
    private VacancyCandidateService vacancyCandidateService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private AppointmentFeedbackService appointmentFeedbackService;
    @Autowired
    protected VacancyProcessingService vacancyProcessingService;

    @Override
    @Transactional
    public BaseResp saveV2(Long id, VacancyV2Req vacancyReq, Authentication authentication) {
        validateVacancyInputV2(vacancyReq);
        Long updateOwner = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        Vacancy vacancy = prepareVacancyFromInput(id, vacancyReq, authentication);
        Staff updater = validateService.checkExistsStaffInApprovedCompany(vacancyReq.getCompanyId(), updateOwner);

        updateVacancyFromDB(id, vacancyReq, updater, vacancy, authentication);
        VacancyDTO resultDTO = saveVacancy(vacancy, qoocoDomainPath, ((AuthenticatedUser) authentication.getPrincipal()).getLocale());
        return new BaseResp<>(resultDTO);
    }

    @Override
    @Transactional
    public BaseResp save(Long id, VacancyV2Req vacancyReq, Authentication authentication) {
        validateVacancyInput(vacancyReq);
        Long updateOwner = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        Vacancy vacancy = prepareVacancyFromInput(id, vacancyReq, authentication);
        Staff updater = validateService.checkExistsStaffInApprovedCompany(vacancyReq.getCompanyId(), updateOwner);

        City city = validateService.checkExistsCity(vacancyReq.getCityId());
        City searchCity = validateService.checkExistsCity(vacancyReq.getSearchCityId());
        vacancy.setCity(city);
        vacancy.setSearchCity(searchCity);

        List<Long> cityIds = Lists.newArrayList(vacancyReq.getCityId(), vacancyReq.getSearchCityId());
        cityIds = cityIds.stream().distinct().collect(toList());
        List<Location> locations = getLocationForCity(vacancyReq.getCompanyId(), cityIds);

        vacancy.setJobLocation(getLocationFromCity(vacancyReq.getCityId(), locations));
        vacancy.getJobLocation().setUsed(true);
        vacancy.setSearchLocation(getLocationFromCity(vacancyReq.getSearchCityId(), locations));
        vacancy.getSearchLocation().setUsed(true);
        vacancy.setSalaryMax(vacancy.getSalary());

        updateVacancyFromDB(id, vacancyReq, updater, vacancy, authentication);
        VacancyDTO resultDTO = saveVacancy(vacancy, qoocoDomainPath, ((AuthenticatedUser) authentication.getPrincipal()).getLocale());
        return new BaseResp<>(resultDTO);
    }


    @Override
    public BaseResp get(Long id, Authentication authentication) {
        if (Objects.isNull(id)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        Vacancy result = vacancyService.findById(id);
        if (Objects.isNull(result)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_VACANCY);
        }

        VacancyDTO resultDTO = new VacancyDTO(result, qoocoDomainPath, ((AuthenticatedUser) authentication.getPrincipal()).getLocale());
        return new BaseResp<>(resultDTO);
    }

    @Override
    public BaseResp delete(Long id) {
        if (Objects.isNull(id)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        vacancyService.delete(id);
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp sync(Long id) {
        boostActorManager.saveVacancyInMongoByIds(Lists.newArrayList(id));
        return null;
    }

    @Override
    public BaseResp getOpeningVacanciesOfCompany(long companyId, long userProfileId, Authentication authentication) {
        validateService.checkExistsCompany(companyId);
        List<Vacancy> vacancies = vacancyService.findOpeningVacancyByUserAndCompany(userProfileId, companyId);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<VacancyCandidateResp> vacancyCandidateRespList = getVacancyCandidateRespList(vacancies, user.getLocale());
        return new BaseResp<>(vacancyCandidateRespList);
    }

    private List<VacancyCandidateResp> getVacancyCandidateRespList(List<Vacancy> vacancies, String locale) {
        List<VacancyCandidateResp> vacancyCandidateRespList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(vacancies)) {
            List<Long> vacancyIds = vacancies.stream().map(Vacancy::getId).collect(toList());
            List<VacancyDoc> vacancyDocs = vacancyDocService.findAllById(vacancyIds);
            Optional<VacancyDoc> vacancyDocOptional;
            List<CandidateInfoDTO> candidateInfoList;
            long numberOfCandidate;
            long numberOfAppointment;
            for (Vacancy vacancy : vacancies) {
                candidateInfoList = new ArrayList<>();
                numberOfCandidate = 0;
                numberOfAppointment = 0;
                vacancyDocOptional = vacancyDocs.stream()
                        .filter(v -> v.getId().equals(vacancy.getId())).findFirst();
                if (vacancyDocOptional.isPresent()) {
                    VacancyDoc vacancyDoc = vacancyDocOptional.get();
                    numberOfCandidate = vacancyDoc.getNumberOfCandidate();
                    if (CollectionUtils.isNotEmpty(vacancyDoc.getAppointments())) {
                        numberOfAppointment = vacancyDoc.getAppointments().stream().filter(a -> !a.isDeleted()).count();
                    }
                    if (CollectionUtils.isNotEmpty(vacancyDoc.getCandidateProfiles())) {
                        for (UserProfileCvEmbedded userProfileCvEmbedded : vacancyDoc.getCandidateProfiles()) {
                            candidateInfoList.add(new CandidateInfoDTO(userProfileCvEmbedded.getUserProfileCvId(),
                                    userProfileCvEmbedded.getAvatar()));
                        }
                    }
                }
                vacancyCandidateRespList.add(new VacancyCandidateResp(new VacancyShortInformationDTO(vacancy, locale),
                        candidateInfoList, numberOfCandidate, numberOfAppointment));
            }
        }
        return vacancyCandidateRespList;
    }

    @Override
    public BaseResp classifyCandidate(ClassifyCandidateReq req, Authentication authentication) {
        Vacancy vacancy = validateService.checkExistsValidVacancy(req.getId());
        if (Const.Vacancy.Status.INACTIVE == vacancy.getVacancyStatus()) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_INACTIVE);
        } else if (Const.Vacancy.Status.SUSPEND == vacancy.getVacancyStatus()) {
            throw new NoPermissionException(ResponseStatus.VACANCY_IS_SUSPENDED);
        }
        UserCurriculumVitae candidate = validateService.checkExistsUserCurriculumVitae(req.getUserCVId());
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Staff staff = validateService.checkExistsStaffInCompany(user.getCompanyId(), user.getId());
        if (Const.Vacancy.ClassifyAction.APPLIED == req.getAction()) {
            boolean result = addAppliedUserCvIds(req.getId(), req.getUserCVId());
            if (result) {
                AppliedVacancyMessage vacancyMessage = new AppliedVacancyMessage();

                //TODO: Check again 2 action below, All information is get again in actor
                UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findById(req.getUserCVId());
                UserFit sender = userFitService.findById(vacancy.getContactPerson().getUserFit().getUserProfileId());

                vacancyMessage.setVacancy(vacancy);
                vacancyMessage.setRecipient(userCurriculumVitae.getUserProfile());
                vacancyMessage.setSender(sender);
                boostActorManager.sendAppliedMessageActor(vacancyMessage);

            }
        } else if (Const.Vacancy.ClassifyAction.REJECTED == req.getAction()) {
            addRejectedUserCv(req.getId(), req.getUserCVId());
        }

        VacancyProcessing vacancyProcessing = new VacancyProcessing(candidate, vacancy, staff, req.getAction());
        vacancyProcessingService.save(Lists.newArrayList(vacancyProcessing));
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    @Transactional
    public BaseResp editVacancy(EditVacancyRequest vacancyReq, Authentication authentication) {
        Vacancy vacancy = initVacancy(vacancyReq, authentication);
        vacancyAssessmentLevelService.deleteByVacancyId(vacancyReq.getId());
        vacancy = vacancyService.save(vacancy);
        VacancyDTO resultDTO = new VacancyDTO(vacancy, qoocoDomainPath, ((AuthenticatedUser) authentication.getPrincipal()).getLocale());
        EditVacancyInMongo editVacancyInMongo = new EditVacancyInMongo(new Vacancy(vacancy));
        boostActorManager.editVacancyInMongo(editVacancyInMongo);
        return new BaseResp<>(resultDTO);
    }

    @Override
    public BaseResp getOpeningVacancies(long companyId, Long userProfileId, int page, int size, Authentication authentication) {
        validateService.checkExistsCompany(companyId);
        Page<Vacancy> vacancies = vacancyService.findOpeningVacancyByUserAndCompany(userProfileId, companyId, page, size);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<VacancyCandidateResp> vacancyCandidateResps = getVacancyCandidateRespList(vacancies.getContent(), user.getLocale());
        PagedResult<VacancyCandidateResp> vacancyCandidateRespPage = new PagedResult<>(vacancyCandidateResps, page, vacancies.getSize(),
                vacancies.getTotalPages(), vacancies.getTotalElements(), vacancies.hasNext(), vacancies.hasPrevious());
        return new BaseResp<>(vacancyCandidateRespPage);
    }

    @Override
    public BaseResp getOpeningVacanciesHavingAppointments(long companyId, Long userProfileId, int page, int size, Authentication authentication) {
        validateService.checkExistsCompany(companyId);
        Staff staff = validateService.checkExistsStaffInApprovedCompany(companyId, userProfileId);
        List<String> roleNames = CompanyRole.valueOf(staff.getRole().getName()).getRolesForAppointmentManager();
        Page<VacancyHasAppointment> vacancies = appointmentDetailService.findOpeningVacancyHavingAppointmentsByUserAndCompany(userProfileId, companyId, roleNames, page, size);
        if (Objects.nonNull(vacancies) && CollectionUtils.isNotEmpty(vacancies.getContent())) {
            AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
            List<VacancyCandidateResp> resps = doGetVacancyHavingAppointmentList(vacancies.getContent().stream().map(
                    VacancyHasAppointment::getVacancy).collect(toList()), roleNames, staff.getStaffId(), user.getLocale());
            return new BaseResp<>(new PagedResultV2<>(resps, page, vacancies));
        }
        return new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
    }

    @Override
    public BaseResp getCandidatesOfVacancy(long vacancyId, int page, int size, Authentication authentication) {
        BaseResp result = new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Vacancy vacancy = vacancyService.findOpeningByVacancyAndUserProfileAndCompany(vacancyId, user.getId(), user.getCompanyId());
        if (Objects.nonNull(vacancy)) {
            if (Const.Vacancy.Status.INACTIVE == vacancy.getVacancyStatus()) {
                throw new NoPermissionException(ResponseStatus.VACANCY_IS_INACTIVE);
            } else if (Const.Vacancy.Status.SUSPEND == vacancy.getVacancyStatus()) {
                throw new NoPermissionException(ResponseStatus.VACANCY_IS_SUSPENDED);
            }

            VacancyDoc vacancyDoc = vacancyDocService.findById(vacancyId);
            if (Objects.nonNull(vacancyDoc)) {
                List<Long> userCVIdCandidates = new ArrayList<>();
                List<MessageDoc> interestedApplicants = messageDocService.findInterestedApplicantOrApplicationAppointmentMessage(vacancyDoc);
                if (CollectionUtils.isNotEmpty(interestedApplicants)) {
                    List<Long> interestedUserCVIds = interestedApplicants.stream()
                            .filter(u -> Objects.nonNull(u.getRecipient().getUserProfileCvId()))
                            .map(u -> u.getRecipient().getUserProfileCvId()).collect(toList());
                    userCVIdCandidates.addAll(interestedUserCVIds);
                }
                if (MapUtils.isNotEmpty(vacancyDoc.getAppointmentSlots())) {
                    List<Integer> acceptedStatus = AppointmentStatus.getAcceptedStatus();
                    List<Long> appointmentSlotCandidateIds = new ArrayList<>();
                    vacancyDoc.getAppointmentSlots().forEach((userCVId, slots) -> {
                        boolean isAcceptedSlots = slots.stream()
                                .anyMatch(slot -> acceptedStatus.contains(AppointmentStatus.convertFromValue(slot.getStatus()).getValue()));
                        if (isAcceptedSlots) {
                            appointmentSlotCandidateIds.add(userCVId);
                        }
                    });
                    userCVIdCandidates.addAll(appointmentSlotCandidateIds);
                }
                if (CollectionUtils.isNotEmpty(userCVIdCandidates)) {
                    userCVIdCandidates = userCVIdCandidates.stream().distinct().collect(toList());
                    List<VacancyCandidate> feedbackCandidates = vacancyCandidateService.findByVacancyAndStatus(vacancyId, ImmutableList.of(RECRUITED, NOT_RECRUITED));
                    List<Long> feedbackCandidateIds = CollectionUtils.isNotEmpty(feedbackCandidates)
                            ? feedbackCandidates.stream().map(it -> it.getCandidate().getCurriculumVitaeId()).distinct().collect(toList())
                            : new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(feedbackCandidateIds)) {
                        userCVIdCandidates.removeAll(feedbackCandidateIds);
                    }

                    if (CollectionUtils.isNotEmpty(userCVIdCandidates)) {
                        Page<UserCurriculumVitae> userCVs = userCurriculumVitaeService.findByUserIdsWithPagination(userCVIdCandidates, page, size);
                        if (CollectionUtils.isNotEmpty(userCVs.getContent())) {
                            List<CandidateInfoDTO> candidateInfoDTOS = userCVs.getContent().stream().map(CandidateInfoDTO::new).collect(toList());
                            result.setData(new PagedResultV2<>(candidateInfoDTOS, page, userCVs));
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public BaseResp closeCandidateOfVacancy(long vacancyId, long candidateId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Vacancy vacancy = ofNullable(vacancyService.findByVacancyAndUserProfileAndCompany(vacancyId, user.getId(), user.getCompanyId()))
                .map(Optional::of)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_VACANCY))
                .filter(it -> it.getVacancyStatus() != Status.INACTIVE).map(Optional::of)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_IS_INACTIVE))
                .filter(it -> it.getVacancyStatus() != Status.SUSPEND)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_IS_SUSPENDED));

        final Integer numOfSeat = vacancy.getNumberOfSeat();
        List<VacancyCandidate> vacancyCandidates = Optional.of(emptyIfNull(vacancy.getClosedCandidates()))
                .filter(it -> numOfSeat == null || it.size() < numOfSeat).map(Optional::of)
                .orElseThrow(() -> new NoPermissionException(VACANCY_IS_CLOSED))
                .filter(it -> it.stream().noneMatch(c -> c.getCandidate().getCurriculumVitaeId().equals(candidateId)))
                .orElseThrow(() -> new InvalidParamException(CANDIDATE_IS_ALREADY_CLOSED));

        VacancyCandidate candidate = new VacancyCandidate(
                validateService.checkExistsUserCurriculumVitae(candidateId),
                vacancy,
                validateService.checkExistsStaffInApprovedCompany(user.getCompanyId(), user.getId()),
                CandidateStatus.RECRUITED);

        vacancy.setClosedCandidates(vacancyCandidateService.save(union(vacancyCandidates, ImmutableList.of(candidate))));
        boolean isFull = numOfSeat != null && numOfSeat == vacancy.getClosedCandidates().size();
        if (isFull) {
            vacancy.setStatus(Status.INACTIVE);
            vacancy = vacancyService.save(vacancy);
        }
        doFeedbackCandidateInVacancy(candidate.getArchivist(), vacancy, candidate.getCandidate(), RECRUITED);
        Map<Long, UserCvDoc> userCVs = ofNullable(vacancy.getClosedCandidates())
                .filter(it -> !it.isEmpty())
                .map(it -> userCvDocService.findAllById(it.stream().map(obj -> obj.getCandidate().getCurriculumVitaeId()).collect(toList())))
                .orElseGet(ImmutableList::of)
                .stream()
                .collect(Collectors.toMap(UserCvDoc::getId, Function.identity()));

        List<UserProfileCvEmbedded> closedCandidates = transform(vacancy.getClosedCandidates(), it -> MongoConverters.convertToUserProfileCvEmbedded(userCVs.get(it.getCandidate().getCurriculumVitaeId())));
        vacancyDocService.updateStatusAndClosedCandidatesOfVacancy(vacancy.getId(), vacancy.getStatus(), closedCandidates);

        final Vacancy finalVacancy = vacancy;
        ofNullable(isFull ? appointmentDetailService.findByStatusOfVacancyAndNotExpired(getAvailableStatus(), vacancyId)
                : appointmentDetailService.findByUserCVIdAndStatusOfVacancyAndNotExpired(candidateId, getAvailableStatus(), vacancyId))
                .ifPresent(it -> {
                    businessAppointmentDetailService.cancelAppointmentDetails(it);
                    doEditAppointmentInMongo(finalVacancy, it, isFull ? CancelAppointmentReason.INACTIVE : CancelAppointmentReason.RECRUITED, new VacancyCandidateSDO(candidateId, finalVacancy));
                });
        return new BaseResp<>(new VacancyDTO(vacancy, qoocoDomainPath, user.getLocale()));
    }

    public BaseResp<VacancyDTO> declineCandidateOfVacancy(long vacancyId, long candidateId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Vacancy vacancy = ofNullable(vacancyService.findByVacancyAndUserProfileAndCompany(vacancyId, user.getId(), user.getCompanyId()))
                .map(Optional::of)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_VACANCY))
                .filter(it -> it.getVacancyStatus() != Status.INACTIVE)
                .map(Optional::of)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_IS_INACTIVE))
                .filter(it -> it.getVacancyStatus() != Status.SUSPEND)
                .orElseThrow(() -> new EntityNotFoundException(VACANCY_IS_SUSPENDED));

        Optional.of(emptyIfNull(vacancy.getClosedCandidates()))
                .filter(it -> it.stream().noneMatch(c -> c.getCandidate().getCurriculumVitaeId().equals(candidateId)))
                .orElseThrow(() -> new InvalidParamException(CANDIDATE_IS_ALREADY_CLOSED));
        ofNullable(appointmentDetailService.findByUserCVIdAndStatusOfVacancyAndExpired(candidateId, getAvailableStatus(), vacancyId))
                .filter(it -> !it.isEmpty()).orElseThrow(() -> new InvalidParamException(NOT_FOUND_APPOINTMENT_DETAIL));
        UserCurriculumVitae curriculumVitae = validateService.checkExistsUserCurriculumVitae(candidateId);
        VacancyCandidate candidate = ofNullable(vacancyCandidateService.findByVacancyAndUserProfileAndStatus(vacancyId, curriculumVitae.getUserProfile().getUserProfileId(), ALL_CANDIDATE_STATUSES))
                .orElseGet(() -> new VacancyCandidate(curriculumVitae, vacancy, validateService.checkExistsStaffInApprovedCompany(user.getCompanyId(), user.getId()), CandidateStatus.UNKNOWN));
        if (candidate.getStatus() != CandidateStatus.NOT_RECRUITED) {
            candidate.setStatus(CandidateStatus.NOT_RECRUITED);
            vacancyCandidateService.save(candidate);
        }
        doFeedbackCandidateInVacancy(candidate.getArchivist(), vacancy, curriculumVitae, CandidateStatus.NOT_RECRUITED);
        return new BaseResp<>(new VacancyDTO(vacancy, qoocoDomainPath, user.getLocale()));
    }

    @Override
    public BaseResp syncAllVacancies(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        UserProfile isRootAdmin = userProfileService.checkUserProfileIsRootAdmin(authenticatedUser.getId());
        if (Objects.isNull(isRootAdmin)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        long vacancyId = 0;
        List<Vacancy> vacancies = vacancyService.findVacancies(vacancyId, Constants.DEFAULT_LIMITED_ITEM);
        while (CollectionUtils.isNotEmpty(vacancies)) {
            vacancyDocService.updateStatusOfVacancy(vacancies);
            vacancyId = vacancies.stream().max(Comparator.comparing(Vacancy::getId)).get().getId();
            vacancies = vacancyService.findVacancies(vacancyId, Constants.DEFAULT_LIMITED_ITEM);
        }
        return new BaseResp();
    }

    @Override
    public BaseResp suspendVacancy(long vacancyId, Integer suspendDays, Authentication authentication) {
        if (Objects.nonNull(suspendDays) && suspendDays <= 0) {
            throw new InvalidParamException(ResponseStatus.SUSPENDED_DAYS_IS_INVALID);
        }
        Vacancy vacancy = validateService.checkOpeningVacancy(vacancyId);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Staff staff = validateService.checkPermissionOnVacancy(vacancy, user.getId());

        Date now = DateUtils.nowUtcForOracle();
        if (Objects.isNull(suspendDays)) {
            vacancy.setStatus(Const.Vacancy.Status.PERMANENT_SUSPEND);
        } else {
            vacancy.setStatus(Const.Vacancy.Status.OPENING);
            vacancy.setStartSuspendDate(now);
        }
        vacancy.setSuspendDays(suspendDays);
        vacancy.setArchivist(staff);
        vacancy.setUpdatedBy(user.getId());
        vacancy.setUpdatedDate(now);
        vacancyService.save(vacancy);
        Date endDate;
        Date startDate = DateUtils.toUtcForOracle(vacancy.getStartSuspendDate());
        if (vacancy.getStatus() == Const.Vacancy.Status.PERMANENT_SUSPEND) {
            endDate = DateUtils.MAX_DATE;
        } else {
            endDate = DateUtils.addDays(startDate, vacancy.getSuspendDays());
        }

        List<AppointmentDetail> appointmentDetails = appointmentDetailService
                .findPendingAndAcceptedAppointmentInSuspendRange(vacancy.getId(), startDate, endDate);

        List<AppointmentDetail> cancelledAppointments = businessAppointmentDetailService.cancelAppointmentDetails(appointmentDetails);

        doEditAppointmentInMongo(vacancy, cancelledAppointments, Const.Vacancy.CancelAppointmentReason.SUSPEND, null);

        return new BaseResp<>();
    }

    private void doEditAppointmentInMongo(Vacancy vacancy, List<AppointmentDetail> cancelledAppointments,
                                          int cancelReason, VacancyCandidateSDO vacancyCandidate) {
        CancelAppointmentDetailInMongo appointmentDetailSDO = new CancelAppointmentDetailInMongo();
        appointmentDetailSDO.setReason(cancelReason);
        Map<Appointment, List<AppointmentDetail>> cancelEventMap = new HashMap<>();
        List<Long> appointmentsId = cancelledAppointments.stream()
                .map(AppointmentDetail::getAppointmentId).collect(toList());
        appointmentsId.forEach(id -> {
            List<AppointmentDetail> cancelAppointmentEvents = cancelledAppointments.stream().filter
                    (ad -> ad.getAppointmentId() == id).collect(toList());
            if (!cancelAppointmentEvents.isEmpty()) {
                cancelEventMap.put(cancelAppointmentEvents.get(0).getAppointment(), cancelAppointmentEvents);
            }
        });
        appointmentDetailSDO.setCancelEventMap(cancelEventMap);

        EditVacancyInMongo editVacancyInMongo = new EditVacancyInMongo(new Vacancy(vacancy));
        editVacancyInMongo.setCancelAppointment(appointmentDetailSDO);
        editVacancyInMongo.setCancelReason(cancelReason);
        editVacancyInMongo.setVacancyCandidate(vacancyCandidate);
        boostActorManager.editVacancyInMongo(editVacancyInMongo);
    }

    private List<VacancyCandidateResp> doGetVacancyHavingAppointmentList(List<Vacancy> vacancies, List<String> roleNames, long staffId, String locale) {
        List<VacancyCandidateResp> resps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(vacancies)) {
            List<Long> vacancyIds = vacancies.stream().distinct().map(Vacancy::getId).collect(toList());
            List<AppointmentDetail> events = appointmentDetailService.findAvailableByVacancyIdsAndStatuses(vacancyIds,
                    AppointmentStatus.getAcceptedStatus(), roleNames, staffId);
            for (Vacancy vacancy : vacancies) {
                //TODO: Should write a native SQL to file the ID => It make load big data in this session
                List<Long> appointmentIds = vacancy.getVacancyAppointments().stream().distinct()
                        .filter(a -> !a.getIsDeleted()).map(Appointment::getId).collect(toList());
                List<AppointmentDetail> appointmentDetails = new ArrayList<>();
                events.stream().filter(e -> appointmentIds.contains(e.getAppointmentId())).forEach(appointmentDetails::add);
                List<CandidateInfoDTO> candidateInfo = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(appointmentDetails)) {
                    appointmentDetails.forEach(ad -> candidateInfo.add(new CandidateInfoDTO(ad)));
                }
                resps.add(new VacancyCandidateResp(vacancy, candidateInfo, appointmentIds.size(), locale));
            }
        }
        return resps;
    }

    private Vacancy updateVacancyFromDB(Long id, VacancyV2Req req, Staff updater, Vacancy vacancy, Authentication authentication) {
        if (Objects.nonNull(id) && id > 0) {

            Vacancy vacancyExists = validateService.checkExistsValidVacancy(id);
            validateService.checkPermissionOnVacancy(vacancyExists, updater);

            vacancyBenefitService.deleteByVacancyId(id);
            vacancyDesiredHourService.deleteByVacancyId(id);
            vacancyLanguageService.deleteByVacancyId(id);
            vacancySoftSkillService.deleteByVacancyId(id);
            vacancyAssessmentLevelService.deleteByVacancyId(id);

            if (CollectionUtils.isNotEmpty(req.getAppointments())) {
                List<Long> appointmentIdsReqs = req.getAppointments().stream()
                        .filter(at -> Objects.nonNull(at.getId()))
                        .map(AppointmentBaseReq::getId).collect(toList());

                List<Long> appointmentIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(vacancy.getVacancyAppointments())) {
                    appointmentIds = vacancyExists.getVacancyAppointments().stream()
                            .filter(at -> Objects.nonNull(at.getId()))
                            .map(Appointment::getId).collect(toList());
                }

                List<Long> deleteIds = appointmentIds.stream().filter(ap -> !appointmentIdsReqs.contains(ap)).collect(toList());
                if (CollectionUtils.isNotEmpty(deleteIds)) {
                    businessAppointmentService.deleteAppointments(deleteIds, authentication);
                }
            }

            vacancy.setId(vacancyExists.getId());
            vacancy.setCreatedBy(vacancyExists.getCreatedBy());
            vacancy.setCreatedDate(vacancyExists.getCreatedDate());
            vacancy.setUpdatedBy(updater.getUserFit().getUserProfileId());
            vacancy.setUpdatedDate(DateUtils.nowUtcForOracle());
            vacancy.setStatus(vacancyExists.getStatus());
            vacancy.setSearchRange(vacancyExists.getSearchRange());
        }
        return vacancy;
    }

    private void validateVacancyInputV2(VacancyBaseReq vacancyReq) {
        removeDuplicates(vacancyReq);
        validateRequiredCommon(vacancyReq);
        if (Objects.isNull(vacancyReq.getJobLocationId())
                || Objects.isNull(vacancyReq.getSearchLocationId())
        ) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        validateBusinessLogicV2(vacancyReq);
    }

    private void validateVacancyInput(VacancyBaseReq vacancyReq) {
        removeDuplicates(vacancyReq);
        validateRequired(vacancyReq);
        validateBusinessLogic(vacancyReq);
    }

    private Location getLocationFromCity(Long cityId, List<Location> resources) {
        Optional<Location> jobLocationOptional = resources.stream().filter(l -> cityId.equals(l.getCity().getCityId())).findFirst();
        return jobLocationOptional.orElse(null);
    }

    private boolean addAppliedUserCvIds(long vacancyId, long userCV) {
        VacancyDoc vacancyDoc = vacancyDocService.findById(vacancyId);
        List<Long> appliedUserCvIds = vacancyDoc.getAppliedUserCvId();
        if (null == appliedUserCvIds) {
            appliedUserCvIds = new ArrayList<>();
        }

        boolean isExist = appliedUserCvIds.stream().anyMatch(upf -> upf == userCV);
        if (!isExist) {
            List<RejectedUserCvEmbedded> rejectedUserCVs = vacancyDoc.getRejectedUserCv();
            ofNullable(rejectedUserCVs).ifPresent(it -> it.remove(new RejectedUserCvEmbedded(userCV)));

            appliedUserCvIds.add(userCV);
            vacancyDoc.setAppliedUserCvId(appliedUserCvIds);
            vacancyDoc.setRejectedUserCv(rejectedUserCVs);
            vacancyDoc.setUpdatedDate(DateUtils.toServerTimeForMongo());
            vacancyDocService.addAppliedUserCvIds(appliedUserCvIds, rejectedUserCVs, vacancyId);
            boostActorManager.updateMessageCenterDocInMongoActor(vacancyDoc);
            logger.info(StringUtil.append("Update vacancy Id =", vacancyDoc.getId().toString()));
            return true;
        }
        return false;
    }

    private void addRejectedUserCv(long vacancyId, long userCV) {
        VacancyDoc vacancyDoc = vacancyDocService.findById(vacancyId);
        List<RejectedUserCvEmbedded> rejectedUserCVs = vacancyDoc.getRejectedUserCv();
        if (null == rejectedUserCVs) {
            rejectedUserCVs = new ArrayList<>();
        }

        int index = rejectedUserCVs.indexOf(new RejectedUserCvEmbedded(userCV, DateUtils.toServerTimeForMongo()));
        if (index < 0) {
            rejectedUserCVs.add(new RejectedUserCvEmbedded(userCV, DateUtils.toServerTimeForMongo()));
        } else {
            rejectedUserCVs.get(index).setRejectedDate(DateUtils.toServerTimeForMongo());
        }

        vacancyDoc.setRejectedUserCv(rejectedUserCVs);
        vacancyDoc.setUpdatedDate(DateUtils.toServerTimeForMongo());
        vacancyDocService.addRejectedUserCv(rejectedUserCVs, vacancyId);
        boostActorManager.updateMessageCenterDocInMongoActor(vacancyDoc);
        logger.info(StringUtil.append("Update vacancy Id =", vacancyDoc.getId().toString()));
    }

    private void doFeedbackCandidateInVacancy(Staff staff, Vacancy vacancy, UserCurriculumVitae userCurriculumVitae, int status) {
        long now = new Date().getTime();
        Optional.ofNullable(getAppointmentDetailOfCandidateInVacancy(vacancy, userCurriculumVitae))
                .filter(it -> !it.isEmpty())
                .ifPresent(it -> appointmentFeedbackService.save(
                        it.stream().map(ap -> new AppointmentFeedback(staff, ap, now, status))::iterator));
    }

    private List<AppointmentDetail> getAppointmentDetailOfCandidateInVacancy(Vacancy vacancy, UserCurriculumVitae userCurriculumVitae) {
        long lastFeedbackDate = appointmentFeedbackService.findLastFeedbackByVacancyAndCandidate(
                vacancy.getId(), userCurriculumVitae.getCurriculumVitaeId())
                .map(af -> DateUtils.getUtcForOracle(af.getFeedbackDate()).getTime()).orElse(0L);
        return appointmentDetailService.findAcceptedByUserCVIdOfVacancyAndExpiredAfter(
                userCurriculumVitae.getCurriculumVitaeId(), vacancy.getId(), DateUtils.toUtcForOracle(new Date(lastFeedbackDate)));
    }
}
