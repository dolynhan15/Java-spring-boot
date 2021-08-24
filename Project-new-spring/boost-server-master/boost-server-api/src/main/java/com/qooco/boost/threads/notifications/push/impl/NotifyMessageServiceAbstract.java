package com.qooco.boost.threads.notifications.push.impl;

import com.baidu.yun.push.model.PushBatchUniMsgRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.baidu.yun.push.model.PushRequest;
import com.google.gson.GsonBuilder;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.oracle.entities.views.ViewAccessTokenRoles;
import com.qooco.boost.data.oracle.services.views.ViewAccessTokenRolesService;
import com.qooco.boost.threads.notifications.constants.DeviceType;
import com.qooco.boost.threads.notifications.dto.UserProfileDTO;
import com.qooco.boost.threads.notifications.enumeration.PushType;
import com.qooco.boost.threads.notifications.messages.AndroidMessage;
import com.qooco.boost.threads.notifications.messages.IOSAps;
import com.qooco.boost.threads.notifications.messages.IOSMessage;
import com.qooco.boost.threads.notifications.requests.NotifyPushTask;
import com.qooco.boost.threads.notifications.requests.PushNotificationMessage;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;

public abstract class NotifyMessageServiceAbstract {

    private static final int NOTIFY_RING = 0x04;
    private static final int NOTIFY_VIBRATION = 0x02;
    private static final int NOTIFY_CLEAR = 0x01;

    private static final int NOTIFICATION_TYPE = NOTIFY_RING | NOTIFY_VIBRATION | NOTIFY_CLEAR;
    private static final int SILENT_TYPE = NOTIFY_CLEAR;

    private static int OPEN_NOTHING = 0;
    private static int OPEN_URL = 1;
    private static int OPEN_CUSTOM = 2;

    protected abstract long getMessageNotifyExpired();

    protected abstract int getDeployStatus();

    protected abstract ViewAccessTokenRolesService getViewAccessTokenRolesService();

    protected List<PushNotificationMessage> createPushNotificationMessageForSingle(NotifyPushTask pushTask, String title, String decs, Object customContent) {
        List<PushNotificationMessage> results = new ArrayList<>();

        List<ViewAccessTokenRoles> viewAccessTokenRoles = getViewAccessTokenRolesService()
                .findByUserProfileIds(pushTask.getReceiverIds(), getAppId(pushTask.getTargetApp()), true);

        if(CollectionUtils.isNotEmpty(viewAccessTokenRoles)) {
            PushNotificationMessage pushMsg;
            PushRequest push;

            List<ViewAccessTokenRoles> sendToAndroid = getViewAccessTokenRolesByDeviceType(viewAccessTokenRoles, DeviceType.ANDROID);
            if(CollectionUtils.isNotEmpty(sendToAndroid)){
                push = createPushMsgRequest(sendToAndroid, pushTask.getPushType(), title, decs, customContent);
                pushMsg = new PushNotificationMessage(pushTask.getTargetApp(), push);
                results.add(pushMsg);
            }

            List<ViewAccessTokenRoles> sendToIOS = getViewAccessTokenRolesByDeviceType(viewAccessTokenRoles, DeviceType.IOS);
            if(CollectionUtils.isNotEmpty(sendToIOS)){
                push = createPushMsgRequest(sendToIOS, pushTask.getPushType(), title, decs, customContent);
                pushMsg = new PushNotificationMessage(pushTask.getTargetApp(), push);
                results.add(pushMsg);
            }
        }
        return results;
    }

    private <T extends PushRequest> T createPushMsgRequest(List<ViewAccessTokenRoles> accessTokenRoles, PushType pushType,
                                                           String title, String desc, Object customContent){
        return createPushMsgRequest(accessTokenRoles, pushType, title, desc, null, null, customContent);
    }

    private <T extends PushRequest> T createPushMsgRequest(List<ViewAccessTokenRoles> accessTokenRoles, PushType pushType,
                                                           String title, String desc, String url, String pkgContent,
                                                           Object customContent){
        PushRequest result = null;
        if(CollectionUtils.isNotEmpty(accessTokenRoles)){
            int deviceType = accessTokenRoles.get(0).getDeviceType();
            String msg = createDevicePushMessage(deviceType, pushType, title, desc, url, pkgContent, customContent);

            if(accessTokenRoles.size() == 1){
                result = new PushMsgToSingleDeviceRequest();
                ((PushMsgToSingleDeviceRequest) result).setChannelId(accessTokenRoles.get(0).getChannelId());

                ((PushMsgToSingleDeviceRequest) result).setMessage(msg);
                ((PushMsgToSingleDeviceRequest) result).setMessageType(pushType.getValue());
                ((PushMsgToSingleDeviceRequest) result).addExpires(getMessageNotifyExpired());
                if (deviceType == DeviceType.IOS) {
                    ((PushMsgToSingleDeviceRequest) result).setDeployStatus(getDeployStatus());
                }

            }else {
                result = new PushBatchUniMsgRequest();
                String[] channelIds = accessTokenRoles.stream().map(ViewAccessTokenRoles::getChannelId).toArray(String[]:: new);
                ((PushBatchUniMsgRequest) result).setChannelIds(channelIds);

                ((PushBatchUniMsgRequest) result).setMessage(msg);
                ((PushBatchUniMsgRequest) result).setMessageType(pushType.getValue());
                ((PushBatchUniMsgRequest) result).addExpires(getMessageNotifyExpired());
            }

            result.setDeviceType(deviceType);
        }
        return (T) result;
    }

