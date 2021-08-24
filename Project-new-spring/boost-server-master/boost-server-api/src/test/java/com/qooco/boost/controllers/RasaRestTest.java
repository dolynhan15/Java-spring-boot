package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessBoostHelperService;
import com.qooco.boost.business.BusinessLanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.pemistahl.lingua.api.Language.*;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.constants.ApplicationConstant.REFERRAL_CODE_LENGTH;
import static java.lang.Math.max;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.JAPANESE;
import static java.util.Locale.KOREAN;
import static java.util.Locale.*;
import static java.util.Optional.ofNullable;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnabledIfSystemProperty(named = "run.rasa.tests", matches = "true")
class RasaRestTest extends BaseMvcTest {
    private final static String CALLBACK_PATH = "/callback/";
    private final static String REST_PATH = "/rest/";
    private final static String RESP_TEXT = "text";
    private final static String REQ_MESSAGE = "message";
    private final static String REQ_SENDER = "sender";
    private final static String RESP_SUPPORT = "Hi! I can't answer you now, but I will redirect your message to our Support service, so they will be able to help you.";
    private final static String RESP_HELLO = "Hello! How are you? What would you like to know about BOOST?";
    private final static String RESP_OWN_REFER_CODE = "Hi. It’s your referral code that will get you Friend Coins if you share it. Do you need some help sharing it?";
    private final static String RESP_FRIEND_REFER_CODE = "Hello! It’s your friend’s referral code, that can get Friend Coins to you both. Do you want my help redeeming it?";
    private final static String RESP_REDEEM_CODE = "url/redeem_code?code=%s ✔";
    private final static String RESP_SHARE_CODE = "url/share_code?code=%s ✔";
    private final static String RESP_FALLBACK = "Sorry, I cannot understand you. But I certainly try to reach BOOST support team with your message.";
    private final static String SENDER1 = "1000528950";
    private final static String MSG_RESTART = "/restart";
    private final static String MSG_REDEEM_CODE = "/redeem_referral_code";
    private final static String MSG_SHARE_CODE = "/share_referral_code";
    private final static List<String> MSG_SUPPORT_EN = Stream.of("i need support", "i want support", "i need help", "i want help", "help", "can't", "find", "can", "how", "when", "who",
            "please help", "please support", "help me", "support me").collect(toImmutableList());
    private final static List<String> MSG_SUPPORT_VI = Stream.of("tôi cần hỗ trợ", "tôi cần trợ giúp", "tôi cần giúp đỡ", "xin vui lòng giúp đỡ",
            "xin vui lòng hỗ trợ", "xin vui lòng trợ giúp", "hỗ trợ tôi", "giúp đỡ tôi", "trợ giúp tôi", "giúp tôi").collect(toImmutableList());
    private final static List<String> MSG_HELLO_EN = Stream.of("hi", "hello", "good day", "how r you", "how are you").collect(toImmutableList());
    private final static List<String> MSG_HELLO_VI = Stream.of("xin chào", "xin kính chào", "chào bạn", "chào cụ ạ", "chào ông", "chào bác", "chào bà", "chào anh", "chào chú", "chào ba", "chào cậu", "chào chị", "chào thím", "chào cô", "chào em",
            "chào cháu", "chào con", "chào qúy vị", "chào quý vị", "chào buổi sáng", "chào buổi chiều", "chào buổi tối", "chào đồng chí", "chào bác đồng nghiệp", "dạ chào ông ạ", "con kính chào ông ạ", "bạn có  khoẻ không", "anh có khoẻ không",
            "chị có  khoẻ không", "khỏe chứ", "hoanh nghênh", "chào mừng", "xin chào mừng", "kính chào quý khách", "xin kính chào quý khách").collect(toImmutableList());
    private final static List<String> MSG_HELLO_TH = Stream.of("สวัสดี", "สวัสดีครับ", "สวัสดีคค่ะ", "สวัสดีคจ๊ะ", "สบายดีไหม", "สบายดีไหมครับ", "สบายดีไหมค่ะ", "สบายดีหรือ", "สบายดีรึเปล่า", "หวัดดี", "หวัดดีครับ", "หวัดดีค่ะ", "กินข้าวหรึอยัง", "ไปไหน", "ไปไหนคะ", "จะไปไหนคะ", "ไปไหนมา", "ไปไหนมาครับ",
            "ไปไหนมาค่ะ", "เป็นอย่างไรบ้าง", "เป็นยังไงบ้าง", "เป็นไงบ้าง", "เป็นไงมั่ง", "เป็นไง", "ยินดีต้อนรับ").collect(toImmutableList());
    private final static List<String> MSG_HELLO_MS = Stream.of("selamat", "selamat pagi", "selamat petang", "selamat tengahari", "selamat malam", "assalamualaikum", "apa khabar", "selamat datang").collect(toImmutableList());
    private final static List<String> MSG_HELLO_JA = Stream.of("こんにちは", "おはよう", "おはようございます", "こんばんは", "元気ですか", "お元気ですか", "だいじょうぶ です か").collect(toImmutableList());
    private final static List<String> MSG_HELLO_KO = Stream.of("안녕하십니까", "안녕하세요", "안녕", "안녕히 주무셨습니까", "안녕히 주무셨어요", "좋은아침입니다", "좋은아침", "좋은저녁입니다", "좋은밤입니다", "어떻게 지내세요",
            "어떠십니까", "어떻게 지내십니까", "잘 지넸습니까", "잘 지내셨어요", "식사하셨어요", "어디 가니", "어서 오세요", "환영합니다", "여보세요").collect(toImmutableList());
    private final static List<String> MSG_HELLO_ID = Stream.of("selamat", "selamat siang", "selamat pagi", "selamat sore", "selamat malam", "selamat datang", "salam", "apa kabar", "sudah sarapan", "hai", "met pagi", "met malam")
            .map(String::toLowerCase).collect(toImmutableList());
    private final static List<String> MSG_HELLO_ZH_CN = Stream.of("你好", "您好", "你们好", "早晨好", "早上好", "早安", "早", "下午好", "午安", "晚上好", "晚安", "你好吗", "你好嗎", "您好吗", "您好嗎", "你怎么样", "你吃飯了嗎",
            "你有吃飯嗎", "幸会", "久仰", "欢迎", "歡迎", "欢迎光临", "歡迎光臨", "喂", "大家好").collect(toImmutableList());
    private final static List<String> MSG_HELLO_ZH_TW = Stream.of("你好", "您好", "你们好", "早晨好", "早上好", "早安", "早", "下午好", "午安", "晚上好", "晚安", "你好吗", "你好嗎", "您好吗", "您好嗎", "你怎么样", "你吃飯了嗎",
            "你有吃飯嗎", "幸会", "久仰", "欢迎", "歡迎", "欢迎光临", "歡迎光臨", "喂", "大家好").collect(toImmutableList());
    private final static List<String> MSG_OWN_REFER_CODE = List.of("OWNCODE1");
    private final static List<String> MSG_NOT_UNDERSTOOD = List.of("my code ION8ẸGBD", "got code ION8ẸGBD", "code");
    private final static Map<Locale, List<String>> LANGUAGE2HELLOs = Map.of(
            KOREAN, MSG_HELLO_KO,
            JAPANESE, MSG_HELLO_JA,
            forLanguageTag(THAI.getIsoCode()), MSG_HELLO_TH,
            forLanguageTag(VIETNAMESE.getIsoCode()), MSG_HELLO_VI);
    private final static Map<Locale, List<String>> LOCALE2HELLOs = Map.of(
            ENGLISH, MSG_HELLO_EN,
            TRADITIONAL_CHINESE, MSG_HELLO_ZH_TW,
            SIMPLIFIED_CHINESE, MSG_HELLO_ZH_CN,
            forLanguageTag(INDONESIAN.getIsoCode()), MSG_HELLO_ID,
            forLanguageTag(MALAY.getIsoCode()), MSG_HELLO_MS);
    private final static Random random = new SecureRandom();
    private final static List<String> TEXT2LANG = List.of(
            "languages are awesome Cài bản mới", ENGLISH.toLanguageTag(),
            "languages are awesome Cài bản mới luôn", VIETNAMESE.getIsoCode(),
            "languages are awesome", ENGLISH.toLanguageTag(),
            "halo", ENGLISH.toLanguageTag(),
            "안녕", KOREAN.toLanguageTag(),
            "Cài bản mới luôn", VIETNAMESE.getIsoCode(),
            "12345678", "",
            "hi", "",
            "兰", SIMPLIFIED_CHINESE.toLanguageTag(),
            "蘭", TRADITIONAL_CHINESE.toLanguageTag(),
            "龙", SIMPLIFIED_CHINESE.toLanguageTag(),
            "龍", TRADITIONAL_CHINESE.toLanguageTag()
    );

