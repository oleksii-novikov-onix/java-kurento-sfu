package com.onix.kurento.model.message;

import com.onix.kurento.enums.OutputMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class OutputMessage<D> {

    OutputMessageType id;
    D data;

}
