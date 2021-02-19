package com.onix.kurento.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.*;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KurentoRoomService {

    private final KurentoClient kurentoClient;

    private final ConcurrentMap<Integer, MediaPipeline> roomMediaPipelines = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, WebRtcEndpoint> outgoingEndpoints = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, ConcurrentMap<Integer, WebRtcEndpoint>> incomingEndpoints = new ConcurrentHashMap<>();

    public void createOutgoingEndpoint(
            final int roomId,
            final int userId,
            final EventListener<IceCandidateFoundEvent> listener
    ) {
        final MediaPipeline mediaPipeline = this.getMediaPipeline(roomId);

        log.info("Create [OUTGOING_ENDPOINT] for identifier [{}]", userId);
        final WebRtcEndpoint outgoingEndpoint = new WebRtcEndpoint.Builder(mediaPipeline).build();
        outgoingEndpoint.addIceCandidateFoundListener(listener);

        this.outgoingEndpoints.put(userId, outgoingEndpoint);
    }

    public WebRtcEndpoint createIncomingEndpoint(
            final int roomId,
            final int userId,
            final int senderId,
            final EventListener<IceCandidateFoundEvent> listener
    ) {
        if (!this.incomingEndpoints.containsKey(userId)) {
            this.incomingEndpoints.put(userId, new ConcurrentHashMap<>());
        }

        final MediaPipeline mediaPipeline = this.getMediaPipeline(roomId);

        log.info("Create [INCOMING_ENDPOINT] for identifier [{}]", userId + "-" + senderId);
        final WebRtcEndpoint incomingEndpoint = new WebRtcEndpoint.Builder(mediaPipeline).build();
        incomingEndpoint.addIceCandidateFoundListener(listener);

        this.incomingEndpoints.get(userId).put(senderId, incomingEndpoint);

        return incomingEndpoint;
    }

    public WebRtcEndpoint getOutgoingEndpoint(final int userId) {
        return this.outgoingEndpoints.get(userId);
    }

    public WebRtcEndpoint getIncomingEndpoint(final int userId, final int senderId) {
        if (this.incomingEndpoints.containsKey(userId)) {
            return this.incomingEndpoints.get(userId).get(senderId);
        }

        return null;
    }

    public void removeIncomingEndpoint(final int userId, final int senderId) {
        if (this.incomingEndpoints.containsKey(userId) && this.incomingEndpoints.get(userId).containsKey(senderId)) {
            log.info("Release [INCOMING_ENDPOINT] for identifier [{}]", userId + "-" + senderId);
            this.incomingEndpoints.get(userId).remove(senderId).release();
        }
    }

    public void removeIncomingEndpoint(final int userId) {
        if (this.incomingEndpoints.containsKey(userId)) {
            this.incomingEndpoints.remove(userId)
                    .forEach((user, incomingEndpoint) -> {
                        log.info("Release [INCOMING_ENDPOINT] for identifier [{}]", user);
                        incomingEndpoint.release();
                    });
        }
    }

    public void removeOutgoingEndpoint(final int userId) {
        if (this.outgoingEndpoints.containsKey(userId)) {
            log.info("Release [OUTGOING_ENDPOINT] for identifier [{}]", userId);
            this.outgoingEndpoints.remove(userId).release();
        }
    }

    public void addIceCandidateToOutgoingEndpoint(
            final int userId,
            final String sdp,
            final String sdpMid,
            final int sdpMLineIndex
    ) {
        if (this.outgoingEndpoints.containsKey(userId)) {
            this.outgoingEndpoints.get(userId)
                    .addIceCandidate(new IceCandidate(sdp, sdpMid, sdpMLineIndex));
        }
    }

    public void addIceCandidateToIncomingEndpoint(
            final int userId,
            final int senderId,
            final String sdp,
            final String sdpMid,
            final int sdpMLineIndex
    ) {
        if (this.incomingEndpoints.containsKey(userId) && this.incomingEndpoints.get(userId).containsKey(senderId)) {
            this.incomingEndpoints.get(userId).get(senderId)
                    .addIceCandidate(new IceCandidate(sdp, sdpMid, sdpMLineIndex));
        }
    }

    public void removeRoomMediaPipeline(final int roodId) {
        if (this.roomMediaPipelines.containsKey(roodId)) {
            log.info("Release [ROOM_PIPELINE] for identifier [{}]", roodId);
            this.roomMediaPipelines.remove(roodId).release();
        }
    }

    private MediaPipeline getMediaPipeline(final int roomId) {
        if (!this.roomMediaPipelines.containsKey(roomId)) {
            log.info("Create [ROOM_PIPELINE] for identifier [{}]", roomId);
            this.roomMediaPipelines.put(roomId, this.kurentoClient.createMediaPipeline());
        }

        return this.roomMediaPipelines.get(roomId);
    }

    @PreDestroy
    public void preDestroy() {
        log.info("Start release all endpoints and pipelines.");

        this.outgoingEndpoints.forEach((userId, endpoint) -> {
            log.info("Release [OUTGOING_ENDPOINT] for identifier [{}]", userId);
            endpoint.release();
        });

        this.incomingEndpoints.forEach(
                (userId, endpoints) -> endpoints.forEach(
                        (senderId, endpoint) -> {
                            log.info("Release [INCOMING_ENDPOINT] for identifier [{}]", userId + "|" + senderId);
                            endpoint.release();
                        }
                )
        );

        this.roomMediaPipelines.forEach((roomId, pipeline) -> {
            log.info("Release [ROOM_PIPELINE] for identifier [{}]", roomId);
            pipeline.release();
        });
    }

}
