package com.onix.kurento.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onix.kurento.enums.StompError;
import com.onix.kurento.model.StompErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class ClientOutboundChannelService {

    private final MessageChannel clientOutboundChannel;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void sendError(final StompError reason, final String sessionId) {
        final byte[] payload = this.objectMapper.writeValueAsString(new StompErrorResponse(reason)).getBytes();

        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setMessage(reason.toString());

        this.clientOutboundChannel.send(
                MessageBuilder
                        .withPayload(payload)
                        .setHeaders(headerAccessor)
                        .build()
        );
    }

}
