package com.qooco.boost.threads.notifications;

import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.baidu.yun.push.auth.PushKeyPair;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.threads.notifications.constants.DeviceType;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BaiduPushNotificationProvider {
    protected Logger logger = LogManager.getLogger(BaiduPushNotificationProvider.class);
    @Value(ApplicationConstant.BOOST_PROFILE_ANDROID_BAIDU_PUSH_NOTIFICATION_API_KEY)
    private String apiKeyProfileAndroid;
    @Value(ApplicationConstant.BOOST_PROFILE_ANDROID_BAIDU_PUSH_NOTIFICATION_SECRET_KEY)
    private String secretKeyProfileAndroid;
    @Value(ApplicationConstant.BOOST_PROFILE_IOS_BAIDU_PUSH_NOTIFICATION_API_KEY)
    private String apiKeyProfileiOS;
    @Value(ApplicationConstant.BOOST_PROFILE_IOS_BAIDU_PUSH_NOTIFICATION_SECRET_KEY)
    private String secretKeyProfileiOS;


    @Value(ApplicationConstant.BOOST_FIT_ANDROID_BAIDU_PUSH_NOTIFICATION_API_KEY)
    private String apiKeyFitAndroid;
    @Value(ApplicationConstant.BOOST_FIT_ANDROID_BAIDU_PUSH_NOTIFICATION_SECRET_KEY)
    private String secretKeyFitAndroid;
    @Value(ApplicationConstant.BOOST_FIT_IOS_BAIDU_PUSH_NOTIFICATION_API_KEY)
    private String apiKeyFitiOS;
    @Value(ApplicationConstant.BOOST_FIT_IOS_BAIDU_PUSH_NOTIFICATION_SECRET_KEY)
    private String secretKeyFitiOS;

    @Getter
    private BaiduPushClient pushClientProfileAndroid;
    @Getter
    private BaiduPushClient pushClientFitAndroid;
    @Getter
    private BaiduPushClient pushClientProfileiOS;
    @Getter
    private BaiduPushClient pushClientFitiOS;

    @PostConstruct
    public void initBaiduPushNotification() {
        pushClientFitiOS = initBaiduPushNotification(apiKeyFitiOS, secretKeyFitiOS);
        pushClientFitAndroid = initBaiduPushNotification(apiKeyFitAndroid, secretKeyFitAndroid);

        pushClientProfileiOS = initBaiduPushNotification(apiKeyProfileiOS, secretKeyProfileiOS);
        pushClientProfileAndroid = initBaiduPushNotification(apiKeyProfileAndroid, secretKeyProfileAndroid);
    }

    private BaiduPushClient initBaiduPushNotification(String apiKey, String secretKey) {
        /*1. 创建PushKeyPair
         *用于app的合法身份认证
         *apikey和secretKey可在应用详情中获取
         */
        PushKeyPair pair = new PushKeyPair(apiKey, secretKey);
        // 2. 创建BaiduPushClient，访问SDK接口
        BaiduPushClient pushClient = new BaiduPushClient(pair, BaiduPushConstants.CHANNEL_REST_URL);
        // 3. 注册YunLogHandler，获取本次请求的交互信息
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                logger.info(event.getMessage());
            }
        });
        return pushClient;
    }

    public BaiduPushClient getBaiduPushClient(int targetApp, int platform){
        switch (targetApp){
            case Const.PushTarget.PUSH_TO_CAREER:
                if(DeviceType.ANDROID == platform){
                    return pushClientProfileAndroid;
                }else if(DeviceType.IOS == platform){
                    return pushClientProfileiOS;
                }
                break;
            case Const.PushTarget.PUSH_TO_HOTEL:
                if(DeviceType.ANDROID == platform){
                    return pushClientFitAndroid;
                }else if(DeviceType.IOS == platform){
                    return pushClientFitiOS;
                }
                break;
            case Const.PushTarget.PUSH_TO_WEB:
                break;
        }
        return null;
    }

}
