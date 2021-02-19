package com.onix.kurento.model.message.output;

import com.onix.kurento.model.message.OutputMessage;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import static com.onix.kurento.enums.OutputMessageType.WEBRTC_ANSWER;

@Getter
public final class AnswerOutputMessage extends OutputMessage<AnswerOutputMessage.AnswerCandidate> {

    public AnswerOutputMessage(final Integer userId, final String sdp) {
        super(WEBRTC_ANSWER, new AnswerCandidate(userId, sdp));
    }

    @Value
    @ToString(exclude = "sdp")
    static class AnswerCandidate {

        Integer userId;
        String sdp;

    }

}
