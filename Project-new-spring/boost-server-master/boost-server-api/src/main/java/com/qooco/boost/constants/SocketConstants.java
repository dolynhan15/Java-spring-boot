package com.qooco.boost.constants;

public class SocketConstants {

    public static final String APP_DESTINATION_PREFIX = "/pata";
    public static final String END_POINT_MESSAGE_REGISTRY_SECURED = "/boost-message-secured";
    public static final String END_POINT_MESSAGE_REGISTRY = "/boost-message";

    public static final String BROKER_CHANNEL_SECURED = "/channel.secured";
    public static final String BROKER_CHANNEL = "/channel";

    private static final String PRIVATE_CHATTING_ENCRYPT_METHOD = "/private.chat.encrypted";
    private static final String PRIVATE_CHATTING_METHOD = "/private.chat";
    public static final String PRIVATE_NOTIFY_METHOD = "/private.notify";



    public static final String PRIVATE_CHANNEL_CHATTING_SECURED = BROKER_CHANNEL_SECURED + PRIVATE_CHATTING_ENCRYPT_METHOD;
    public static final String PRIVATE_CHANNEL_NOTIFY_DATA_SECURED = BROKER_CHANNEL_SECURED + PRIVATE_NOTIFY_METHOD;

    public static final String PRIVATE_CHANNEL_CHATTING = BROKER_CHANNEL + PRIVATE_CHATTING_METHOD;
}
