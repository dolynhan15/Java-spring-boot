package com.qooco.boost.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;


/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/10/2018 - 6:36 PM
*/
@RunWith(PowerMockRunner.class)
@SpringBootTest
public class ListUtilTest {

    @Test
    public void removeDuplicatesInArray_whenArrayIsNull_thenReturnNull() {
        Assert.assertNotNull(ListUtil.removeDuplicatesIntArray(new int[] {}));
    }

    @Test
    public void removeDuplicatesInArray_whenArrayIsEmpty_thenReturnNull() {
        Assert.assertNotNull(ListUtil.removeDuplicatesIntArray(new int[] {}));
    }

    @Test
    public void removeDuplicatesInList_whenListIsEmpty_thenReturnEmpty() {
        Assert.assertNotNull(ListUtil.removeDuplicatesIntegerArray(null));
    }

    @Test
    public void removeDuplicatesInList_whenListIs2Duplicates_thenReturnRightResult() {
        Assert.assertEquals(5, ListUtil.removeDuplicatesIntegerArray(new Integer[] {1,2,3,4,3,2,5}).length);
    }
}