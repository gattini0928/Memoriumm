
package com.memoriumm.controllers;

import com.memoriumm.dtos.*;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
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

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = service.login(
                request.login(),
                request.password()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> profile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID id = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));

        return ResponseEntity.ok(service.findUserById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> userProfile(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(service.findUserById(id));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateProfileDTO userProfileDTO
    ) {
        UUID id = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );

        service.updateProfile(id, userProfileDTO);

        return ResponseEntity.noContent().build();
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
