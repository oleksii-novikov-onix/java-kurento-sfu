package com.onix.kurento.listener;

import com.onix.kurento.model.UserPrincipal;
import com.onix.kurento.service.WebRtcService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public final class StompEventListener {

    private final WebRtcService webRtcService;

    @EventListener
    public void unsubscribe(final SessionUnsubscribeEvent event) {
        final Integer userId = this.getUserId(event);

        if (Objects.nonNull(userId)) {
            this.webRtcService.leave(userId);
        }
    }

    @EventListener
    public void disconnect(final SessionDisconnectEvent event) {
        final Integer userId = this.getUserId(event);

        if (Objects.nonNull(userId)) {
            this.webRtcService.leave(userId);
        }
    }

    private Integer getUserId(final AbstractSubProtocolEvent event) {
        if (Objects.nonNull(event.getUser())) {
            final UserPrincipal userPrincipal = (UserPrincipal) event.getUser();

            return userPrincipal.getId();
        }

        return null;
    }

}
