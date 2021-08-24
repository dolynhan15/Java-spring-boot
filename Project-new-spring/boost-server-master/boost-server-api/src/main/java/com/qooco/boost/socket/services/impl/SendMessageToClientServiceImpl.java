package com.qooco.boost.socket.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.MessageCenterType;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.qooco.boost.constants.SocketConstants.PRIVATE_CHANNEL_CHATTING;
import static com.qooco.boost.constants.SocketConstants.PRIVATE_CHANNEL_CHATTING_SECURED;
import static com.qooco.boost.data.enumeration.MessageCenterType.BOOST_SUPPORT_CHANNEL;
import static java.util.Optional.ofNullable;

@Service
public class SendMessageToClientServiceImpl implements SendMessageToClientService {
    protected Logger logger = LogManager.getLogger(SendMessageToClientServiceImpl.class);

    private static final List<String> CHANNELS = ImmutableList.of(
            PRIVATE_CHANNEL_CHATTING_SECURED,
            PRIVATE_CHANNEL_CHATTING);

    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";

    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void sendMessage(MessageBase doc, String locale) {
        ofNullable(doc).ifPresent(it -> sendMessage(doc, true, locale));
    }

    @Override
    public void sendMessage(MessageBase doc, boolean isEncrypted, String locale) {
        ofNullable(doc).ifPresent(it -> sendMessage(new MessageDTO(doc, MessageConstants.SUBMIT_MESSAGE_ACTION, isEncrypted, qoocoDomainPath, locale)));
    }

    @Override
    public void sendMessage(MessageBase doc, int messageAction, String locale) {
        ofNullable(doc).ifPresent(it -> sendMessage(new MessageDTO(doc, messageAction, qoocoDomainPath, locale)));
    }

    @Override
    public void sendMessage(List<MessageDTO> messages) {
        ofNullable(messages).filter(CollectionUtils::isNotEmpty).ifPresent(it -> it.forEach(this::sendMessage));
    }

    @Override
    public void sendMessage(MessageDTO message) {
        ofNullable(message).ifPresent(it -> CHANNELS.forEach(ch -> sendMessage(it, ch)));
    }

    private void sendMessage(MessageDTO message, String channel) {
        if (Objects.nonNull(message) && StringUtils.isNotBlank(channel)) {
            var destination = StringUtil.append(channel, Constants.DOT, message.getConversationId());
            if (PRIVATE_CHANNEL_CHATTING.equals(channel)) {
                sendMessageToWebChannel(message, destination);
            } else {
                this.template.convertAndSend(destination, message);
                logger.info(String.format("Channel : %s", destination));
                logger.info(String.format("Data : %s", StringUtil.convertToJson(message)));
            }
        }
    }

    //TODO: Remove in next time after web update secured
    private void sendMessageToWebChannel(MessageDTO message, String destination) {
        if(message.getMessageCenterType() == BOOST_SUPPORT_CHANNEL.value()) {

            var copy = message;
            if (Objects.nonNull(message.getSecretKey())) {
                var gson = new Gson();
                copy = gson.fromJson(gson.toJson(message), MessageDTO.class);
                copy.setContent(CipherKeys.decryptByAES(message.getContent(), message.getSecretKey()));
            }
            this.template.convertAndSend(destination, copy);
            logger.info(String.format("Channel : %s", destination));
            logger.info(String.format("Data : %s", StringUtil.convertToJson(copy)));
        }
    }
}
