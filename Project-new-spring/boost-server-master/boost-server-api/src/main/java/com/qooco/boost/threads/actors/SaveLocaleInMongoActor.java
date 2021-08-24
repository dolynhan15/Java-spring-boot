package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.mongo.entities.localization.BaseLocaleDoc;
import com.qooco.boost.data.mongo.entities.localization.boost.*;
import com.qooco.boost.data.mongo.services.localization.boost.BoostLocaleService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveLocaleInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveLocaleInMongoActor.class);
    public static final String ACTOR_NAME = "saveLocaleInMongoActor";

    private static final String LOCALE_RESOURCE_FILE = "classpath:db/data/locale.xlsx";
    private final BoostLocaleService boostLocaleService;
    private static final int MAX_DOCUMENT_NUMBER = 100;

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            try {
                readLocaleAndSave(LOCALE_RESOURCE_FILE);
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    private void readLocaleAndSave(String excelFilePath) throws IOException {
        File excelFile = ResourceUtils.getFile((excelFilePath));
        String md5File = checkSumOfFile(excelFile.getAbsolutePath());
        if (!isModified(md5File, ColumnName.EN_US.clazz)) return;


        //TODO: Remove drop collection after version 2.1.4 go to production
        ColumnName.getLanguageSupport().forEach(it -> boostLocaleService.drop(it.clazz().getSimpleName()));
        boostLocaleService.drop("BoostEnGbDoc");

        var excelInputStream = new FileInputStream(excelFile);
        var workbook = new XSSFWorkbook(excelInputStream);
        var sheet = workbook.getSheetAt(0);

        Map<Integer, ColumnName> columnIndexes = new HashMap<>();
        Map<Class, List<BaseLocaleDoc>> result = new HashMap<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                columnIndexes = getColumnIndex(row);
                continue;
            }

            Map<Class, BaseLocaleDoc> localeMap = createLocaleDoc(row, columnIndexes);
            localeMap.forEach((clazz, locale) -> ofNullable(locale).ifPresent(it -> {
                List<BaseLocaleDoc> locales = ofNullable(result.get(clazz)).orElse(new ArrayList<>());
                it.setMd5(md5File);
                locales.add(it);
                result.put(clazz, locales);
            }));

            if(row.getRowNum() % MAX_DOCUMENT_NUMBER == 0){
                result.forEach((clazz, locales) -> ofNullable(locales).filter(CollectionUtils::isNotEmpty).ifPresent(it -> boostLocaleService.insertAll(locales,clazz)));
                result.clear();
            }
        }

         result.forEach((clazz, locales) -> boostLocaleService.insertAll(locales,clazz));

        workbook.close();
        excelInputStream.close();
    }

    private Map<Class, BaseLocaleDoc> createLocaleDoc(Row row, Map<Integer, ColumnName> columnIndexes) {
        Map<Class, BaseLocaleDoc> locales = initLocaleDoc();
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                ColumnName columnName = columnIndexes.get(cell.getColumnIndex());
                switch (columnName) {
                    case ID:
                        locales.values().forEach(it -> it.id(cell.getStringCellValue()));
                        break;
                    case COLLECTION:
                        locales.values().forEach(it -> it.collection(cell.getStringCellValue()));
                        break;
                    case EN_US:
                    case ID_ID:
                    case JA_JP:
                    case KO_KR:
                    case MS_MY:
                    case TH_TH:
                    case VI_VN:
                    case ZH_CN:
                    case ZH_TW:
                        locales.get(columnName.clazz).content(cell.getStringCellValue());
                }
            }
        }
        return locales;
    }


    private Map<Integer, ColumnName> getColumnIndex(Row row) {
        var result = new HashMap<Integer, ColumnName>();
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                Arrays.stream(ColumnName.values()).filter(name -> name.key.equals(cell.getStringCellValue()))
                        .findFirst().ifPresent(value -> result.put(cell.getColumnIndex(), value));
            }
        }
        return result;
    }

    private boolean isModified(String fileMD5, Class clazz) {
        var baseLocate = boostLocaleService.findOneHasMD5(clazz);
        var result = new AtomicBoolean(true);
        ofNullable(baseLocate).ifPresent(it -> result.set(!fileMD5.equals(baseLocate.getMd5())));
        return result.get();
    }

    private String checkSumOfFile(String filePath) {
        try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
            return DigestUtils.md5Hex(is);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return "";
    }

    private Map<Class, BaseLocaleDoc> initLocaleDoc(){
        Map<Class, BaseLocaleDoc> result = new HashMap<>();
        ColumnName.getLanguageSupport().forEach(it -> result.put(it.clazz, initLocaleDoc(it)));
        return result;
    }
    private BaseLocaleDoc initLocaleDoc(ColumnName localeColumn) {
        switch (localeColumn) {
            case ID_ID:
                return new BoostIdIdDoc();
            case JA_JP:
                return new BoostJaJpDoc();
            case KO_KR:
                return new BoostKoKrDoc();
            case MS_MY:
                return new BoostMsMyDoc();
            case TH_TH:
                return new BoostThThDoc();
            case VI_VN:
                return new BoostViVnDoc();
            case ZH_CN:
                return new BoostZhCnDoc();
            case ZH_TW:
                return new BoostZhTwDoc();
            case EN_US:
            default:
                return new BoostEnUsDoc();
        }
    }

    @RequiredArgsConstructor
    enum ColumnName {
        ID(":ID", null),
        COLLECTION(":COLLECTION", null),
        EN_US(":EN_US", BoostEnUsDoc.class),
        ID_ID(":ID_ID", BoostIdIdDoc.class),
        JA_JP(":JA_JP", BoostJaJpDoc.class),
        KO_KR(":KO_KR", BoostKoKrDoc.class),
        MS_MY(":MS_MY", BoostMsMyDoc.class),
        TH_TH(":TH_TH", BoostThThDoc.class),
        VI_VN(":VI_VN", BoostViVnDoc.class),
        ZH_CN(":ZH_CN", BoostZhCnDoc.class),
        ZH_TW(":ZH_TW", BoostZhTwDoc.class);

        @Getter
        @Accessors(fluent = true)
        private final String key;
        @Getter
        @Accessors(fluent = true)
        private final Class clazz;

        public static List<ColumnName> getLanguageSupport() {
            return ImmutableList.of(EN_US, ID_ID, JA_JP, KO_KR, MS_MY, TH_TH, VI_VN, ZH_CN, ZH_TW);
        }
    }
}
