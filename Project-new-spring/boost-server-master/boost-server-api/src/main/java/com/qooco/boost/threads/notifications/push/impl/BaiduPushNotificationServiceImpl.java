package com.qooco.boost.threads.notifications.push.impl;

import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.*;
import com.qooco.boost.threads.notifications.enumeration.BaiduErrorCode;
import com.qooco.boost.threads.notifications.push.BaiduPushNotificationService;
import com.qooco.boost.threads.notifications.response.PushMessageResponse;
import com.qooco.boost.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 1:48 PM
 */
@Service
public class BaiduPushNotificationServiceImpl implements BaiduPushNotificationService {
    protected Logger logger = LogManager.getLogger(BaiduPushNotificationServiceImpl.class);

    @Override
    public PushMessageResponse pushNotification(BaiduPushClient pushClient, Object message) {
        PushMessageResponse pushMessageResponse = new PushMessageResponse();
        pushMessageResponse.setErrorCode(BaiduErrorCode.SUCCESS.getErrorCode());
        pushMessageResponse.setErrorMsg(BaiduErrorCode.SUCCESS.getErrorMessage());
        if (Objects.nonNull(pushClient)) {
            try {
                logger.info("Push content : " + StringUtil.convertToJson(message));
                if (message instanceof PushMsgToSingleDeviceRequest) {
                    PushMsgToSingleDeviceResponse response = pushClient.pushMsgToSingleDevice((PushMsgToSingleDeviceRequest) message);
                    pushMessageResponse = updateResponseInformation(pushMessageResponse, response);
                    logger.info(StringUtil.append("msgId: ", response.getMsgId(), ",sendTime: ", String.valueOf(response.getSendTime())));
                } else if (message instanceof PushBatchUniMsgRequest) {
                    PushBatchUniMsgResponse response = pushClient.pushBatchUniMsg((PushBatchUniMsgRequest) message);
                    pushMessageResponse = updateResponseInformation(pushMessageResponse, response);
                    logger.info(StringUtil.append("msgId: ", response.getMsgId(), ",sendTime: ", String.valueOf(response.getSendTime())));
                } else if (message instanceof PushMsgToAllRequest) {
                    PushMsgToAllResponse response = pushClient.pushMsgToAll((PushMsgToAllRequest) message);
                    pushMessageResponse = updateResponseInformation(pushMessageResponse, response);
                    logger.info(StringUtil.append("msgId: ", response.getMsgId(), ",sendTime: ", String.valueOf(response.getSendTime()), ",timerId: ", response.getTimerId()));
                }
            } catch (PushClientException e) {
                e.printStackTrace();
                pushMessageResponse.setErrorMsg(e.getMessage());
                pushMessageResponse.setErrorCode(BaiduErrorCode.CLIENT_EXCEPTION.getErrorCode());

            } catch (PushServerException e) {
                logger.error(String.format("requestId: %d, errorCode: %d, errorMsg: %s", e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
                pushMessageResponse.setRequestId(e.getRequestId());
                pushMessageResponse.setErrorCode(e.getErrorCode());
                pushMessageResponse.setErrorMsg(e.getErrorMsg());
            }
        }
        return pushMessageResponse;
    }

    private PushMessageResponse updateResponseInformation(PushMessageResponse pushResponse, PushResponse response) {
        if (response instanceof PushMsgToSingleDeviceResponse) {
            pushResponse.setRequestId(((PushMsgToSingleDeviceResponse) response).getSendTime());
            pushResponse.setSendTime(((PushMsgToSingleDeviceResponse) response).getSendTime());
            pushResponse.setMessageId(((PushMsgToSingleDeviceResponse) response).getMsgId());
        } else if (response instanceof PushBatchUniMsgResponse) {
            pushResponse.setSendTime(((PushBatchUniMsgResponse) response).getSendTime());
            pushResponse.setMessageId(((PushBatchUniMsgResponse) response).getMsgId());
        } else if (response instanceof PushMsgToAllResponse) {
            pushResponse.setSendTime(((PushMsgToAllResponse) response).getSendTime());
            pushResponse.setMessageId(((PushMsgToAllResponse) response).getMsgId());
        }
        return pushResponse;
    }
}
