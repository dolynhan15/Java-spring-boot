package com.qooco.boost.utils;

import java.util.Objects;

public class Converter {

    public static double valueOfDouble(Double value, double defaultValue){
        return  (Objects.nonNull(value))? value.doubleValue() : defaultValue;
    }

    public static int valueOfInteger(Integer value, int defaultValue){
        return  (Objects.nonNull(value))? value.intValue() : defaultValue;
    }

}
