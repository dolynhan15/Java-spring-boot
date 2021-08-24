package com.qooco.boost.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.qooco.boost.business.QoocoService;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.models.qooco.*;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.Jackson2ResourceReader;
import org.springframework.data.repository.init.ResourceReaderRepositoryPopulator;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static com.qooco.boost.HibernateInterceptor.unsetTransactionCompleted;
import static com.qooco.boost.business.impl.QoocoServiceImpl.Fields.qoocoApiPath;
import static com.qooco.boost.constants.QoocoApiConstants.LOGIN_PASSWORD_ERROR;
import static com.qooco.boost.constants.QoocoApiConstants.LOGIN_USERNAME_ERROR;
import static com.qooco.boost.constants.StatusConstants.*;
import static com.qooco.boost.constants.Api.*;
import static com.qooco.boost.constants.URLConstants.BOOST_API;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.ThreadLocal.withInitial;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Clock.systemDefaultZone;
import static java.util.Arrays.stream;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.mock.web.MockHttpServletRequest.DEFAULT_SCHEME;
import static org.springframework.mock.web.MockHttpServletRequest.DEFAULT_SERVER_NAME;

@RestController
@RequestMapping(BOOST_API)
class MockQooController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Gson gson;

    @PostMapping(SERVICE_LOGIN_SYSTEM_ACCOUNT)
    public ResponseEntity<String> oauthToken(@RequestParam String report) throws Exception {
        var login = objectMapper.readValue(report, LoginRequest.class);
        var result = LoginResponse.builder().username(login.getUsername());
        if ("phuc1234|phuc1234".equals(login.getUsername() + "|" + login.getPassword())) {
            result.errorCode(0);
        } else if (!"abc".equals(login.getUsername())) {
            result.errorCode(LOGIN_USERNAME_ERROR);
        } else if (!"123".equals(login.getPassword())) {
            result.errorCode(LOGIN_PASSWORD_ERROR);
        }
        return ok(gson.toJson(result.build()));
    }

    @GetMapping("/retrievePassword")
    public ResponseEntity<String> retrievePassword() {
        return ok(gson.toJson(QoocoForgotPasswordResp.builder().errorCode(FORGOT_PASSWORD_NOT_FOUND_CODE).build()));
    }

    @PostMapping(SERVICE_VERIFY_SIGN_UP_CODE)
    public ResponseEntity<String> verifySignUpCode(@RequestParam String report) throws Exception {
        var req = objectMapper.readValue(report, VerificationCode.class);
        var result = QoocoResponseBase.builder();
        if ("trungmhv@gmail.com".equalsIgnoreCase(req.getContact()) && "124322".equalsIgnoreCase(req.getCode())) {
            result.errorCode(QoocoApiConstants.VERIFICATION_CODE_EXPIRED);
        }
        return ok(gson.toJson(result.build()));
    }

    @GetMapping("/check-user-contact")
    public ResponseEntity<String> checkUserContact() {
        return ok(gson.toJson(QoocoResponseBase.builder().build()));
    }

    @PostMapping(SERVICE_GENERATE_SIGN_UP_CODE)
    public ResponseEntity<String> generateSignupCode() {
        return ok(gson.toJson(QoocoResponseBase.builder().build()));
    }

    @PostMapping(SERVICE_REGISTER_SYSTEM_ACCOUNT)
    public ResponseEntity<String> registerSystemAccount() {
        return ok(gson.toJson(QoocoResponseBase.builder().build()));
    }
}

