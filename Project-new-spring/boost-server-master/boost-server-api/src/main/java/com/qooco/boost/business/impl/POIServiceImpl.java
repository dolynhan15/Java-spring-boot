package com.qooco.boost.business.impl;

import com.qooco.boost.business.FileStorageService;
import com.qooco.boost.business.POIService;
import com.qooco.boost.models.poi.StaticData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class POIServiceImpl implements POIService {

    private static final int MODULE_INDEX = 1;
    private static final int KEY_INDEX = 2;
    private static final int CONTENT_INDEX = 9;
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public Map<Integer, List<Object>> readExcel(String fileLocation) throws IOException, InvalidFormatException {
        Map<Integer, List<Object>> data = new HashMap<>();
        Workbook workbook = null;
        FileInputStream file = null;
        try {
            Resource resource = fileStorageService.loadResource(fileLocation);
            workbook = new XSSFWorkbook(resource.getFile());
            Sheet sheet = workbook.getSheetAt(0);
            int i = 0;
            for (Row row : sheet) {
                data.put(i, new ArrayList());
                for (Cell cell : row) {
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            data.get(i).add(cell.getRichStringCellValue().getString());
                            break;
                        case NUMERIC:
                            data.get(i).add(DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : (int) cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            data.get(i).add(cell.getBooleanCellValue());
                            break;
                        case FORMULA:
                            data.get(i).add(cell.getCellFormula());
                            break;
                        default:
                            data.get(i).add("");
                    }
                }
                i++;
            }
        } finally {
            if (Objects.nonNull(file)) {
                file.close();
            }
            if (Objects.nonNull(workbook)) {
                workbook.close();
            }
        }

        return data;

    }

    @Override
    public List<StaticData> readLocalizationStaticData(String fileLocation) throws IOException, InvalidFormatException {
        List<StaticData> data = new ArrayList<>();
        Workbook workbook = null;
        FileInputStream file = null;
        try {
            Resource resource = fileStorageService.loadResource(fileLocation);
            workbook = new XSSFWorkbook(resource.getFile());
            workbook.iterator().forEachRemaining(sheet -> {
                for (Row row : sheet) {
                    if (row.getRowNum() > 0) {
                        StaticData staticData = new StaticData();
                        staticData.setLocale(sheet.getSheetName());
                        for (Cell cell : row) {
                            String value = "";
                            switch (cell.getCellType()) {
                                case STRING:
                                    value = cell.getRichStringCellValue().getString();
                                    break;
                                case NUMERIC:
                                    value = String.valueOf(DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : (int) cell.getNumericCellValue());
                                    break;
                                case BOOLEAN:
                                    value = String.valueOf(cell.getBooleanCellValue());
                                    break;
                                case FORMULA:
                                    value = String.valueOf(cell.getCellFormula());
                                    break;
                                default:
                                    break;
                            }
                            switch (cell.getColumnIndex()) {
                                case MODULE_INDEX:
                                    staticData.setModule(value);
                                    break;
                                case KEY_INDEX:
                                    staticData.setKey(value);
                                    break;
                                case CONTENT_INDEX:
                                    staticData.setContent(value);
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (!staticData.hasEmptyValue()) {
                            data.add(staticData);
                        }
                    }
                }
            });
        } finally {
            if (Objects.nonNull(workbook)) {
                workbook.close();
            }
        }

        return data;
    }
}
