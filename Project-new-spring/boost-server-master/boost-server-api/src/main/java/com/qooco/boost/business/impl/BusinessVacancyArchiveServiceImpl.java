package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessVacancyArchiveService;
import com.qooco.boost.business.impl.abstracts.BusinessVacancyServiceAbstract;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.VacancyAssessmentLevelService;
import com.qooco.boost.data.oracle.services.VacancyCandidateService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.dto.user.CandidateClosedDTO;
import com.qooco.boost.models.dto.user.CandidateInfoDTO;
import com.qooco.boost.models.dto.user.ShortUserCVDTO;
import com.qooco.boost.models.dto.vacancy.VacancyClosedDTO;
import com.qooco.boost.models.dto.vacancy.VacancyClosedShortInfoDTO;
import com.qooco.boost.models.dto.vacancy.VacancyDTO;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import com.qooco.boost.models.request.EditVacancyRequest;
import com.qooco.boost.models.request.VacancyV2Req;
import com.qooco.boost.threads.models.EditVacancyInMongo;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ListUtil;
import com.qooco.boost.utils.ServletUriUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.qooco.boost.constants.Const.Vacancy.CandidateStatus.RECRUITED;

@Service
public class BusinessVacancyArchiveServiceImpl extends BusinessVacancyServiceAbstract implements BusinessVacancyArchiveService {
    @Autowired
    private VacancyCandidateService vacancyCandidateService;
    @Autowired
    private MessageCenterDocService messageCenterDocService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private VacancyAssessmentLevelService vacancyAssessmentLevelService;

    @Override
    public BaseResp getClosedCandidateOfVacancy(long id, int page, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        validateService.checkPermissionOnVacancy(id, user.getCompanyId(), user.getId());
        Page<VacancyCandidate> vacanciesCandidates = vacancyCandidateService.findByVacancyAndStatus(id, ImmutableList.of(RECRUITED), page, size);
        if (Objects.nonNull(vacanciesCandidates)) {
            List<VacancyCandidate> vacancyCandidates = vacanciesCandidates.getContent();
            MessageCenterDoc messageCenterDoc = messageCenterDocService.findByVacancy(id);

            Map<Long, ConversationDTO> conversationDTOMap = new HashMap<>();
            if (Objects.nonNull(messageCenterDoc)) {
                List<Long> userProfileIds = vacancyCandidates.stream()
                        .map(VacancyCandidate::getCandidate)
                        .map(UserCurriculumVitae::getUserProfile)
                        .map(UserProfile::getUserProfileId)
                        .distinct().collect(Collectors.toList());

                List<List<Long>> userProfileGroup = new ArrayList<>();
                userProfileIds.forEach(userProfileId -> userProfileGroup.add(Lists.newArrayList(user.getId(), userProfileId)));

                List<ConversationDoc> conversationDocs = conversationDocService.findByMessageCenterAndParticipant(messageCenterDoc.getId(), userProfileGroup);
                List<ObjectIdCount> count = messageDocService.countUnreadMessageGroupByConversation(messageCenterDoc.getId(), user.getId(), MessageConstants.RECEIVE_IN_HOTEL_APP);
                Map<ObjectId, Long> unReadMessageMap = count.stream().collect(Collectors.groupingBy(ObjectIdCount::getId, Collectors.summingLong(ObjectIdCount::getTotal)));

                conversationDocs.forEach(conversationDoc -> {
                    ConversationDTO conversationDTO = new ConversationDTO(conversationDoc, unReadMessageMap.get(conversationDoc.getId()), user.getToken());
                    List<UserProfileCvMessageEmbedded> participants = conversationDoc.getParticipants();
                    Optional<UserProfileCvMessageEmbedded> recipient = participants.stream().filter(participant -> !participant.getUserProfileId().equals(user.getId())).findFirst();
                    recipient.ifPresent(userProfileCvEmbedded -> conversationDTOMap.put(userProfileCvEmbedded.getUserProfileId(), conversationDTO));
                });
            }

            List<CandidateClosedDTO> result = new ArrayList<>();
            vacancyCandidates.forEach(vacancyCandidate -> {
                CandidateClosedDTO candidateClosedDTO = new CandidateClosedDTO();
                candidateClosedDTO.setCandidate(new ShortUserCVDTO(vacancyCandidate.getCandidate(), user.getLocale()));
                candidateClosedDTO.setConversation(conversationDTOMap.get(vacancyCandidate.getCandidate().getUserProfile().getUserProfileId()));
                result.add(candidateClosedDTO);
            });
            return new BaseResp<>(new PagedResultV2<>(result, page, vacanciesCandidates));
        }
        return new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
    }

    @Deprecated
    @Override
    public BaseResp getSuspendVacancy(int page, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Page<Vacancy> vacancies = vacancyService.findSuspendByCompanyAndUserProfile(user.getCompanyId(), user.getId(), page, size);
        if (Objects.nonNull(vacancies)) {
            List<VacancyDTO> result = new ArrayList<>();
            vacancies.getContent().forEach(vacancy -> result.add(new VacancyDTO(vacancy, qoocoDomainPath, user.getLocale())));
            return new BaseResp<>(new PagedResultV2<>(result, page, vacancies));
        }
        return new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
    }