    @Autowired
    private TestRestTemplate restTpl;
    @Autowired
    private BusinessLanguageService businessLanguageService;
    @Autowired
    private BusinessBoostHelperService businessBoostHelperService;

    @Value(REFERRAL_CODE_LENGTH)
    private int codeLen;

    private String rasaUrl;

    private final Consumer<Object> chatRandom = unused -> range(0, 99).mapToObj(it -> randomAlphanumeric(max(1, random.nextInt(codeLen * 2 + 1)))).filter(it -> it.length() != codeLen && !MSG_HELLO_EN.contains(it.toLowerCase())).findFirst().ifPresent(it -> assertChat(RESP_FALLBACK, it));
    private final Consumer<Object> chatSupportEn = unused -> assertChat(RESP_SUPPORT, MSG_SUPPORT_EN.get(random.nextInt(MSG_SUPPORT_EN.size())));
    private final Consumer<Object> chatSupportViet = unused -> assertChat(RESP_SUPPORT, MSG_SUPPORT_VI.get(random.nextInt(MSG_SUPPORT_VI.size())));
    private final Consumer<Object> chatHello = unused -> assertChat(RESP_HELLO, MSG_HELLO_EN.get(random.nextInt(MSG_HELLO_EN.size())));
    private final Consumer<Object> chatHelloMalay = unused -> assertChat(RESP_HELLO, MSG_HELLO_MS.get(random.nextInt(MSG_HELLO_MS.size())));
    private final Consumer<Object> chatHelloIndo = unused -> assertChat(RESP_HELLO, MSG_HELLO_ID.get(random.nextInt(MSG_HELLO_ID.size())));
    private final Consumer<Object> chatHelloJap = unused -> assertChat(RESP_HELLO, MSG_HELLO_JA.get(random.nextInt(MSG_HELLO_JA.size())));
    private final Consumer<Object> chatHelloViet = unused -> assertChat(RESP_HELLO, MSG_HELLO_VI.get(random.nextInt(MSG_HELLO_VI.size())));
    private final Consumer<Object> chatHelloChina = unused -> assertChat(RESP_HELLO, MSG_HELLO_ZH_CN.get(random.nextInt(MSG_HELLO_ZH_CN.size())));
    private final Consumer<Object> chatHelloTaiwan = unused -> assertChat(RESP_HELLO, MSG_HELLO_ZH_TW.get(random.nextInt(MSG_HELLO_ZH_TW.size())));
    private final Consumer<Object> chatHelloThai = unused -> assertChat(RESP_HELLO, MSG_HELLO_TH.get(random.nextInt(MSG_HELLO_TH.size())));
    private final Consumer<Object> chatHelloKorean = unused -> assertChat(RESP_HELLO, MSG_HELLO_KO.get(random.nextInt(MSG_HELLO_KO.size())));
    private final Consumer<Object> chatOwnReferCode = unused -> assertChat(RESP_OWN_REFER_CODE, MSG_OWN_REFER_CODE.get(random.nextInt(MSG_OWN_REFER_CODE.size())));
    private final Consumer<Object> chatNotUnderstood = unused -> assertChat(RESP_FALLBACK, MSG_NOT_UNDERSTOOD.get(random.nextInt(MSG_NOT_UNDERSTOOD.size())));
    private final Consumer<Object> chatOwnReferCodeAndShare = unused -> assertChat(RESP_SHARE_CODE, MSG_SHARE_CODE, assertChat(RESP_OWN_REFER_CODE, MSG_OWN_REFER_CODE.get(random.nextInt(MSG_OWN_REFER_CODE.size()))));
    private final Consumer<Object> chatFriendReferCode = unused -> assertChat(RESP_FRIEND_REFER_CODE, randomAlphanumeric(codeLen));
    private final Consumer<Object> chatFriendReferCodeAndRedeem = unused -> assertChat(RESP_REDEEM_CODE, MSG_REDEEM_CODE, assertChat(RESP_FRIEND_REFER_CODE, randomAlphanumeric(codeLen)));
    private final List<Consumer<Object>> allChats = List.of(chatRandom, chatHello, chatOwnReferCode, chatOwnReferCodeAndShare, chatFriendReferCode, chatFriendReferCodeAndRedeem, chatNotUnderstood, chatSupportEn);