class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    public void beforeTestExecution(ExtensionContext ctx) {
        ctx.getStore(Namespace.create(getClass())).put(ctx.getRequiredTestMethod(), currentTimeMillis());
    }

    public void afterTestExecution(ExtensionContext ctx) {
        System.out.println(format("%s took %sms", ctx.getRequiredTestMethod().getName(), currentTimeMillis() - ctx.getStore(Namespace.create(getClass())).remove(ctx.getRequiredTestMethod(), long.class)));
    }
}

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(classes=BaseMvcTest.Configuration.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(TimingExtension.class)
public abstract class BaseMvcTest {
    @Component
    public static class MutableClock extends Clock {
        @Getter
        private ThreadLocal<Clock> threadLocalClock = withInitial(Clock::systemDefaultZone);

        public ZoneId getZone() {
            return threadLocalClock.get().getZone();
        }

        public Clock withZone(ZoneId zoneId) {
            return threadLocalClock.get().withZone(zoneId);
        }

        public Instant instant() {
            return threadLocalClock.get().instant();
        }
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {
            var factory = new Jackson2RepositoryPopulatorFactoryBean();
            factory.setResources(new Resource[] { new ClassPathResource("mongo/LevelTestScaleDoc.json") });
            return factory;
        }
    }

    static final String TOKEN_NORMAL_USER = "bfa611e0ecd443b487f1e392f46a67ba206acdf6b294d9223b6ec7b3df36ef60";
    static final String ROOT = "$.";
    static final String INDEX0 = "[0].";
    static final String INSERT_VACANCY = "INSERT INTO VACANCY (VACANCY_ID,LOGO,COMPANY_ID,JOB_ID,CITY_ID,SEARCH_CITY_ID,CONTACT_PERSON_ID,EDUCATION_ID,SALARY,IS_HOUR_SALARY,WORKING_TYPE,IS_ASAP,EXPECTED_START_DATE,SHORT_DESCRIPTION,ASSESSMENT_LEVEL,STATUS,CURRENCY_ID,IS_DELETED,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,NUMBER_OF_SEAT,FULL_DESCRIPTION,SEARCH_RANGE,SALARY_MAX,JOB_LOCATION_ID,SEARCH_LOCATION_ID,START_SUSPEND_DATE,SUSPEND_DAYS,ARCHIVIST_ID) VALUES";
    static final String INSERT_VACANCY_CANDIDATE = "INSERT INTO VACANCY_CANDIDATE (ID,VACANCY_ID,CANDIDATE_STATUS,IS_DELETED,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,CANDIDATE_ID,ARCHIVIST_ID) VALUES";
    static final String INSERT_APPOINTMENT_DETAIL = "INSERT INTO APPOINTMENT_DETAIL (APPOINTMENT_DETAIL_ID,APPOINTMENT_ID,CURRICULUM_VITAE_ID,APPOINTMENT_TIME,IS_DELETED,CREATED_DATE,CREATED_BY,UPDATED_DATE,UPDATED_BY,STATUS) VALUES";
    static final String INSERT_APPOINTMENT = "INSERT INTO APPOINTMENT (APPOINTMENT_ID,LOCATION_ID,APPOINTMENT_DATE,STAFF_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED,VACANCY_ID,\"TYPE\",FROM_DATE) VALUES";
    protected static final String INSERT_USER_ATTRIBUTE_EVENT = "Insert into USER_ATTRIBUTE_EVENT (ID,USER_PROFILE_ID,PROFILE_ATTRIBUTE_ID,EVENT_CODE,COUNT,IS_DELETED,CREATED_DATE,UPDATED_DATE,CREATED_BY,UPDATED_BY) values ";
    protected static final String INSERT_USER_ATTRIBUTE = "Insert into USER_ATTRIBUTE (ID,USER_PROFILE_ID,PROFILE_ATTRIBUTE_ID,SCORE,\"LEVEL\",IS_NEW_LEVEL,IS_DELETED,CREATED_DATE,UPDATED_DATE,CREATED_BY,UPDATED_BY) values ";
    static final String INSERT_USER_QUALIFICATION = "Insert into USER_QUALIFICATION (QUALIFICATION_ID,NAME,LEVEL_NAME,EXPIRED_TIME,CREATED_DATE,UPDATED_DATE,CREATED_BY,UPDATED_BY,IS_DELETED,USER_PROFILE_ID,SCALE_ID,ASSESSMENT_ID,LEVEL_VALUE,SUBMISSION_TIME) values ";
    static final String INSERT_USER_CURRICULUM_VITAE = "INSERT INTO USER_CURRICULUM_VITAE (CURRICULUM_VITAE_ID,IS_HOUR_SALARY,MIN_SALARY,MAX_SALARY,EDUCATION_ID,IS_ASAP,EXPECTED_START_DATE,IS_FULL_TIME,USER_PROFILE_ID,CURRENCY_ID,SOCIAL_LINKS,IS_DELETED,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,HAS_PERSONALITY) VALUES ";
    static final String INSERT_LOCATION = "INSERT INTO LOCATION (LOCATION_ID,CITY_ID,IS_DELETED,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,COMPANY_ID,ADDRESS,IS_USED,IS_PRIMARY) VALUES ";
    static final String INSERT_JOB = "INSERT INTO JOB (JOB_ID, JOB_NAME, JOB_DESCRIPTION, CREATED_BY, CREATED_DATE, UPDATED_BY, UPDATED_DATE, IS_DELETED, COMPANY_ID) VALUES ";
    static final String INSERT_STAFF = "Insert into STAFF (STAFF_ID,USER_PROFILE_ID,COMPANY_ID,ROLE_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED) values ";
    static final String INSERT_APP_VERSION = "Insert into APP_VERSION (APP_VERSION_ID,APP_ID,OS,IS_FORCE_UPDATE,CREATED_DATE,UPDATED_DATE,APP_VERSION,APP_VERSION_NAME) values ";
    static final String INSERT_LANGUAGE = "Insert into LANGUAGE (LANGUAGE_ID,NAME,CODE,IS_DELETED,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE) values ";
    static final String INSERT_COMPANY = "Insert into COMPANY (COMPANY_ID,COMPANY_NAME,LOGO,CITY_ID,ADDRESS,PHONE,EMAIL,WEB,AMADEUS,GALILEO,WORLDSPAN,SABRE,HOTEL_TYPE_ID,DESCRIPTION,STATUS,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED) values ";
    protected static final String INSERT_PROFILE_ATTRIBUTE = "Insert into PROFILE_ATTRIBUTE (ID,NAME,DESCRIPTION,IS_DELETED,CREATED_DATE,UPDATED_DATE,CREATED_BY,UPDATED_BY) values ";
    protected static final String INSERT_ATTRIBUTE_EVENT = "Insert into ATTRIBUTE_EVENT (ID,PROFILE_ATTRIBUTE_ID,EVENT_CODE,SCORE,IS_REPEATABLE,IS_DELETED,CREATED_DATE,UPDATED_DATE,CREATED_BY,UPDATED_BY) values ";
    static final String INSERT_ASSESSMENT = "Insert into ASSESSMENT (ASSESSMENT_ID,NAME,PRICE,PICTURE,NUMBER_COMPANY_REQUIRE,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED,SCALE_ID,MAPPING_ID,TYPE,PACKAGE_ID,TOPIC_ID,CATEGORY_ID,TIME_LIMIT) values ";
    static final String INSERT_ASSESSMENT_LEVEL = "Insert into ASSESSMENT_LEVEL (ASSESSMENT_LEVEL_ID,ASSESSMENT_LEVEL_NAME,ASSESSMENT_LEVEL_VALUE,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED,SCALE_ID,MAPPING_ID,ASSESSMENT_LEVEL_DESC,ASSESSMENT_ID,LEVEL_VALUE) values ";
    static final String INSERT_CURRENCY = "Insert into CURRENCY (CURRENCY_ID,CODE,NAME,UNIT_PER_USD,USD_PER_UNIT,MIN_SALARY,MAX_SALARY,SYMBOL) values ";
    static final String INSERT_SOFT_SKILL = "Insert into SOFT_SKILL (SOFT_SKILL_ID,NAME,DESCRIPTION,IS_DELETED,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE) values ";
    static final String INSERT_EDUCATION = "Insert into EDUCATION (EDUCATION_ID,NAME,DESCRIPTION) values ";
    static final String INSERT_WORKING_HOUR = "Insert into WORKING_HOUR (WORKING_HOUR_ID,WORKING_HOUR_DESCRIPTION,START_TIME,END_TIME,WORKING_TYPE) values ";
    static final String INSERT_ROLE_COMPANY = "Insert into ROLE_COMPANY (ROLE_ID,NAME,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED,DISPLAY_NAME) values ";
    static final String INSERT_HOTEL_TYPE = "Insert into HOTEL_TYPE (HOTEL_TYPE_ID,HOTEL_TYPE_NAME) values ";
    static final String INSERT_USER_ACCESS_TOKEN = "Insert into USER_ACCESS_TOKEN (USER_PROFILE_ID,TOKEN,REFRESH_TOKEN,TYPE,ACCESS_TOKEN_ID,CHANNEL_ID,CREATED_DATE,IS_DELETED,CREATED_BY,UPDATED_DATE,UPDATED_BY,SIGN_IN_ID,SESSION_ID,DEVICE_TYPE,PUBLIC_KEY,COMPANY_ID) values ";
    protected static final String INSERT_USER_PROFILE = "Insert into USER_PROFILE (USER_PROFILE_ID,USERNAME,FIRST_NAME,LAST_NAME,GENDER,AVATAR,BIRTHDAY,PHONE_NUMBER,ADDRESS,COUNTRY_ID,NATIONAL_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED,EMAIL,IS_ADMIN,DEFAULT_COMPANY,CITY_ID,PROFILE_STEP,FIT_FIRST_NAME,FIT_LAST_NAME,FIT_AVATAR,FIT_GENDER,FIT_PHONE_NUMBER,FIT_BIRTHDAY,FIT_NATIONAL_ID,FIT_ADDRESS,FIT_PROFILE_STEP) values ";
    protected static final String INSERT_CITY = "Insert into CITY (CITY_ID,PROVINCE_ID,CITY_NAME,LATITUDE,LONGITUDE,IS_DELETED) values ";
    protected static final String INSERT_PROVINCE = "Insert into PROVINCE (PROVINCE_ID,COUNTRY_ID,NAME,TYPE,CODE,IS_DELETED) values ";
    protected static final String INSERT_COUNTRY = "Insert into COUNTRY (COUNTRY_ID,COUNTRY_NAME,COUNTRY_CODE,PHONE_CODE,IS_DELETED) values ";
    static final String INSERT_CLIENT_INFO = "INSERT INTO CLIENT_INFO (TOKEN, APP_ID, APP_VERSION, DEVICE_MODEL, PLATFORM, OS_VERSION, DEVICE_TOKEN, USER_PROFILE_ID, LOGIN_TIME, LOGOUT_TIME, CHANNEL_ID) VALUES ";
    static final String INSERT_BENEFIT = "Insert into BENEFIT (BENEFIT_ID,DESCRIPTION,NAME,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED) values ";

    @Value(DEFAULT_SCHEME + "://" + DEFAULT_SERVER_NAME + ":${local.server.port}" + BOOST_API)
    private String mockControllerPath;

    @Value("classpath:schema.sql")
    protected Resource[] schemaSqls;

    @Autowired
    protected DataSource dataSource;

    @javax.annotation.Resource
    protected MutableClock systemClock;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @Autowired
    MongoTemplate mongoTemplate;

    DateFormat iso8601DateFormat;

    private Repositories repositories;

    private Jackson2ResourceReader jackson2ResourceReader;

    @Autowired
    QoocoService qoocoService;

    @Autowired
    public void setObjectMapper(ObjectMapper value) {
        iso8601DateFormat = value.getDateFormat();
        jackson2ResourceReader = new Jackson2ResourceReader(value);
    }

    @Autowired
    public void setWebApplicationContext(WebApplicationContext value) {
        repositories = new Repositories(value);
    }

    public static Date SYS_EXTRACT_UTC(Date date) {
        return date;
    }

    public static String TO_CLOB(String value) {
        return value;
    }

    @BeforeEach
    public final void setup() {
        systemClock.getThreadLocalClock().set(systemDefaultZone());
        ReflectionTestUtils.setField(qoocoService, qoocoApiPath, mockControllerPath);
    }

    @AfterEach
    public final void after() {
        if (unsetTransactionCompleted()) new ResourceDatabasePopulator(schemaSqls).execute(dataSource);
        unsetTransactionCompleted();
        Stream.of(
                AssessmentTestHistoryDoc.class,
                SupportConversationDoc.class
        ).forEach(mongoTemplate::dropCollection);
    }

    void loadMongoDataJson(String... json) {
        var populator = new ResourceReaderRepositoryPopulator(jackson2ResourceReader);
        populator.setResources(stream(json).map(it -> new ByteArrayResource(it.getBytes(UTF_8))).toArray(Resource[]::new));
        populator.populate(repositories);
    }

    public static int IS_SUSPEND_RANGE(int VACANCY_STATUS, Date START_DATE, Integer SUSPEND_DAYS, Date NOW_TIME) {
        var TEMP_STATUS = 1;
        if (VACANCY_STATUS == 1 && START_DATE != null && SUSPEND_DAYS > 0) {
            var calendar = Calendar.getInstance();
            calendar.setTime(START_DATE);
            calendar.add(Calendar.DATE, SUSPEND_DAYS);
            if (NOW_TIME.getTime() > calendar.getTimeInMillis()) TEMP_STATUS = 1;
            else TEMP_STATUS = 2;
        } else if (VACANCY_STATUS == 2) TEMP_STATUS = 2;
        else if (VACANCY_STATUS == 3) TEMP_STATUS = 3;
        return TEMP_STATUS;
    }

    public static String GET_DISPLAY_NAME(String FIRST_NAME, String LAST_NAME, String USERNAME) {
        return (FIRST_NAME == null && LAST_NAME == null ? USERNAME : FIRST_NAME + LAST_NAME).toUpperCase();
    }
}
