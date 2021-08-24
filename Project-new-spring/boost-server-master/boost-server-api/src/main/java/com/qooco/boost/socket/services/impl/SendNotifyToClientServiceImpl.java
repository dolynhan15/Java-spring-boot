package com.qooco.boost.socket.services.impl;

import com.qooco.boost.constants.SocketConstants;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.enumeration.BoostHelperParticipant;
import com.qooco.boost.socket.services.SendNotifyToClientService;
import com.qooco.boost.threads.notifications.dto.NotifyData;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SendNotifyToClientServiceImpl implements SendNotifyToClientService {
    protected Logger logger = LogManager.getLogger(SendNotifyToClientServiceImpl.class);
    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void sendNotifyMessage(NotifyData notifyData) {
        if (Objects.nonNull(notifyData)) {
            sendNotifyMessageToUserProfileIdAndAppId(notifyData);
        }
    }

    private void sendNotifyMessageToUserProfileIdAndAppId(NotifyData notifyData) {
        Map<String, List<Long>> targets = notifyData.getTargets();
        if(MapUtils.isNotEmpty(targets)) {
            targets.forEach((appId, userProfileIds) -> userProfileIds.stream().filter(it -> !BoostHelperParticipant.getIds().contains(it)).forEach(userProfileId -> {
                String channelSecuredAppId = StringUtil.append(SocketConstants.PRIVATE_CHANNEL_NOTIFY_DATA_SECURED,
                        Constants.DOT, userProfileId.toString(), Constants.DOT, appId);
                this.template.convertAndSend(channelSecuredAppId, notifyData);

                logger.debug(StringUtil.append("Channel Secured AppId : ", channelSecuredAppId));
                logger.debug(StringUtil.append("Data : ", StringUtil.convertToJson(notifyData)));
            }));
        }
    }

    @Override
    public void sendNotifyMessage(List<NotifyData> notifiesData) {
        if (CollectionUtils.isNotEmpty(notifiesData)) {
            notifiesData.forEach(this::sendNotifyMessage);
        }
    }
}