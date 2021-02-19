package com.onix.kurento.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.security.Principal;

@AllArgsConstructor
@Getter
@ToString
public final class UserPrincipal implements Principal {

    private final Integer id;

    public String getName() {
        return this.id.toString();
    }

}
