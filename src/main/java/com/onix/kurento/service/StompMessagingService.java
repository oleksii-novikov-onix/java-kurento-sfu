package com.onix.kurento.service;

import com.onix.kurento.consts.Destination;
import com.onix.kurento.model.message.OutputMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public final class StompMessagingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public <D> void sendToUser(final Integer userId, final OutputMessage<D> message) {
        final String destination = Destination.TOPIC + "/" + userId;

        this.simpMessagingTemplate.convertAndSend(destination, message);
    }

}