    private String assertChat(String expected, String msg, Object... slots) {
        if (rasaUrl.contains(CALLBACK_PATH))
            System.out.println(msg + " ==> " + restTpl.postForObject(rasaUrl, Map.of(REQ_SENDER, SENDER1, REQ_MESSAGE, msg), String.class));
        else {
            System.out.println(msg + " <==> " + expected + (slots.length > 0 ? " " + Arrays.toString(slots) : ""));
            assertEquals(ofNullable(expected).map(it -> slots.length > 0 ? format(it, slots) : format(it, msg)).orElse(null),
                    stream(restTpl.postForObject(rasaUrl, Map.of(REQ_SENDER, SENDER1, REQ_MESSAGE, msg), Map[].class)).findFirst().map(it -> it.get(RESP_TEXT)).orElse(null));
        }
        return msg;
    }

    @BeforeEach
    void before() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(ENGLISH).replace(CALLBACK_PATH, REST_PATH);
        assertChat(null, MSG_RESTART);
    }

    @Test
    void notUnderstandingRandom() {
        range(0, 99).boxed().forEach(chatRandom);
    }

    @Test
    void supportEn() {
        range(0, 9).boxed().forEach(chatSupportEn);
    }

    @Test
    void supportViet() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(forLanguageTag(VIETNAMESE.getIsoCode())).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatSupportViet);
    }

    @Test
    void hello() {
        range(0, 9).boxed().forEach(chatHello);
    }

    @Test
    void helloIndo() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(forLanguageTag(INDONESIAN.getIsoCode())).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloIndo);
    }

    @Test
    void helloViet() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(forLanguageTag(VIETNAMESE.getIsoCode())).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloViet);
    }

    @Test
    void helloKorean() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(KOREAN).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloKorean);
    }

    @Test
    void helloThai() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(forLanguageTag(THAI.getIsoCode())).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloThai);
    }

    @Test
    void helloMalay() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(forLanguageTag(MALAY.getIsoCode())).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloMalay);
    }

    @Test
    void helloJap() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(JAPANESE).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloJap);
    }

    @Test
    void helloChina() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(SIMPLIFIED_CHINESE).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloChina);
    }

    @Test
    void helloTaiwan() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(TRADITIONAL_CHINESE).replace(CALLBACK_PATH, REST_PATH);
        range(0, 9).boxed().forEach(chatHelloTaiwan);
    }

    @Test
    void ownReferralCode() {
        range(0, 9).boxed().forEach(chatOwnReferCode);
    }

    @Test
    void notUnderstanding() {
        range(0, 9).boxed().forEach(chatNotUnderstood);
    }

    @Test
    void ownReferralCodeFollowedByHello() {
        range(0, 9).boxed().peek(chatOwnReferCode).forEach(chatHello);
    }

    @Test
    void ownReferralCodeFollowedByShare() {
        range(0, 9).boxed().forEach(chatOwnReferCodeAndShare);
    }

    @Test
    void ownReferralCodeFollowedByRandom() {
        range(0, 99).boxed().peek(chatOwnReferCode).forEach(chatRandom);
    }

    @Test
    void ownReferralCodeFollowedByFriendReferralCode() {
        range(0, 9).boxed().peek(chatOwnReferCode).forEach(chatFriendReferCode);
    }

    @Test
    void ownReferralCodeFollowedByOwnReferralCode() {
        range(0, 9).boxed().peek(chatOwnReferCode).forEach(chatOwnReferCode);
    }

    @Test
    void friendReferralCode() {
        range(0, 9).boxed().forEach(chatFriendReferCode);
    }

    @Test
    void friendReferralCodeFollowedByHello() {
        range(0, 9).boxed().peek(chatFriendReferCode).forEach(chatHello);
    }

    @Test
    void friendReferralCodeFollowedByfriendReferralCode() {
        range(0, 9).boxed().peek(chatFriendReferCode).forEach(chatFriendReferCode);
    }

    @Test
    void friendReferralCodeFollowedByOwnReferralCode() {
        range(0, 9).boxed().peek(chatFriendReferCode).forEach(chatOwnReferCode);
    }

    @Test
    void friendReferralCodeFollowedByRandom() {
        range(0, 99).boxed().peek(chatFriendReferCode).forEach(chatRandom);
    }

    @Test
    void friendReferralCodeFollowedByRedeem() {
        range(0, 9).boxed().forEach(chatFriendReferCodeAndRedeem);
    }

    @Test
    void randomConversationSize10() {
        range(0, 10).forEach(it -> allChats.get(random.nextInt(allChats.size())).accept(it));
    }

    @Test
    void randomConversationSize100() {
        range(0, 100).forEach(it -> allChats.get(random.nextInt(allChats.size())).accept(it));
    }

    @Test
    void randomConversationSize1000() {
        range(0, 1000).forEach(it -> allChats.get(random.nextInt(allChats.size())).accept(it));
    }

    @Test
    @Disabled
    void randomConversationSize1000Callback() {
        rasaUrl = businessBoostHelperService.getRasaEndpoint(ENGLISH);
        range(0, 1000).forEach(it -> allChats.get(random.nextInt(allChats.size())).accept(it));
    }

    @Test
    void detectLanguage() {
        range(0, TEXT2LANG.size() / 2).forEach(idx -> assertEquals(ofNullable(TEXT2LANG.get(idx * 2 + 1)).filter(lng -> !lng.isEmpty()).map(Locale::forLanguageTag).orElse(null),
                businessLanguageService.detectSupportedRasaLocale(TEXT2LANG.get(idx * 2))));
        LANGUAGE2HELLOs.forEach((lng, hellos) -> hellos.forEach(it -> assertEquals(lng, businessLanguageService.detectSupportedRasaLocale(it))));
        LOCALE2HELLOs.forEach((lng, hellos) -> hellos.forEach(it -> assertEquals(lng, businessLanguageService.detectSupportedRasaLocale(it, lng))));
    }
}
