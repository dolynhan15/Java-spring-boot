package com.qooco.boost.threads;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.threads.actors.DataFeedbackActor;
import com.qooco.boost.threads.actors.DeleteFileActor;
import com.qooco.boost.threads.actors.SaveSystemLoggerInMongoActor;
import com.qooco.boost.threads.actors.syncs.AssessmentActor;
import com.qooco.boost.threads.actors.syncs.LocalizationActor;
import com.qooco.boost.threads.actors.syncs.SyncCurrencyRatesActor;
import com.qooco.boost.threads.models.DataFeedback;
import com.qooco.boost.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Component
public class SetupScheduleActorManager {
    protected Logger logger = LogManager.getLogger(SetupScheduleActorManager.class);
    private static final long HOURS_OF_DAY = 24;
    private static final long HOURS_OF_THREE_DAYS = 72;
    @Autowired
    private ActorSystem system;
    @Value(ApplicationConstant.BOOST_SYSTEM_LOGGER_TIME_LIVE)
    private int timeLive;

    @Value(ApplicationConstant.BOOST_MESSAGE_CONFIG_FILE_LIVE_TIME)
    private Integer numberLiveDayOfMessageFile;

    @Value(ApplicationConstant.BOOST_SCHEDULE_INTERVAL_MINUTE)
    private int intervalMinute = 0;

    @Value(ApplicationConstant.OPEN_EXCHANGE_RATE_SYNC_TIME)
    private int syncCurrencyHour = 168;

    private Cancellable cancellable;
    private Cancellable cancellableCleanSystemLogger;
    private Cancellable cancellableDeleteFile;
    private Cancellable cancellableDataFeedback;
    private Cancellable cancellableDataFeedbackThreeDays;
    private Cancellable cancellableSyncCurrencyRates;

    public void startSchedule() {
        setScheduleSyncLevelTests();
        setScheduleAtMidNight();
    }

    private void setScheduleAtMidNight(){
        Date midNight = DateUtils.atEndOfDate(new Date());
        long remainTime = midNight.getTime() - new Date().getTime();
        FiniteDuration delayTime = remainTime > 0 ? Duration.create(remainTime, TimeUnit.MILLISECONDS) : Duration.Zero();

        cancellableCleanSystemLogger = system.scheduler().schedule(delayTime, Duration.create(HOURS_OF_DAY, TimeUnit.HOURS),
                this::doCleanSystemLogger, system.dispatcher());
        cancellableDeleteFile = system.scheduler().schedule(delayTime, Duration.create(HOURS_OF_DAY, TimeUnit.HOURS),
                this::doDeleteFile, system.dispatcher());
        cancellableDataFeedback = system.scheduler().schedule(delayTime, Duration.create(HOURS_OF_DAY, TimeUnit.HOURS),
                this::doDataFeedback, system.dispatcher());

        cancellableDataFeedbackThreeDays = system.scheduler().schedule(delayTime, Duration.create(HOURS_OF_THREE_DAYS, TimeUnit.HOURS),
                this::doDataFeedbackThreeDays, system.dispatcher());

        cancellableSyncCurrencyRates = system.scheduler().schedule(delayTime, Duration.create(syncCurrencyHour, TimeUnit.HOURS),
                this::syncCurrencyRates, system.dispatcher());

    }

    public void stopSchedule() {
        Stream.of(cancellable, cancellableCleanSystemLogger, cancellableDeleteFile,
                cancellableDataFeedback, cancellableDataFeedbackThreeDays, cancellableSyncCurrencyRates)
                .filter(Objects::nonNull).forEach(Cancellable::cancel);
    }

    private void setScheduleSyncLevelTests() {
        cancellable = system.scheduler()
                .schedule(Duration.Zero(), Duration.create(intervalMinute, TimeUnit.MINUTES),
                        () -> syncLocalization(QoocoApiConstants.SYNC_LEVEL_TESTS), system.dispatcher());
    }

    private void doCleanSystemLogger() {
        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(SaveSystemLoggerInMongoActor.ACTOR_NAME));
        Date deleteDate = DateUtils.addDays(new Date(), -timeLive);
        updater.tell(deleteDate, ActorRef.noSender());
    }

    private void doDeleteFile() {
        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(DeleteFileActor.ACTOR_NAME));

        Date expiredDate = DateUtils.addDays(new Date(), - numberLiveDayOfMessageFile);
        updater.tell(expiredDate, ActorRef.noSender());
    }

    private void syncLocalization(String collection) {
        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(LocalizationActor.ACTOR_NAME));
        ActorRef assessmentActor = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(AssessmentActor.ACTOR_NAME));
        updater.tell(collection, assessmentActor);
    }

    private void doDataFeedback() {
        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(DataFeedbackActor.ACTOR_NAME));
        updater.tell(DataFeedback.SCHEDULE_DATA_FEEDBACK_PROFILE, ActorRef.noSender());
        updater.tell(DataFeedback.SCHEDULE_DATA_FEEDBACK_SELECT, ActorRef.noSender());
    }

    private void doDataFeedbackThreeDays() {
        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(DataFeedbackActor.ACTOR_NAME));
        updater.tell(DataFeedback.SCHEDULE_THREE_DAYS_DATA_FEEDBACK_SELECT, ActorRef.noSender());
    }

    private void syncCurrencyRates() {
        ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(SyncCurrencyRatesActor.ACTOR_NAME));
        updater.tell(SyncCurrencyRatesActor.SYNC_CURRENCY_RATE, ActorRef.noSender());
    }

}
