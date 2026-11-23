
package com.memoriumm.controllers;

import com.memoriumm.dtos.*;
import com.memoriumm.entities.User;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.services.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Set;
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
            @Valid
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
            @Valid
            @RequestBody UpdateProfileDTO userProfileDTO
    ) {
        UUID id = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );

        service.updateProfile(id, userProfileDTO);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody UpdatePasswordDTO request
    ) {
        UUID id = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );

        service.updatePassword(
                id,
                request.currentPassword(),
                request.newPassword()
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/profile-picture")
    public ResponseEntity<Void> updateProfilePicture(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("photo") MultipartFile photo
    ) {
        UUID id = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );

        service.updateProfilePicture(id, photo);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> follow(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        UUID followerID = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );

        service.followUser(followerID, id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollow(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        UUID followerID = UUID.fromString(
                Objects.requireNonNull(jwt.getSubject())
        );

        service.unfollowUser(followerID, id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<Set<UserResponseDTO>> getAllUserFollowers(
            @PathVariable UUID id
    ) {
        Set<UserResponseDTO> followers = service.getAllUserFollowers(id);

        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<Set<UserResponseDTO>> getAllUserFollowing(
            @PathVariable UUID id
    ) {
        Set<UserResponseDTO> following = service.getAllUserFollowing(id);

        return ResponseEntity.ok(following);
    }
}
