package com.qooco.boost.threads.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import com.google.common.collect.ImmutableList;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.mongo.services.embedded.AppointmentEmbeddedService;
import com.qooco.boost.data.mongo.services.embedded.VacancyEmbeddedService;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.oracle.entities.VacancyAssessmentLevel;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.data.oracle.services.VacancyService;
import com.qooco.boost.models.sdo.VacancyCandidateSDO;
import com.qooco.boost.models.sdo.VacancyClonedSDO;
import com.qooco.boost.threads.models.*;
import com.qooco.boost.threads.services.StaffActorService;
import com.qooco.boost.threads.services.VacancyActorService;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveVacancyInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveVacancyInMongoActor.class);
    public static final String ACTOR_NAME = "saveVacancyInMongoActor";

    private VacancyDocService vacancyDocService;
    private VacancyService vacancyService;
    private VacancyActorService vacancyActorService;
    private StaffActorService staffActorService;
    private StaffService staffService;
    private VacancyEmbeddedService vacancyEmbeddedService;
    private AppointmentEmbeddedService appointmentEmbeddedService;
    private UserCvDocService userCvDocService;

    public SaveVacancyInMongoActor(VacancyDocService vacancyDocService,
                                   VacancyActorService vacancyActorService,
                                   StaffActorService staffActorService,
                                   StaffService staffService,
                                   VacancyService vacancyService,
                                   VacancyEmbeddedService vacancyEmbeddedService,
                                   AppointmentEmbeddedService appointmentEmbeddedService,
                                   UserCvDocService userCvDocService) {
        this.vacancyDocService = vacancyDocService;
        this.vacancyActorService = vacancyActorService;
        this.staffActorService = staffActorService;
        this.staffService = staffService;
        this.vacancyService = vacancyService;
        this.vacancyEmbeddedService = vacancyEmbeddedService;
        this.appointmentEmbeddedService = appointmentEmbeddedService;
        this.userCvDocService = userCvDocService;
    }

    @Override
    public void onReceive(Object message) {
        logger.info("Start Update vacancy ");
        if (message instanceof Vacancy) {
            Vacancy vacancy = ((Vacancy) message);
            Long vacancyId = vacancy.getId();
            if (Objects.nonNull(vacancyId)) {
                VacancyDoc vacancyDoc = convertToVacancyDoc(vacancy, false);
                VacancyDoc result = vacancyDocService.save(vacancyDoc);
                updateVacancyEmbedded(result);
                logger.info(StringUtil.append("Update vacancy Id =", result.getId().toString()));
            }
        } else if (message instanceof VacancyClonedSDO) {
            Vacancy vacancy = ((VacancyClonedSDO) message).getClonedVacancy();
            Long oldVacancyId = ((VacancyClonedSDO) message).getOldVacancyId();
            if (Objects.nonNull(vacancy.getId())) {
                VacancyDoc clonedVacancyDoc = convertToVacancyDoc(vacancy, false);
                VacancyDoc oldVacancyDoc = vacancyDocService.findById(oldVacancyId);
                if (CollectionUtils.isNotEmpty(oldVacancyDoc.getClosedCandidates())) {
                    List<Long> closedCandidateIdsOnClonedVacancy = oldVacancyDoc.getClosedCandidates().stream()
                            .map(UserProfileCvEmbedded::getUserProfileCvId)
                            .collect(Collectors.toList());
                    clonedVacancyDoc.setClosedCandidateIdsOnClonedVacancy(closedCandidateIdsOnClonedVacancy);
                }
                VacancyDoc result = vacancyDocService.save(clonedVacancyDoc);
                updateVacancyEmbedded(result);
                logger.info(StringUtil.append("Create/Update cloned vacancy Id =", result.getId().toString()));
            }

        } else if (message instanceof List) {
            List<Vacancy> vacancies;
            if (CollectionUtils.isNotEmpty((List) message) && ((List) message).get(0) instanceof Number) {
                List<Number> idsBigDecimal = (List<Number>) message;
                List<Long> ids = idsBigDecimal.stream().map(Number::longValue).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(ids)) {
                    List<Long> idGroup = ids.stream().limit(10).collect(Collectors.toList());
                    ids.removeAll(idGroup);
                    logger.info(StringUtil.append("Updating vacancies Ids =", idGroup.toString()));
                    vacancies = vacancyService.findValidByIds(idGroup);
                    saveVacanciesToMongo(vacancies);
                    getSelf().tell(ids, getSelf());
                }
            } else if (CollectionUtils.isNotEmpty((List) message) && ((List) message).get(0) instanceof Vacancy) {
                vacancies = (List<Vacancy>) message;
                saveVacanciesToMongo(vacancies);
            } else if (CollectionUtils.isNotEmpty((List) message) && ((List) message).get(0) instanceof SaveAppointmentInMongo) {
                saveAppointmentInMongo((List<SaveAppointmentInMongo>) message);
            }
        } else if (message instanceof DeleteAppointmentInMongo) {
            deleteAppointmentInMongo((DeleteAppointmentInMongo) message);
        } else if (message instanceof SaveAppointmentInMongo) {
            saveAppointmentInMongo((SaveAppointmentInMongo) message);
        } else if (message instanceof EditVacancyInMongo) {
            updateVacancyInMongo((EditVacancyInMongo) message);
        }else if(message instanceof SaveContactPersonInMongo){
            saveContactPersonInMongo((SaveContactPersonInMongo) message);
        }
    }

    private void saveContactPersonInMongo(SaveContactPersonInMongo contactPersonInMongo) {
        vacancyDocService.updateContactPerson(contactPersonInMongo.getVacancyIds(), contactPersonInMongo.getContact());
    }
    private void deleteAppointmentInMongo(DeleteAppointmentInMongo message) {
        List<Long> appointmentIds = message.getAppointmentIds();
        appointmentEmbeddedService.delete(appointmentIds);
        logger.info(StringUtil.append("Updating appointment Ids =", message.getAppointmentIds().toString()));
    }

    private void updateVacancyInMongo(EditVacancyInMongo editVacancyInMongo) {
        if (Objects.nonNull(editVacancyInMongo.getVacancy())) {
            VacancyDoc vacancyDoc = vacancyDocService.findById(editVacancyInMongo.getVacancy().getId());
            if (Objects.nonNull(vacancyDoc)) {
                vacancyDoc.setSearchRange(editVacancyInMongo.getVacancy().getSearchRange());
                vacancyDoc.setNumberOfSeat(editVacancyInMongo.getVacancy().getNumberOfSeat());
                vacancyDoc.setSalary(editVacancyInMongo.getVacancy().getSalary());
                vacancyDoc.setSalaryMax(editVacancyInMongo.getVacancy().getSalaryMax());
                vacancyDoc.setCurrency(MongoConverters.convertToCurrencyEmbedded(editVacancyInMongo.getVacancy().getCurrency()));
                vacancyDoc.setStatus(editVacancyInMongo.getVacancy().getStatus());
                vacancyDoc.setSuspendDays(editVacancyInMongo.getVacancy().getSuspendDays());
                Date startSuspendDate = editVacancyInMongo.getVacancy().getStartSuspendDate();
                Date endSuspendDate = null;
                if (Objects.nonNull(editVacancyInMongo.getVacancy().getStartSuspendDate())) {
                    startSuspendDate = DateUtils.toServerTimeForMongo(startSuspendDate);
                     if (Objects.nonNull(editVacancyInMongo.getVacancy().getSuspendDays())) {
                         endSuspendDate = DateUtils.addDays(startSuspendDate, editVacancyInMongo.getVacancy().getSuspendDays());
                     }
                }
                vacancyDoc.setStartSuspendDate(startSuspendDate);
                vacancyDoc.setEndSuspendDate(endSuspendDate);
                List<VacancyAssessmentLevel> vacancyAssessmentLevels = editVacancyInMongo.getVacancy().getVacancyAssessmentLevels();
                if (Objects.nonNull(editVacancyInMongo.getVacancy().getVacancyAssessmentLevels())) {
                    vacancyDoc.setQualifications(vacancyAssessmentLevels.stream()
                            .map(al -> MongoConverters.convertToQualificationEmbedded(al.getAssessmentLevel()))
                            .collect(Collectors.toList()));

                } else {
                    vacancyDoc.setQualifications(null);
                }
                VacancyDoc result = vacancyDocService.save(vacancyDoc);
                updateVacancyEmbedded(vacancyDoc);
                // Cancel Appointment
                if (Objects.nonNull(getSender())
                        && !getSender().equals(ActorRef.noSender())
                        && Objects.nonNull(editVacancyInMongo.getCancelReason())) {
                    if (Const.Vacancy.CancelAppointmentReason.SUSPEND == editVacancyInMongo.getCancelReason()
                            || Const.Vacancy.CancelAppointmentReason.RECRUITED == editVacancyInMongo.getCancelReason()
                            || Const.Vacancy.CancelAppointmentReason.INACTIVE == editVacancyInMongo.getCancelReason()) {
                        CancelAppointmentDetailInMongo cancelAppointmentDetailInMongo = editVacancyInMongo.getCancelAppointment();
                        cancelAppointmentDetailInMongo.setVacancyDoc(result);
                        VacancyCandidateSDO vacancyCandidateSDO = editVacancyInMongo.getVacancyCandidate();
                        if (Objects.nonNull(vacancyCandidateSDO)) {
                            vacancyCandidateSDO.setVacancyDoc(result);
                            cancelAppointmentDetailInMongo.setVacancyCandidate(vacancyCandidateSDO);
                        }
                        getSender().tell(cancelAppointmentDetailInMongo, ActorRef.noSender());
                    }
                }
                logger.info(StringUtil.append("Update VancacyDoc Id =", String.valueOf(result.getId())));
            }
        }
    }


    private void saveAppointmentInMongo(List<SaveAppointmentInMongo> appointments) {
        if(CollectionUtils.isNotEmpty(appointments)){
            appointments.forEach(this::saveAppointmentInMongo);
        }
    }
    private void saveAppointmentInMongo(SaveAppointmentInMongo appointment) {
        AppointmentEmbedded appointmentEmbedded = MongoConverters.convertToAppointmentEmbedded(appointment);
        appointmentEmbeddedService.update(appointment.getVacancyId(), appointmentEmbedded);
        logger.info(StringUtil.append("Save appointment Id =", appointment.getAppointment().getId().toString()));
    }

    private void saveVacanciesToMongo(List<Vacancy> vacancies) {
        if (CollectionUtils.isNotEmpty(vacancies)) {
            List<VacancyDoc> vacancyDocs = new ArrayList<>();
            vacancies.forEach(v -> vacancyDocs.add(convertToVacancyDoc(v, true)));
            vacancyDocService.updateOrInsertVacancyDoc(vacancyDocs);
        }
    }

    private VacancyDoc convertToVacancyDoc(Vacancy vacancy, boolean isUpdateLazy) {
        if (isUpdateLazy) {
            vacancy = vacancyActorService.updateLazyValue(vacancy);
        }
        List<UserCvDoc> userCvDocs = ofNullable(vacancy.getClosedCandidates())
                .filter(it -> !it.isEmpty())
                .map(it -> userCvDocService.findAllById(it.stream().map(obj -> obj.getCandidate().getCurriculumVitaeId()).collect(Collectors.toList())))
                .orElseGet(ImmutableList::of);
        VacancyDoc vacancyDoc = MongoConverters.convertToVacancyDoc(vacancy, userCvDocs);

        if (Objects.nonNull(vacancy.getCreatedBy())) {
            List<Staff> createdBy = staffService.findByUserProfileAndCompanyApproval(vacancy.getCreatedBy(), vacancy.getCompany().getCompanyId());
            Staff staff = staffActorService.updateLazyValue(createdBy.get(0));
            vacancyDoc.setCreatedByStaff(MongoConverters.convertToStaffEmbedded(staff));
        }
        return vacancyDoc;
    }

    private void updateVacancyEmbedded(VacancyDoc vacancyDoc) {
        VacancyEmbedded vacancyEmbedded = MongoConverters.convertToVacancyEmbedded(vacancyDoc);
        vacancyEmbeddedService.update(vacancyEmbedded);
    }

}