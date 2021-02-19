package com.onix.kurento.config;

import com.onix.kurento.consts.Application;
import com.onix.kurento.interceptor.ClientInboundChannelInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired
    private ClientInboundChannelInterceptor clientInboundChannelInterceptor;

    @Override
    public final void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp").setAllowedOrigins("*");
    }

    @Override
    public final void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic")
                .setTaskScheduler(new DefaultManagedTaskScheduler())
                .setHeartbeatValue(Application.HEARTBEAT);
    }

    @Override
    public final void configureClientInboundChannel(final ChannelRegistration registration) {
        registration.interceptors(this.clientInboundChannelInterceptor);
    }

    @Override
    public final void configureClientOutboundChannel(final ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
                final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                log.info("OUTGOING {}", accessor.getDetailedLogMessage(message.getPayload()));
                return message;
            }
        });
    }

}
