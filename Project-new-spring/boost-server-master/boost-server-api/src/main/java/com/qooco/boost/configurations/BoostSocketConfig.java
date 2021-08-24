package com.qooco.boost.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.List;

import static com.qooco.boost.constants.SocketConstants.*;

@Configuration
@EnableWebSocketMessageBroker
public class BoostSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(BROKER_CHANNEL_SECURED).setHeartbeatValue(new long[]{10000, 10000}).setTaskScheduler(heartBeatScheduler());
        config.setApplicationDestinationPrefixes(APP_DESTINATION_PREFIX);

        //Un security -> remove in next time
        config.enableSimpleBroker(BROKER_CHANNEL).setHeartbeatValue(new long[]{10000, 10000}).setTaskScheduler(heartBeatScheduler());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(END_POINT_MESSAGE_REGISTRY_SECURED).setAllowedOrigins("*");
        registry.addEndpoint(END_POINT_MESSAGE_REGISTRY_SECURED).setAllowedOrigins("*").withSockJS();

        //Un security -> remove in next time
        registry.addEndpoint(END_POINT_MESSAGE_REGISTRY).setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration webSocketTransportRegistration) {

    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration channelRegistration) {

    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration channelRegistration) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> list) {

    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> list) {
        return false;
    }

}
