package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.threads.models.CancelAppointmentDetailInMongo;
import com.qooco.boost.threads.models.SaveAppointmentDetailInMongo;
import com.qooco.boost.threads.services.AppointmentDetailActorService;
import com.qooco.boost.threads.services.MessageDocActorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.ofNullable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveAppointmentDetailInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveAppointmentDetailInMongoActor.class);
    public static final String ACTOR_NAME = "saveAppointmentDetailInMongoActor";

    private AppointmentDetailActorService appointmentDetailActorService;
    private MessageDocActorService messageDocActorService;

    public SaveAppointmentDetailInMongoActor(AppointmentDetailActorService appointmentDetailActorService,
                                             MessageDocActorService messageDocActorService) {
        this.appointmentDetailActorService = appointmentDetailActorService;
        this.messageDocActorService = messageDocActorService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof SaveAppointmentDetailInMongo) {
            SaveAppointmentDetailInMongo saveAppointmentDetailInMongo = (SaveAppointmentDetailInMongo) message;
            if (CollectionUtils.isNotEmpty(saveAppointmentDetailInMongo.getEvents())) {
                appointmentDetailActorService.saveAppointmentDetail(saveAppointmentDetailInMongo.getAppointment(), saveAppointmentDetailInMongo.getEvents(), null);
            }
        } else if (message instanceof AppointmentDetail) {
            appointmentDetailActorService.updateAppointmentDetail((AppointmentDetail) message, null);
        } else if (message instanceof List && ((List) message).get(0) instanceof AppointmentDetail) {
            ((List) message).forEach(a -> appointmentDetailActorService.updateAppointmentDetailDoc(((AppointmentDetail) a), null));
        } else if (message instanceof CancelAppointmentDetailInMongo) {
            CancelAppointmentDetailInMongo appointmentSDO = (CancelAppointmentDetailInMongo) message;
            Map<Appointment, List<AppointmentDetail>> cancelEventMap = appointmentSDO.getCancelEventMap();
            if (MapUtils.isNotEmpty(cancelEventMap)) {
                cancelEventMap.forEach((appointment, appointmentDetails) -> appointmentDetailActorService.saveAppointmentDetail(appointment, appointmentDetails, null));

                if (Objects.nonNull(appointmentSDO.getTargetAppointment()) && CollectionUtils.isNotEmpty(appointmentSDO.getNewAppointmentDetails())) {
                    appointmentDetailActorService.saveAppointmentDetail(appointmentSDO.getTargetAppointment(), appointmentSDO.getNewAppointmentDetails(), null);
                }
            }

            switch (appointmentSDO.getReason()) {
                case Const.Vacancy.CancelAppointmentReason.SUSPEND:
                    messageDocActorService.saveMessageForSuspendedOrInactiveVacancy(appointmentSDO.getVacancyDoc(), appointmentSDO.getReason(), null);
                    break;
                case Const.Vacancy.CancelAppointmentReason.INACTIVE:
                    messageDocActorService.saveMessageForRecruitedVacancyCandidate(appointmentSDO.getVacancyCandidate());
                    messageDocActorService.saveMessageForSuspendedOrInactiveVacancy(appointmentSDO.getVacancyDoc(), appointmentSDO.getReason(), null);
                    break;
                case Const.Vacancy.CancelAppointmentReason.RECRUITED:
                    messageDocActorService.saveMessageForRecruitedVacancyCandidate(appointmentSDO.getVacancyCandidate());
                    break;
                case Const.Vacancy.CancelAppointmentReason.UNASSIGNED_ROLE:
                    ofNullable(cancelEventMap).filter(MapUtils::isNotEmpty)
                            .ifPresent(it -> messageDocActorService.saveChangedContactPersonMessage(it.values().stream().flatMap(List::stream).collect(toImmutableList())));

                    break;
                default:
            }
        }
    }
}