    private PushMsgToSingleDeviceRequest createSingleDeviceRequest(ViewAccessTokenRoles accessTokenRole, PushType pushType, String title, String desc, Object customContent) {
        return createSingleDeviceRequest(accessTokenRole, pushType, title, desc, null, null, customContent);
    }

    private PushMsgToSingleDeviceRequest createSingleDeviceRequest(ViewAccessTokenRoles accessTokenRole, PushType pushType,
                                                                   String title, String desc, String url,
                                                                   String pkgContent, Object customContent) {
        PushMsgToSingleDeviceRequest singleDeviceMsg = null;
        if (Objects.nonNull(accessTokenRole)) {
            int deviceType = accessTokenRole.getDeviceType();
            String msg = createDevicePushMessage(deviceType, pushType, title, desc, url, pkgContent, customContent);

            singleDeviceMsg = new PushMsgToSingleDeviceRequest();
            singleDeviceMsg.setMessage(msg);
            singleDeviceMsg.setDeviceType(deviceType);
            singleDeviceMsg.setMessageType(pushType.getValue());
            singleDeviceMsg.setChannelId(accessTokenRole.getChannelId());
            singleDeviceMsg.addExpires(getMessageNotifyExpired());

            if (deviceType == DeviceType.IOS) {
                singleDeviceMsg.setDeployStatus(getDeployStatus());
            }
        }
        return singleDeviceMsg;
    }

    private String createDevicePushMessage(int deviceType, PushType pushType,
                                           String title, String desc, String url,
                                           String pkgContent, Object customContent) {
        switch (deviceType) {
            case DeviceType.ANDROID:
                AndroidMessage androidMsg = createAndroidMessage(
                        title,
                        desc,
                        pushType == PushType.NOTIFICATION ? NOTIFICATION_TYPE : SILENT_TYPE,
                        OPEN_CUSTOM,
                        url,
                        pkgContent,
                        customContent
                );
                return convertToJson(androidMsg);
            case DeviceType.IOS:
                IOSMessage iosMsg = createIOSMessage(
                        desc,
                        customContent
                );
                return convertToJson(iosMsg);
            default:
                return null;
        }
    }

    private IOSMessage createIOSMessage(String alertContent, Object customContent) {
        IOSMessage message = new IOSMessage();
        IOSAps aps = new IOSAps();
        aps.setAlert(alertContent);
        aps.setBadge(0);
        aps.setSound(null);
        aps.setContentAvailable(1);
        aps.setMutableContent(1);

        message.setAps(aps);
        message.setCustomContent(customContent);
        return message;
    }

    private AndroidMessage createAndroidMessage(String title, String des, int notifyStyle,
                                                int openType, String url, String pkgContent,
                                                Object customContent) {
        AndroidMessage message = new AndroidMessage();

        message.setTitle(title);
        message.setDescription(des);
        message.setBasicStyle(notifyStyle);
        message.setOpenType(openType);
        message.setUrl(url);
        //android client custom notification style, if not set default to 0.
        message.setBuilderId(0);
        message.setPkgContent(pkgContent);
        message.setCustomContent(customContent);
        return message;
    }

    private String getAppId(int target) {
        switch (target) {
            case Const.PushTarget.PUSH_TO_HOTEL:
                return SELECT_APP.appId();
            case Const.PushTarget.PUSH_TO_CAREER:
                return PROFILE_APP.appId();
            default:
                return null;
        }
    }

    private String convertToJson(Object message) {
        return new GsonBuilder().serializeNulls().create().toJson(message);
    }

    protected String getTitle(UserProfileDTO userProfile) {
        return StringUtil.append(userProfile.getFirstName(), " ", userProfile.getLastName());
    }

    private List<ViewAccessTokenRoles> getViewAccessTokenRolesByDeviceType(List<ViewAccessTokenRoles> viewAccessTokenRoles, int deviceType){
        if(CollectionUtils.isNotEmpty(viewAccessTokenRoles)){
            return viewAccessTokenRoles.stream().filter(vr -> vr.getDeviceType() == deviceType).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
