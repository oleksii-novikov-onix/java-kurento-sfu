package com.onix.kurento.model.message.output;

import com.onix.kurento.model.User;
import com.onix.kurento.model.message.OutputMessage;
import lombok.Getter;
import lombok.Value;

import static com.onix.kurento.enums.OutputMessageType.WEBRTC_ROOM_USER_LEFT;

@Getter
public final class RoomUserLeftOutputMessage extends OutputMessage<RoomUserLeftOutputMessage.RoomUserLeft> {

    public RoomUserLeftOutputMessage(final User user) {
        super(WEBRTC_ROOM_USER_LEFT, new RoomUserLeft(user));
    }

    @Value
    static class RoomUserLeft {

        User user;

    }

}
