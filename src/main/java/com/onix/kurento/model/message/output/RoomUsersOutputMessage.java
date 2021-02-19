package com.onix.kurento.model.message.output;

import com.onix.kurento.model.User;
import com.onix.kurento.model.message.OutputMessage;
import lombok.Getter;
import lombok.Value;

import java.util.List;

import static com.onix.kurento.enums.OutputMessageType.WEBRTC_ROOM_USERS;

@Getter
public final class RoomUsersOutputMessage extends OutputMessage<RoomUsersOutputMessage.RoomUsers> {

    public RoomUsersOutputMessage(final List<User> users) {
        super(WEBRTC_ROOM_USERS, new RoomUsers(users));
    }

    @Value
    static class RoomUsers {

        List<User> users;

    }

}
