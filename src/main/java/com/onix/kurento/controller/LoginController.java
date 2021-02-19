package com.onix.kurento.controller;

import com.onix.kurento.model.LoginRequest;
import com.onix.kurento.model.User;
import com.onix.kurento.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public final class LoginController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> login(final @Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(this.userService.add(loginRequest.getName()));
    }

}
