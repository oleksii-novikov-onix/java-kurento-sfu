package com.onix.kurento.interceptor;

import com.onix.kurento.enums.StompError;
import com.onix.kurento.model.User;
import com.onix.kurento.model.UserPrincipal;
import com.onix.kurento.service.ClientOutboundChannelService;
import com.onix.kurento.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public final class ClientInboundChannelInterceptor implements ChannelInterceptor {

    private final UserService userService;
    private final ClientOutboundChannelService clientOutboundChannelService;

    @Override
    public Message<?> preSend(Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (Objects.nonNull(accessor)) {
            log.info("INCOMING {}", accessor.getDetailedLogMessage(message.getPayload()));

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                message = this.connect(message, accessor);
            }
        }

        return message;
    }

    private Message<?> connect(Message<?> message, final StompHeaderAccessor accessor) {
        final int userId = Integer.parseInt(accessor.getFirstNativeHeader("user-id"));

        final Optional<User> optionalUser = this.userService.findById(userId);

        if (optionalUser.isPresent()) {
            accessor.setUser(new UserPrincipal(userId));
        } else {
            this.clientOutboundChannelService.sendError(StompError.INVALID_USER_ID, accessor.getSessionId());

            message = null;
        }

        return message;
    }

}
