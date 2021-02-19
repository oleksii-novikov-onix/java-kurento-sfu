package com.onix.kurento.model.message.output;

import com.onix.kurento.model.User;
import com.onix.kurento.model.message.OutputMessage;
import lombok.Getter;
import lombok.Value;

import static com.onix.kurento.enums.OutputMessageType.WEBRTC_ROOM_USER_ADDED;

@Getter
public final class RoomUserAddedOutputMessage extends OutputMessage<RoomUserAddedOutputMessage.RoomUserAdded> {

    public RoomUserAddedOutputMessage(final User user) {
        super(WEBRTC_ROOM_USER_ADDED, new RoomUserAdded(user));
    }

    @Value
    static class RoomUserAdded {

        User user;

    }

}
