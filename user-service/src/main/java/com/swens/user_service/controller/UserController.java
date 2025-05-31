package com.swens.user_service.controller;


import com.swens.user_service.dto.UserLoginDTO;
import com.swens.user_service.dto.UserRequestDTO;
import com.swens.user_service.dto.UserResponseDTO;
import com.swens.user_service.service.UserService;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Validated({Default.class}) @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()               // get the current request URL (/users or whatever)
                .path("/{id}")                      // append /{id}
                .buildAndExpand(createdUser.getId())  // expand {id} with the created user's ID
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }


    @GetMapping("/{email}")
    public ResponseEntity<UserLoginDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

}
