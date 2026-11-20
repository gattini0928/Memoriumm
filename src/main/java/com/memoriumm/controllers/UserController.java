
package com.memoriumm.controllers;

import com.memoriumm.dtos.CreateUserDTO;
import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponseDTO> signup(
            @RequestBody CreateUserDTO dto
    ) {
        UserResponseDTO userDTO = service.create(
                dto.name(),
                dto.email(),
                dto.username(),
                dto.password(),
                dto.privacy()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }

    @PutMapping("me/{id}/profile-picture")
    public ResponseEntity<Void> updateProfilePicture(
            @PathVariable UUID id,
            @RequestParam("photo") MultipartFile photo
    ) {
        service.updateProfilePicture(id, photo);

        return ResponseEntity.noContent().build();
    }
}