    @Override
    public BaseResp getSuspendVacancyWithShortInfo(int page, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Page<Vacancy> vacancies = vacancyService.findSuspendByCompanyAndUserProfile(user.getCompanyId(), user.getId(), page, size);
        if (Objects.nonNull(vacancies)) {
            List<VacancyShortInformationDTO> result = new ArrayList<>();
            vacancies.getContent().forEach(vacancy -> result.add(new VacancyShortInformationDTO(vacancy, user.getLocale())));
            return new BaseResp<>(new PagedResultV2<>(result, page, vacancies));
        }
        return new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
    }

    @Deprecated
    @Override
    public BaseResp getClosedVacancy(int page, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Page<Vacancy> vacancies = vacancyService.findClosedByCompanyAndUserProfile(user.getCompanyId(), user.getId(), page, size);
        if (Objects.nonNull(vacancies) && CollectionUtils.isNotEmpty(vacancies.getContent())) {
            List<Long> vacancyIds = vacancies.getContent().stream().map(Vacancy::getId).collect(Collectors.toList());
            List<VacancyCandidate> vacancyCandidates = vacancyCandidateService.findByVacancyAndStatus(vacancyIds, ImmutableList.of(RECRUITED));
            List<VacancyClosedDTO> result = new ArrayList<>();
            vacancies.getContent().forEach(vacancy -> {
                final List<CandidateInfoDTO> candidateInfoDTOS = new ArrayList<>();
                vacancyCandidates.stream()
                        .filter(vacancyCandidate -> vacancyCandidate.getVacancy().getId().equals(vacancy.getId()))
                        .forEach(vacancyCandidate -> candidateInfoDTOS.add(new CandidateInfoDTO(vacancyCandidate.getCandidate())));

                result.add(new VacancyClosedDTO(vacancy, candidateInfoDTOS, qoocoDomainPath, user.getLocale()));
            });
            return new BaseResp<>(new PagedResultV2<>(result, page, vacancies));
        }
        return new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
    }

    @Override
    public BaseResp getClosedVacancyWithShortInfo(int page, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Page<Vacancy> vacancies = vacancyService.findClosedByCompanyAndUserProfile(user.getCompanyId(), user.getId(), page, size);
        if (Objects.nonNull(vacancies) && CollectionUtils.isNotEmpty(vacancies.getContent())) {
            List<Long> vacancyIds = vacancies.getContent().stream().map(Vacancy::getId).collect(Collectors.toList());
            List<VacancyCandidate> vacancyCandidates = vacancyCandidateService.findByVacancyAndStatus(vacancyIds, ImmutableList.of(RECRUITED));
            List<VacancyClosedShortInfoDTO> result = new ArrayList<>();
            vacancies.getContent().forEach(vacancy -> {
                final List<CandidateInfoDTO> candidateInfoDTOS = new ArrayList<>();
                vacancyCandidates.stream()
                        .filter(vacancyCandidate -> vacancyCandidate.getVacancy().getId().equals(vacancy.getId()))
                        .forEach(vacancyCandidate -> candidateInfoDTOS.add(new CandidateInfoDTO(vacancyCandidate.getCandidate())));

                result.add(new VacancyClosedShortInfoDTO(vacancy, candidateInfoDTOS, user.getLocale()));
            });
            return new BaseResp<>(new PagedResultV2<>(result, page, vacancies));
        }
        return new BaseResp<>(new PagedResultV2<>(new ArrayList<>()));
    }

