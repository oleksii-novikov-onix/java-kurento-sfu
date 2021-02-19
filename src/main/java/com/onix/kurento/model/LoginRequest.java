package com.onix.kurento.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public final class LoginRequest {

    @NotEmpty
    private String name;

}
