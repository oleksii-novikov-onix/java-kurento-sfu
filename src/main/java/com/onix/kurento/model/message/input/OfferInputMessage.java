package com.onix.kurento.model.message.input;

import lombok.ToString;
import lombok.Value;

@Value
@ToString(exclude = "sdp")
public class OfferInputMessage {

    Integer userId;
    String sdp;

}
