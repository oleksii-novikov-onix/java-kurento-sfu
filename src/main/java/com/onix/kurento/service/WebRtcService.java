package com.onix.kurento.service;

import com.onix.kurento.model.User;
import com.onix.kurento.model.message.output.*;
import lombok.RequiredArgsConstructor;
import org.kurento.client.IceCandidate;
import org.kurento.client.WebRtcEndpoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebRtcService {

    private static final int ROOM_ID = 1;

    private final UserService userService;
    private final RoomUserService roomUserService;
    private final KurentoRoomService kurentoRoomService;
    private final StompMessagingService stompMessagingService;

    public void join(final int userId) {
        final Optional<User> userOptional = this.userService.findById(userId);

        if (userOptional.isPresent()) {
            final User user = userOptional.get();

            final List<User> roomUsers = this.roomUserService.findAll();

            final Optional<User> roomUserOptional = this.roomUserService.findById(userId);

            if (roomUserOptional.isPresent()) {
                roomUsers.forEach(roomUser -> this.kurentoRoomService.removeIncomingEndpoint(roomUser.getId(), userId));
                this.kurentoRoomService.removeIncomingEndpoint(userId);
                this.kurentoRoomService.removeOutgoingEndpoint(userId);
            } else {
                this.roomUserService.add(user);
            }

            this.kurentoRoomService.createOutgoingEndpoint(
                    ROOM_ID,
                    userId,
                    event -> {
                        final IceCandidate iceCandidate = event.getCandidate();
                        this.stompMessagingService.sendToUser(userId, new AddIceCandidateOutputMessage(
                                userId,
                                iceCandidate.getCandidate(),
                                iceCandidate.getSdpMid(),
                                iceCandidate.getSdpMLineIndex()
                        ));
                    }
            );

            roomUsers.forEach(roomUser -> this.stompMessagingService.sendToUser(
                    roomUser.getId(),
                    new RoomUserAddedOutputMessage(user)
            ));
            this.stompMessagingService.sendToUser(userId, new RoomUsersOutputMessage(roomUsers));
        }
    }

    public void offer(final int userId, final int senderId, final String sdpOffer) {
        this.kurentoRoomService.removeIncomingEndpoint(userId, senderId);

        final Optional<User> roomUserOptional = this.roomUserService.findById(userId);

        if (roomUserOptional.isPresent()) {
            final WebRtcEndpoint incomingEndpoint = userId == senderId
                    ? this.kurentoRoomService.getOutgoingEndpoint(userId)
                    : this.kurentoRoomService.createIncomingEndpoint(
                    ROOM_ID,
                    userId,
                    senderId,
                    event -> {
                        final IceCandidate iceCandidate = event.getCandidate();
                        this.stompMessagingService.sendToUser(userId, new AddIceCandidateOutputMessage(
                                senderId,
                                iceCandidate.getCandidate(),
                                iceCandidate.getSdpMid(),
                                iceCandidate.getSdpMLineIndex()
                        ));
                    }
            );

            if (userId != senderId) {
                this.kurentoRoomService.getOutgoingEndpoint(senderId).connect(incomingEndpoint);
            }

            final String sdpAnswer = incomingEndpoint.processOffer(sdpOffer);
            this.stompMessagingService.sendToUser(userId, new AnswerOutputMessage(senderId, sdpAnswer));

            incomingEndpoint.gatherCandidates();
        }
    }

    public void leave(final int userId) {
        final Optional<User> roomUserOptional = this.roomUserService.findById(userId);

        if (roomUserOptional.isPresent()) {
            final User user = roomUserOptional.get();
            this.roomUserService.delete(userId);

            final List<User> roomUsers = this.roomUserService.findAll();
            roomUsers.forEach(roomUser -> {
                this.kurentoRoomService.removeIncomingEndpoint(roomUser.getId(), userId);

                this.stompMessagingService.sendToUser(
                        roomUser.getId(),
                        new RoomUserLeftOutputMessage(user)
                );
            });

            if (roomUsers.isEmpty()) {
                this.kurentoRoomService.removeRoomMediaPipeline(ROOM_ID);
            }

            this.kurentoRoomService.removeIncomingEndpoint(userId);
            this.kurentoRoomService.removeOutgoingEndpoint(userId);
        }
    }

    public void iceCandidate(
            final int userId,
            final int senderId,
            final String sdp,
            final String sdpMid,
            final int sdpMLineIndex
    ) {
        if (userId == senderId) {
            this.kurentoRoomService.addIceCandidateToOutgoingEndpoint(userId, sdp, sdpMid, sdpMLineIndex);
        } else {
            this.kurentoRoomService.addIceCandidateToIncomingEndpoint(userId, senderId, sdp, sdpMid, sdpMLineIndex);
        }
    }

}