    @Override
    public BaseResp restoreOrCloneVacancy(Long id, EditVacancyRequest request, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (Objects.nonNull(request)) {
            request.setId(id);
            Vacancy vacancy = initVacancy(request, authentication);
            Staff staff = validateService.checkPermissionOnVacancy(vacancy, user.getId());
            if (Const.Vacancy.Status.INACTIVE == vacancy.getStatus()) {
                VacancyV2Req req = convertToVacancyRequest(vacancy);
                Vacancy clonedVacancy = prepareVacancyFromInput(null, req, authentication);
                clonedVacancy.setSearchRange(vacancy.getSearchRange());
                saveClonedVacancy(clonedVacancy, qoocoDomainPath, vacancy.getId(), user.getLocale());
            } else {
                if (Const.Vacancy.Status.PERMANENT_SUSPEND == vacancy.getStatus()
                        || (Const.Vacancy.Status.OPENING == vacancy.getStatus()
                        && Objects.nonNull(vacancy.getStartSuspendDate())
                        && Objects.nonNull(vacancy.getSuspendDays()))) {
                    vacancy.setStatus(Const.Vacancy.Status.OPENING);
                    vacancy.setStartSuspendDate(null);
                    vacancy.setSuspendDays(null);
                    vacancy.setArchivist(staff);
                }
                vacancyAssessmentLevelService.deleteByVacancyId(vacancy.getId());
                vacancy = vacancyService.save(new Vacancy(vacancy));
                EditVacancyInMongo editVacancyInMongo = new EditVacancyInMongo(new Vacancy(vacancy));
                boostActorManager.editVacancyInMongo(editVacancyInMongo);
            }
        }
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    private VacancyV2Req convertToVacancyRequest(Vacancy vacancy) {
        VacancyV2Req req = new VacancyV2Req();
        req.setLogo(ServletUriUtils.getAbsolutePath(vacancy.getLogo()));
        req.setCompanyId(Objects.nonNull(vacancy.getCompany()) ? vacancy.getCompany().getCompanyId() : null);
        req.setJobId(Objects.nonNull(vacancy.getJob()) ? vacancy.getJob().getJobId() : null);
        req.setJobLocationId(Objects.nonNull(vacancy.getJobLocation()) ? vacancy.getJobLocation().getLocationId() : null);
        req.setSearchLocationId(Objects.nonNull(vacancy.getSearchLocation()) ? vacancy.getSearchLocation().getLocationId() : null);
        req.setContactPersonId(Objects.nonNull(vacancy.getContactPerson()) ? vacancy.getContactPerson().getStaffId() : null);
        req.setEducationId(Objects.nonNull(vacancy.getEducation()) ? vacancy.getEducation().getEducationId() : null);
        req.setCurrencyId(Objects.nonNull(vacancy.getCurrency()) ? vacancy.getCurrency().getCurrencyId() : null);
        req.setNumberOfSeat(vacancy.getNumberOfSeat());
        req.setSalary(vacancy.getSalary());
        req.setSalaryMax(vacancy.getSalaryMax());
        req.setHourSalary(vacancy.getHourSalary());
        req.setFullTime(vacancy.getWorkingType());
        req.setAsap(vacancy.getIsAsap());
        req.setExpectedStartDate(DateUtils.getUtcForOracle(vacancy.getExpectedStartDate()));
        req.setShortDescription(vacancy.getShortDescription());
        req.setFullDescription(vacancy.getFullDescription());
        if (CollectionUtils.isNotEmpty(vacancy.getVacancyLanguages())) {
            List<Long> nativeLanguageIds = vacancy.getVacancyLanguages().stream()
                    .filter(VacancyLanguage::isNative)
                    .map(l -> l.getLanguage().getLanguageId())
                    .collect(Collectors.toList());
            long[] nativeLanguageIdsArray = ListUtil.convertListToArray(nativeLanguageIds);
            req.setNativeLanguageIds(nativeLanguageIdsArray);
            List<Long> languageIds = vacancy.getVacancyLanguages().stream()
                    .filter(l -> !l.isNative())
                    .map(l -> l.getLanguage().getLanguageId())
                    .collect(Collectors.toList());
            long[] languageIdsArray = ListUtil.convertListToArray(languageIds);
            req.setNativeLanguageIds(languageIdsArray);
        }
        if (CollectionUtils.isNotEmpty(vacancy.getVacancyDesiredHours())) {
            List<Long> workHourIds = vacancy.getVacancyDesiredHours().stream()
                    .map(w -> w.getWorkingHour().getWorkingHourId())
                    .collect(Collectors.toList());
            long[] workHourIdsArray = ListUtil.convertListToArray(workHourIds);
            req.setWorkHourIds(workHourIdsArray);
        }
        if (CollectionUtils.isNotEmpty(vacancy.getVacancyBenefits())) {
            List<Long> benefitIds = vacancy.getVacancyBenefits().stream()
                    .map(b -> b.getBenefit().getBenefitId())
                    .collect(Collectors.toList());
            long[] benefitIdsArray = ListUtil.convertListToArray(benefitIds);
            req.setBenefitIds(benefitIdsArray);
        }
        if (CollectionUtils.isNotEmpty(vacancy.getVacancySoftSkills())) {
            List<Long> softSkillIds = vacancy.getVacancySoftSkills().stream()
                    .map(s -> s.getSoftSkill().getSoftSkillId())
                    .collect(Collectors.toList());
            long[] softSkillIdsArray = ListUtil.convertListToArray(softSkillIds);
            req.setSoftSkillIds(softSkillIdsArray);
        }
        if (CollectionUtils.isNotEmpty(vacancy.getVacancyAssessmentLevels())) {
            List<Long> assessmentLevelIds = vacancy.getVacancyAssessmentLevels().stream()
                    .map(a -> a.getAssessmentLevel().getId())
                    .collect(Collectors.toList());
            long[] assessmentLevelIdsArray = ListUtil.convertListToArray(assessmentLevelIds);
            Long[] levelIds = ArrayUtils.toObject(assessmentLevelIdsArray);
            req.setAssessmentLevelIds(levelIds);
        }
        return req;
    }
}
