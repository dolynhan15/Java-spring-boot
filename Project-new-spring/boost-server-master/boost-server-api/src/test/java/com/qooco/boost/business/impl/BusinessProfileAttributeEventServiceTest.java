package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.controllers.BaseMvcTest;
import com.qooco.boost.data.oracle.reposistories.UserAttributeEventRepository;
import com.qooco.boost.data.oracle.reposistories.UserAttributeRepository;
import com.qooco.boost.enumeration.ProfileStep;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.mapWithIndex;
import static com.qooco.boost.constants.AttributeEventType.*;
import static java.time.Clock.offset;
import static java.time.Clock.systemDefaultZone;
import static java.time.Duration.ofDays;
import static java.time.Duration.ofHours;
import static java.util.function.Function.identity;
import static java.util.stream.LongStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.*;

class BusinessProfileAttributeEventServiceTest extends BaseMvcTest {
    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @Autowired
    private UserAttributeEventRepository userAttributeEventRepository;

    @Autowired
    private UserAttributeRepository userAttributeRepository;

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(1,1,1,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_SHARED_GITF_CODE_x_times_concurrently() throws Exception {
        for (var result : rangeClosed(1, 20).mapToObj(it -> businessProfileAttributeEventService.onAttributeEventAsync(EVT_SHARED_GITF_CODE, 1000526864L)).collect(toImmutableList())) result.get();
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 1L, EVT_SHARED_GITF_CODE);
        var userAttribute = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 1L);
        assertEquals(20, userAttributeEvent.getCount());
        assertEquals(500, userAttribute.getScore());
        assertEquals(3, userAttribute.getLevel());
        assertTrue(userAttribute.isNewLevel());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(1,1,1,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)",
            INSERT_USER_ATTRIBUTE_EVENT + "(1,1000526864,1,1,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void given_existing_do_SHARED_GITF_CODE_x_times_concurrently() throws Exception {
        for (var result : rangeClosed(1, 20).mapToObj(it -> businessProfileAttributeEventService.onAttributeEventAsync(EVT_SHARED_GITF_CODE, 1000526864L)).collect(toImmutableList())) result.get();
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 1L, EVT_SHARED_GITF_CODE);
        var userAttribute = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 1L);
        assertEquals(21, userAttributeEvent.getCount());
        assertEquals(800, userAttribute.getScore());
        assertEquals(3, userAttribute.getLevel());
        assertTrue(userAttribute.isNewLevel());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_3_days_in_a_row() {
        rangeClosed(1, 3).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var userAttribute = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        assertEquals(3, userAttributeEvent.getCount());
        assertEquals(25, userAttribute.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_6_days_in_a_row() {
        rangeClosed(1, 6).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var userAttribute = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        assertEquals(6, userAttributeEvent.getCount());
        assertEquals(50, userAttribute.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(46,'Resilience','Resilience attribute defines your ability to cope with and rise to the inevitable challenges, problems and set-backs you meet in the course of work',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(14,39,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(15,46,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_7_days_in_a_row() {
        rangeClosed(1, 7).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var uae3Days = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var uaEnergy = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        var uaResilience = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 46L);
        assertEquals(7, uae3Days.getCount());
        assertEquals(100, uaEnergy.getScore());
        assertEquals(50, uaResilience.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(46,'Resilience','Resilience attribute defines your ability to cope with and rise to the inevitable challenges, problems and set-backs you meet in the course of work',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(14,39,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(15,46,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(16,46,8,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_14_days_in_a_row() {
        rangeClosed(1, 14).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var uaEnergy = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        var uaResilience = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 46L);
        assertEquals(14, userAttributeEvent.getCount());
        assertEquals(200, uaEnergy.getScore());
        assertEquals(150, uaResilience.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                + ",(46,'Resilience','Resilience attribute defines your ability to cope with and rise to the inevitable challenges, problems and set-backs you meet in the course of work',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(14,39,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(15,46,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(16,46,8,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_15_days_in_a_row() {
        rangeClosed(1, 15).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var uaEnergy = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        var uaResilience = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 46L);
        assertEquals(15, userAttributeEvent.getCount());
        assertEquals(225, uaEnergy.getScore());
        assertEquals(150, uaResilience.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(46,'Resilience','Resilience attribute defines your ability to cope with and rise to the inevitable challenges, problems and set-backs you meet in the course of work',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(14,39,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(15,46,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(16,46,8,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_28_days_in_a_row() {
        rangeClosed(1, 28).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var uaEnergy = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        var uaResilience = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 46L);
        assertEquals(28, userAttributeEvent.getCount());
        assertEquals(425, uaEnergy.getScore());
        assertEquals(300, uaResilience.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(46,'Resilience','Resilience attribute defines your ability to cope with and rise to the inevitable challenges, problems and set-backs you meet in the course of work',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(14,39,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(15,46,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(16,46,8,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_29_days_in_a_row() {
        rangeClosed(1, 29).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var uaEnergy = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        var uaResilience = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 46L);
        assertEquals(29, userAttributeEvent.getCount());
        assertEquals(425, uaEnergy.getScore());
        assertEquals(300, uaResilience.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(39,'Energy','Energy attribute defines your ability to perform active actions and achieve personal goals',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(46,'Resilience','Resilience attribute defines your ability to cope with and rise to the inevitable challenges, problems and set-backs you meet in the course of work',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(13,39,6,25,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(14,39,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(15,46,7,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)" +
                    ",(16,46,8,50,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_ENTER_APPLICATION_3_days_in_a_row_skip1_7_days_in_a_row_skip1_14_days_in_a_row() {
        Stream.of(rangeClosed(1, 3), rangeClosed(5, 11), rangeClosed(13, 26)).flatMapToLong(identity()).peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it)))).forEach(it -> businessProfileAttributeEventService.onEnterAppEvent(1000526864L));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_ENTER_APP_EVERYDAY_FOR_3_DAYS);
        var uaEnergy = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 39L);
        var uaResilience = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 46L);
        assertEquals(14, userAttributeEvent.getCount());
        assertEquals(325, uaEnergy.getScore());
        assertEquals(200, uaResilience.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(43,'Enthusiasm','Enthusiasm attribute defines your ability to show a keen interest in a subject or an work activity, as well as a readiness to get involved',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(39,43,15,50,0,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_FILL_THE_PROFILE_IN_ONE_SESSION() {
        mapWithIndex(Stream.of(ProfileStep.values()).skip(1), SimpleImmutableEntry::new)
                .peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofHours(it.getValue()))))
                .forEach(it -> businessProfileAttributeEventService.onUserProfileStep(1000526864L, it.getKey()));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_FILL_THE_PROFILE_IN_ONE_SESSION);
        var userAttribute = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 43L);
        assertEquals(63, userAttributeEvent.getCount());
        assertEquals(50, userAttribute.getScore());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(43,'Enthusiasm','Enthusiasm attribute defines your ability to show a keen interest in a subject or an work activity, as well as a readiness to get involved',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(39,43,15,50,0,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_FILL_THE_PROFILE_IN_MORE_THAN_ONE_SESSION() {
        mapWithIndex(Stream.of(ProfileStep.values()).skip(1), SimpleImmutableEntry::new)
                .peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it.getValue()))))
                .forEach(it -> businessProfileAttributeEventService.onUserProfileStep(1000526864L, it.getKey()));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_FILL_THE_PROFILE_IN_ONE_SESSION);
        var userAttribute = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 43L);
        assertEquals(63, userAttributeEvent.getCount());
        assertNull(userAttribute);
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_PROFILE_ATTRIBUTE + "(43,'Enthusiasm','Enthusiasm attribute defines your ability to show a keen interest in a subject or an work activity, as well as a readiness to get involved',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_ATTRIBUTE_EVENT + "(39,43,15,50,0,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
    })
    void do_FILL_THE_PROFILE_IN_ONE_SESSION_TWICE() {
        mapWithIndex(Stream.of(ProfileStep.values()).skip(1), SimpleImmutableEntry::new)
                .peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofHours(it.getValue()))))
                .forEach(it -> businessProfileAttributeEventService.onUserProfileStep(1000526864L, it.getKey()));
        mapWithIndex(Stream.of(ProfileStep.values()).skip(1), SimpleImmutableEntry::new)
                .peek(it -> systemClock.getThreadLocalClock().set(offset(systemDefaultZone(), ofDays(it.getValue()))))
                .forEach(it -> businessProfileAttributeEventService.onUserProfileStep(1000526864L, it.getKey()));
        var userAttributeEvent = userAttributeEventRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndEventCodeAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, null, EVT_FILL_THE_PROFILE_IN_ONE_SESSION);
        var userAttribute = userAttributeRepository.findByUserProfile_UserProfileIdAndAttribute_IdAndIsDeletedFalseOrderByCreatedDateAscIdAsc(1000526864L, 43L);
        assertEquals(63, userAttributeEvent.getCount());
        assertEquals(50, userAttribute.getScore());
    }
}
