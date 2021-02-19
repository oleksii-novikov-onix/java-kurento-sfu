package com.onix.kurento.model.message.input;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class AddIceCandidateInputMessage {

    Integer userId;
    String sdp;
    String sdpMid;
    int sdpMLineIndex;

}
