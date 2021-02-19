package com.onix.kurento.model.message.output;

import com.onix.kurento.model.message.OutputMessage;
import lombok.Getter;
import lombok.Value;

import static com.onix.kurento.enums.OutputMessageType.WEBRTC_ADD_ICE_CANDIDATE;

@Getter
public final class AddIceCandidateOutputMessage extends OutputMessage<AddIceCandidateOutputMessage.AddIceCandidate> {

    public AddIceCandidateOutputMessage(
            final int userId,
            final String sdp,
            final String sdpMid,
            final int sdpMLineIndex
    ) {
        super(WEBRTC_ADD_ICE_CANDIDATE, new AddIceCandidate(userId, sdp, sdpMid, sdpMLineIndex));
    }

    @Value
    static class AddIceCandidate {

        int userId;
        String sdp;
        String sdpMid;
        int sdpMLineIndex;

    }

}
