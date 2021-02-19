package com.onix.kurento.controller;

import com.onix.kurento.model.AddIceCandidateInputMessage;
import com.onix.kurento.model.OfferInputMessage;
import com.onix.kurento.model.UserPrincipal;
import com.onix.kurento.service.WebRtcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@MessageMapping("/webrtc")
public final class CallController {

    private final WebRtcService webRtcService;

    @MessageMapping("/join")
    void join(final UserPrincipal principal) {
        log.info("INCOMING JOIN, user {}", principal.getId());
        this.webRtcService.join(principal.getId());
    }

    @MessageMapping("/offer")
    void offer(final @Payload OfferInputMessage message, final UserPrincipal principal) {
        log.info("INCOMING OFFER {}, user {}", message, principal.getId());
        this.webRtcService.offer(principal.getId(), message.getUserId(), message.getSdp());
    }

    @MessageMapping("/leave")
    void leave(final UserPrincipal principal) {
        log.info("INCOMING LEAVE, user {}", principal.getId());
        this.webRtcService.leave(principal.getId());
    }

    @MessageMapping("/add-ice-candidate")
    void iceCandidate(
            final @Payload AddIceCandidateInputMessage message,
            final UserPrincipal principal
    ) {
        log.info("INCOMING ICE CANDIDATE {}, user {}", message, principal.getId());
        this.webRtcService.iceCandidate(
                principal.getId(),
                message.getUserId(),
                message.getSdp(),
                message.getSdpMid(),
                message.getSdpMLineIndex()
        );
    }

}
