package com.qooco.boost.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListUtil {

    public static int[] removeDuplicatesIntArray(int[] arr) {
        if (ArrayUtils.isEmpty(arr)) {
            return new int[]{};
        }
        return Arrays.stream(arr).distinct().toArray();
    }

    public static Integer[] removeDuplicatesIntegerArray(Integer[] objects) {
        if (ArrayUtils.isEmpty(objects)) {
            return new Integer[]{};
        }
        return Arrays.stream(objects).distinct().toArray(Integer[]::new);
    }

    public static Long[] removeDuplicatesLongArray(Long[] objects) {
        if (ArrayUtils.isEmpty(objects)) {
            return new Long[]{};
        }
        return Arrays.stream(objects).distinct().toArray(Long[]::new);
    }

    public static long[] removeDuplicatesLongArray(long[] arr) {
        if (ArrayUtils.isEmpty(arr)) {
            return new long[]{};
        }
        return Arrays.stream(arr).distinct().toArray();
    }

    public static long[] removeDuplicatesLongArray(long[] primary, long[] second) {
        if (ArrayUtils.isEmpty(second)) {
            return new long[]{};
        }

        if (ArrayUtils.isEmpty(primary)) {
            return second;
        }
        List<Long> _seconds = Arrays.stream(second).boxed().collect(Collectors.toList());
        Arrays.stream(primary).forEach(_seconds::remove);
        return _seconds.stream().mapToLong(l -> l).toArray();
    }

    public static long[] convertListToArray(List<Long> items) {
        if (CollectionUtils.isNotEmpty(items)) {
            return items.stream().mapToLong(l -> l).toArray();
        }
        return ArrayUtils.EMPTY_LONG_ARRAY;
    }

    public static Long[] convertBigDecimalToLong(Object[] value){
        if(Objects.nonNull(value)){
            return Arrays.stream(value).map(it -> ((BigDecimal)it).longValue()).toArray(Long[]::new);
        }
        return null;
    }
}
