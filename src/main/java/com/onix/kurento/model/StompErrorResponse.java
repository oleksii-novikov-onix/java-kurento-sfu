package com.onix.kurento.model;

import com.onix.kurento.enums.StompError;
import lombok.Value;

@Value
public class StompErrorResponse {

    StompError reason;

}
