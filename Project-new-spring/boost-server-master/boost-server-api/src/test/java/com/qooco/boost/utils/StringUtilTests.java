package com.qooco.boost.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/25/2018 - 1:58 PM
*/
@RunWith(PowerMockRunner.class)
public class StringUtilTests {
    @Test
    public void append_whenNoneEmptyString_thenReturnEmpty() {
        Assert.assertEquals(StringUtil.append("",""), "");
    }

    @Test
    public void append_whenRightString_thenReturnRightResult() {
        Assert.assertEquals("abc123def", StringUtil.append("abc", "123", "def"));
    }

    @Test
    public void convertToList_whenInputRightJson_thenReturnList() {
        List<String> temps = new ArrayList<>();
        temps.add("abc");
        temps.add("edf");
        Assert.assertEquals(temps, StringUtil.convertToList("[\"abc\",\"edf\"]"));
    }

    @Test
    public void convertToList_whenInputWrongJson_thenReturnEmpty() {
        Assert.assertEquals(null, StringUtil.convertToList(""));
    }

    @Test
    public void convertToJson_whenInputRightList_thenReturnJsonString() {
        List<String> temps = new ArrayList<>();
        temps.add("abc");
        temps.add("edf");
        Assert.assertEquals("[\"abc\",\"edf\"]", StringUtil.convertToJson(temps));
    }

    @Test
    public void convertToJson_whenInputWrongList_thenReturnEmpty() {
        Assert.assertEquals(null, StringUtil.convertToJson(null));
    }
}
