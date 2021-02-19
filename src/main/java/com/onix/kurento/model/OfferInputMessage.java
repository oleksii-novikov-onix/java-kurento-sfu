package com.onix.kurento.model;

import lombok.ToString;
import lombok.Value;

@Value
@ToString(exclude = "sdp")
public class OfferInputMessage {

    Integer userId;
    String sdp;

}
