package com.qooco.boost.threads.actors.syncs;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import com.qooco.boost.business.QoocoSyncService;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.mongo.entities.localization.qooco.*;
import com.qooco.boost.data.mongo.services.localization.qooco.*;
import com.qooco.boost.models.qooco.sync.localization.CollectionData;
import com.qooco.boost.models.qooco.sync.localization.GetLocalizationResponse;
import com.qooco.boost.models.qooco.sync.localization.LocaleData;
import com.qooco.boost.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/2/2018 - 1:59 PM
*/
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LocalizationActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(LocalizationActor.class);
    public static final String ACTOR_NAME = "localizationActor";

    private ArSaDocService arSaDocService;
    private DeDeDocService deDeDocService;
    private EnGbDocService enGbDocService;
    private EnUsDocService enUsDocService;
    private EsMxDocService esMxDocService;
    private FrFrDocService frFrDocService;
    private IdIdDocService idIdDocService;
    private JaJpDocService jaJpDocService;
    private KmKhDocService kmKhDocService;
    private KoKrDocService koKrDocService;
    private MsMyDocService msMyDocService;
    private MyMmDocService myMmDocService;
    private PtBrDocService ptBrDocService;
    private RuRuDocService ruRuDocService;
    private ThThDocService thThDocService;
    private ViVnDocService viVnDocService;
    private ZhCnDocService zhCnDocService;
    private ZhTwDocService zhTwDocService;
    private QoocoSyncService qoocoSyncService;

    public LocalizationActor(ArSaDocService arSaDocService,
                             DeDeDocService deDeDocService,
                             EnGbDocService enGbDocService,
                             EnUsDocService enUsDocService,
                             EsMxDocService esMxDocService,
                             FrFrDocService frFrDocService,
                             IdIdDocService idIdDocService,
                             JaJpDocService jaJpDocService,
                             KmKhDocService kmKhDocService,
                             KoKrDocService koKrDocService,
                             MsMyDocService msMyDocService,
                             MyMmDocService myMmDocService,
                             PtBrDocService ptBrDocService,
                             RuRuDocService ruRuDocService,
                             ThThDocService thThDocService,
                             ViVnDocService viVnDocService,
                             ZhCnDocService zhCnDocService,
                             ZhTwDocService zhTwDocService,
                             QoocoSyncService qoocoSyncService) {
        this.arSaDocService = arSaDocService;
        this.deDeDocService = deDeDocService;
        this.enGbDocService = enGbDocService;
        this.enUsDocService = enUsDocService;
        this.esMxDocService = esMxDocService;
        this.frFrDocService = frFrDocService;
        this.idIdDocService = idIdDocService;
        this.jaJpDocService = jaJpDocService;
        this.kmKhDocService = kmKhDocService;
        this.koKrDocService = koKrDocService;
        this.msMyDocService = msMyDocService;
        this.myMmDocService = myMmDocService;
        this.ptBrDocService = ptBrDocService;
        this.ruRuDocService = ruRuDocService;
        this.thThDocService = thThDocService;
        this.viVnDocService = viVnDocService;
        this.zhCnDocService = zhCnDocService;
        this.zhTwDocService = zhTwDocService;
        this.qoocoSyncService = qoocoSyncService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            String collection = (String) message;
            try {
                if (QoocoApiConstants.SYNC_LEVEL_TESTS.equals(collection)) {
                    syncData(collection);
                } else {
                    syncForceData(collection);
                }
            } catch (ResourceAccessException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    private void syncData(String collection) {
        GetLocalizationResponse response = qoocoSyncService.getLocalization(initLocaleData(collection));
        if (Objects.nonNull(response) && Objects.nonNull(response.getLocalizationStrings2())) {
            processLocalizationData(response.getLocalizationStrings2());
        }
        getSender().tell(QoocoApiConstants.GET_LEVEL_TEST_WIZARDS, ActorRef.noSender());
    }

    private void syncForceData(String collection) {
        GetLocalizationResponse response = qoocoSyncService.getLocalization(initLocaleData(collection));
        if (Objects.nonNull(response) && Objects.nonNull(response.getLocalizationStrings2())) {
            processLocalizationData(response.getLocalizationStrings2());
        }
        getSender().tell(StringUtil.append(QoocoApiConstants.GET_LEVEL_TEST_WIZARDS, QoocoApiConstants.SYNC_FORCE), ActorRef.noSender());
    }

    private void processLocalizationData(Map<String, Map<String, CollectionData>> localizationData) {
        localizationData.forEach((key, value) -> {
            switch (key) {
                case QoocoApiConstants.LOCALE_AR_SA:
                    processArSaLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_DE_DE:
                    processDeDeLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_EN_GB:
                    processEnGbLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_EN_US:
                    processEnUsLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_ES_MX:
                    processEsMxLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_FR_FR:
                    processFrFrLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_ID_ID:
                    processIdIdLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_JA_JP:
                    processFrFrLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_KM_KH:
                    processKmKhLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_KO_KR:
                    processKoKrLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_MS_MY:
                    processMsMyLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_MY_MM:
                    processMyMmLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_PT_BR:
                    processPtBrLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_RU_RU:
                    processRuRuLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_TH_TH:
                    processThThLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_VI_VN:
                    processViVnLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_ZH_CN:
                    processZhCnLocale(value);
                    break;
                case QoocoApiConstants.LOCALE_ZH_TW:
                    processZhTwLocale(value);
                    break;
                default:
                    break;
            }
        });
    }

    private void processArSaLocale(Map<String, CollectionData> collectionData) {
        List<ArSaDoc> arSaDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            ArSaDoc arSaDoc = new ArSaDoc(id, key, value.getTimestamp(), content);
            arSaDocs.add(arSaDoc);
        }));
        arSaDocService.save(arSaDocs);
    }

    private void processDeDeLocale(Map<String, CollectionData> collectionData) {
        List<DeDeDoc> deDeDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            DeDeDoc deDeDoc = new DeDeDoc(id, key, value.getTimestamp(), content);
            deDeDocs.add(deDeDoc);
        }));
        deDeDocService.save(deDeDocs);
    }

    private void processEnGbLocale(Map<String, CollectionData> collectionData) {
        List<EnGbDoc> enGbDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            EnGbDoc enGbDoc = new EnGbDoc(id, key, value.getTimestamp(), content);
            enGbDocs.add(enGbDoc);
        }));
        enGbDocService.save(enGbDocs);
    }

    private void processEnUsLocale(Map<String, CollectionData> collectionData) {
        List<EnUsDoc> enUsDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            EnUsDoc enUsDoc = new EnUsDoc(id, key, value.getTimestamp(), content);
            enUsDocs.add(enUsDoc);
        }));
        enUsDocService.save(enUsDocs);
    }

    private void processEsMxLocale(Map<String, CollectionData> collectionData) {
        List<EsMxDoc> esMxDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            EsMxDoc enUsDoc = new EsMxDoc(id, key, value.getTimestamp(), content);
            esMxDocs.add(enUsDoc);
        }));
        esMxDocService.save(esMxDocs);
    }

    private void processFrFrLocale(Map<String, CollectionData> collectionData) {
        List<FrFrDoc> frFrDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            FrFrDoc frFrDoc = new FrFrDoc(id, key, value.getTimestamp(), content);
            frFrDocs.add(frFrDoc);
        }));
        frFrDocService.save(frFrDocs);
    }

    private void processIdIdLocale(Map<String, CollectionData> collectionData) {
        List<IdIdDoc> idIdDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            IdIdDoc idIdDoc = new IdIdDoc(id, key, value.getTimestamp(), content);
            idIdDocs.add(idIdDoc);
        }));
        idIdDocService.save(idIdDocs);
    }

    private void processJaJpLocale(Map<String, CollectionData> collectionData) {
        List<JaJpDoc> jaJpDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            JaJpDoc jaJpDoc = new JaJpDoc(id, key, value.getTimestamp(), content);
            jaJpDocs.add(jaJpDoc);
        }));
        jaJpDocService.save(jaJpDocs);
    }

    private void processKmKhLocale(Map<String, CollectionData> collectionData) {
        List<KmKhDoc> kmKhDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            KmKhDoc kmKhDoc = new KmKhDoc(id, key, value.getTimestamp(), content);
            kmKhDocs.add(kmKhDoc);
        }));
        kmKhDocService.save(kmKhDocs);
    }

    private void processKoKrLocale(Map<String, CollectionData> collectionData) {
        List<KoKrDoc> koKrDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            KoKrDoc koKrDoc = new KoKrDoc(id, key, value.getTimestamp(), content);
            koKrDocs.add(koKrDoc);
        }));
        koKrDocService.save(koKrDocs);
    }

    private void processMsMyLocale(Map<String, CollectionData> collectionData) {
        List<MsMyDoc> msMyDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            MsMyDoc msMyDoc = new MsMyDoc(id, key, value.getTimestamp(), content);
            msMyDocs.add(msMyDoc);
        }));
        msMyDocService.save(msMyDocs);
    }

    private void processMyMmLocale(Map<String, CollectionData> collectionData) {
        List<MyMmDoc> myMmDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            MyMmDoc myMmDoc = new MyMmDoc(id, key, value.getTimestamp(), content);
            myMmDocs.add(myMmDoc);
        }));
        myMmDocService.save(myMmDocs);
    }

    private void processPtBrLocale(Map<String, CollectionData> collectionData) {
        List<PtBrDoc> ptBrDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            PtBrDoc ptBrDoc = new PtBrDoc(id, key, value.getTimestamp(), content);
            ptBrDocs.add(ptBrDoc);
        }));
        ptBrDocService.save(ptBrDocs);
    }

    private void processRuRuLocale(Map<String, CollectionData> collectionData) {
        List<RuRuDoc> ruRuDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            RuRuDoc ruRuDoc = new RuRuDoc(id, key, value.getTimestamp(), content);
            ruRuDocs.add(ruRuDoc);
        }));
        ruRuDocService.save(ruRuDocs);
    }

    private void processThThLocale(Map<String, CollectionData> collectionData) {
        List<ThThDoc> thThDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            ThThDoc thThDoc = new ThThDoc(id, key, value.getTimestamp(), content);
            thThDocs.add(thThDoc);
        }));
        thThDocService.save(thThDocs);
    }

    private void processViVnLocale(Map<String, CollectionData> collectionData) {
        List<ViVnDoc> viVnDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            ViVnDoc viVnDoc = new ViVnDoc(id, key, value.getTimestamp(), content);
            viVnDocs.add(viVnDoc);
        }));
        viVnDocService.save(viVnDocs);
    }

    private void processZhCnLocale(Map<String, CollectionData> collectionData) {
        List<ZhCnDoc> zhCnDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            ZhCnDoc zhCnDoc = new ZhCnDoc(id, key, value.getTimestamp(), content);
            zhCnDocs.add(zhCnDoc);
        }));
        zhCnDocService.save(zhCnDocs);
    }

    private void processZhTwLocale(Map<String, CollectionData> collectionData) {
        List<ZhTwDoc> zhTwDocs = new ArrayList<>();
        collectionData.forEach((key, value) -> value.getStrings().forEach((id, content) -> {
            ZhTwDoc zhTwDoc = new ZhTwDoc(id, key, value.getTimestamp(), content);
            zhTwDocs.add(zhTwDoc);
        }));
        zhTwDocService.save(zhTwDocs);
    }

    private LocaleData initArSaLocale(String collection) {
        long arSaTimeStamp = 0;
        ArSaDoc arSaDoc = arSaDocService.findByLatestCollection(collection);
        if (Objects.nonNull(arSaDoc) && Objects.nonNull(arSaDoc.getTimestamp())) {
            arSaTimeStamp = arSaDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, arSaTimeStamp, QoocoApiConstants.LOCALE_AR_SA);
    }

    private LocaleData initDeDeLocale(String collection) {
        long deDeTimeStamp = 0;
        DeDeDoc deDeDoc = deDeDocService.findByLatestCollection(collection);
        if (Objects.nonNull(deDeDoc) && Objects.nonNull(deDeDoc.getTimestamp())) {
            deDeTimeStamp = deDeDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, deDeTimeStamp, QoocoApiConstants.LOCALE_DE_DE);
    }

    private LocaleData initEnGbLocale(String collection) {
        long enGbTimeStamp = 0;
        EnGbDoc enGbDoc = enGbDocService.findByLatestCollection(collection);
        if (Objects.nonNull(enGbDoc) && Objects.nonNull(enGbDoc.getTimestamp())) {
            enGbTimeStamp = enGbDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, enGbTimeStamp, QoocoApiConstants.LOCALE_EN_GB);
    }

    private LocaleData initEnUsLocale(String collection) {
        long enUsTimeStamp = 0;
        EnUsDoc enUsDoc = enUsDocService.findByLatestCollection(collection);
        if (Objects.nonNull(enUsDoc) && Objects.nonNull(enUsDoc.getTimestamp())) {
            enUsTimeStamp = enUsDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, enUsTimeStamp, QoocoApiConstants.LOCALE_EN_US);
    }

    private LocaleData initEsMxLocale(String collection) {
        long esMxTimeStamp = 0;
        EsMxDoc esMxDoc = esMxDocService.findByLatestCollection(collection);
        if (Objects.nonNull(esMxDoc) && Objects.nonNull(esMxDoc.getTimestamp())) {
            esMxTimeStamp = esMxDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, esMxTimeStamp, QoocoApiConstants.LOCALE_ES_MX);
    }

    private LocaleData initFrFrLocale(String collection) {
        long frFrTimeStamp = 0;
        FrFrDoc frFrDoc = frFrDocService.findByLatestCollection(collection);
        if (Objects.nonNull(frFrDoc) && Objects.nonNull(frFrDoc.getTimestamp())) {
            frFrTimeStamp = frFrDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, frFrTimeStamp, QoocoApiConstants.LOCALE_FR_FR);
    }

    private LocaleData initIdIdLocale(String collection) {
        long idIdTimeStamp = 0;
        IdIdDoc idIdDoc = idIdDocService.findByLatestCollection(collection);
        if (Objects.nonNull(idIdDoc) && Objects.nonNull(idIdDoc.getTimestamp())) {
            idIdTimeStamp = idIdDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, idIdTimeStamp, QoocoApiConstants.LOCALE_ID_ID);
    }

    private LocaleData initJaJpLocale(String collection) {
        long jaJpTimestamp = 0;
        JaJpDoc jaJpDoc = jaJpDocService.findByLatestCollection(collection);
        if (Objects.nonNull(jaJpDoc) && Objects.nonNull(jaJpDoc.getTimestamp())) {
            jaJpTimestamp = jaJpDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, jaJpTimestamp, QoocoApiConstants.LOCALE_JA_JP);
    }

    private LocaleData initKmKhLocale(String collection) {
        long kmKhTimestamp = 0;
        KmKhDoc kmKhDoc = kmKhDocService.findByLatestCollection(collection);
        if (Objects.nonNull(kmKhDoc) && Objects.nonNull(kmKhDoc.getTimestamp())) {
            kmKhTimestamp = kmKhDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, kmKhTimestamp, QoocoApiConstants.LOCALE_KM_KH);
    }

    private LocaleData initKoKrLocale(String collection) {
        long koKrTimestamp = 0;
        KoKrDoc koKrDoc = koKrDocService.findByLatestCollection(collection);
        if (Objects.nonNull(koKrDoc) && Objects.nonNull(koKrDoc.getTimestamp())) {
            koKrTimestamp = koKrDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, koKrTimestamp, QoocoApiConstants.LOCALE_KO_KR);
    }

    private LocaleData initMsMyLocale(String collection) {
        long msMyTimestamp = 0;
        MsMyDoc msMyDoc = msMyDocService.findByLatestCollection(collection);
        if (Objects.nonNull(msMyDoc) && Objects.nonNull(msMyDoc.getTimestamp())) {
            msMyTimestamp = msMyDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, msMyTimestamp, QoocoApiConstants.LOCALE_MS_MY);
    }

    private LocaleData initMyMmLocale(String collection) {
        long myMmTimestamp = 0;
        MyMmDoc myMmDoc = myMmDocService.findByLatestCollection(collection);
        if (Objects.nonNull(myMmDoc) && Objects.nonNull(myMmDoc.getTimestamp())) {
            myMmTimestamp = myMmDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, myMmTimestamp, QoocoApiConstants.LOCALE_MY_MM);
    }

    private LocaleData initPtBrLocale(String collection) {
        long ptBrTimestamp = 0;
        PtBrDoc ptBrDoc = ptBrDocService.findByLatestCollection(collection);
        if (Objects.nonNull(ptBrDoc) && Objects.nonNull(ptBrDoc.getTimestamp())) {
            ptBrTimestamp = ptBrDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, ptBrTimestamp, QoocoApiConstants.LOCALE_PT_BR);
    }

    private LocaleData initRuRuLocale(String collection) {
        long ruRuTimestamp = 0;
        RuRuDoc ruRuDoc = ruRuDocService.findByLatestCollection(collection);
        if (Objects.nonNull(ruRuDoc) && Objects.nonNull(ruRuDoc.getTimestamp())) {
            ruRuTimestamp = ruRuDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, ruRuTimestamp, QoocoApiConstants.LOCALE_RU_RU);
    }

    private LocaleData initThThLocale(String collection) {
        long thThTimestamp = 0;
        ThThDoc thThDoc = thThDocService.findByLatestCollection(collection);
        if (Objects.nonNull(thThDoc) && Objects.nonNull(thThDoc.getTimestamp())) {
            thThTimestamp = thThDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, thThTimestamp, QoocoApiConstants.LOCALE_TH_TH);
    }

    private LocaleData initViVnLocale(String collection) {
        long viVnTimestamp = 0;
        ViVnDoc viVnDoc = viVnDocService.findByLatestCollection(collection);
        if (Objects.nonNull(viVnDoc) && Objects.nonNull(viVnDoc.getTimestamp())) {
            viVnTimestamp = viVnDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, viVnTimestamp, QoocoApiConstants.LOCALE_VI_VN);
    }

    private LocaleData initZhCnLocale(String collection) {
        long zhCnTimestamp = 0;
        ZhCnDoc zhCnDoc = zhCnDocService.findByLatestCollection(collection);
        if (Objects.nonNull(zhCnDoc) && Objects.nonNull(zhCnDoc.getTimestamp())) {
            zhCnTimestamp = zhCnDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, zhCnTimestamp, QoocoApiConstants.LOCALE_ZH_CN);
    }

    private LocaleData initZhTwLocale(String collection) {
        long zhTwTimestamp = 0;
        ZhTwDoc zhTwDoc = zhTwDocService.findByLatestCollection(collection);
        if (Objects.nonNull(zhTwDoc) && Objects.nonNull(zhTwDoc.getTimestamp())) {
            zhTwTimestamp = zhTwDoc.getTimestamp().getTime();
        }
        return new LocaleData(collection, zhTwTimestamp, QoocoApiConstants.LOCALE_ZH_TW);
    }

    private List<LocaleData> initLocaleData(String collection) {
        List<LocaleData> localeData = new ArrayList<>();
        localeData.add(initArSaLocale(collection));
        localeData.add(initDeDeLocale(collection));
        localeData.add(initEnGbLocale(collection));
        localeData.add(initEnUsLocale(collection));
        localeData.add(initEsMxLocale(collection));
        localeData.add(initFrFrLocale(collection));
        localeData.add(initIdIdLocale(collection));
        localeData.add(initJaJpLocale(collection));
        localeData.add(initKmKhLocale(collection));
        localeData.add(initKoKrLocale(collection));
        localeData.add(initMsMyLocale(collection));
        localeData.add(initMyMmLocale(collection));
        localeData.add(initPtBrLocale(collection));
        localeData.add(initRuRuLocale(collection));
        localeData.add(initThThLocale(collection));
        localeData.add(initViVnLocale(collection));
        localeData.add(initZhCnLocale(collection));
        localeData.add(initZhTwLocale(collection));
        return localeData;
    }
}
