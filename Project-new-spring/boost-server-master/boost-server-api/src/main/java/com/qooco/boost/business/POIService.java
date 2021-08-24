package com.qooco.boost.business;

import com.qooco.boost.models.poi.StaticData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface POIService {
    Map<Integer, List<Object>> readExcel(String fileLocation) throws IOException, InvalidFormatException;

    List<StaticData> readLocalizationStaticData(String fileLocation) throws IOException, InvalidFormatException;
}
