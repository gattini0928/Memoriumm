
package com.memoriumm.controllers;

import com.memoriumm.dtos.CreateUserDTO;
import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.services.UserService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {

        this.service = service;
        this.mapper = mapper;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> userProfile(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(service.findUserById(id));
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
