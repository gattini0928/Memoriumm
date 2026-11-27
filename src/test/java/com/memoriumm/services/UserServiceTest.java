package com.memoriumm.services;

import com.memoriumm.dtos.LoginResponseDTO;
import com.memoriumm.dtos.UpdateProfileDTO;
import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import com.memoriumm.enums.PrivacyTypes;
import com.memoriumm.exception.*;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private S3Service s3Service;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private UserService service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(UUID uuid) {
        JwtAuthenticationToken authentication =
                mock(JwtAuthenticationToken.class);

        Jwt jwt = mock(Jwt.class);

        when(authentication.getToken()).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(uuid.toString());

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @Test
    void shouldCreateUser() {

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928$";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        UserResponseDTO expectedDTO =
                mock(UserResponseDTO.class);

        when(passwordEncoder.encode(password))
                .thenReturn("encodedPassword");

        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toDTO(any(User.class)))
                .thenReturn(expectedDTO);

        UserResponseDTO userResponseDTO =
                service.create(name, email, username, password, privacy);

        assertThat(userResponseDTO).isEqualTo(expectedDTO);

        verify(repository).save(any(User.class));
    }
    @Test
    void shouldRaiseEmailAlreadyExistsException() {

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928$";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        when(repository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() ->
                service.create(name, email, username, password, privacy)
        ).isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("já está cadastrado");
    }

    @Test
    void shouldRaiseUsernameAlreadyExistsException() {

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928$";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        when(repository.existsByUsername(username)).thenReturn(true);

        assertThatThrownBy(() ->
                service.create(name, email, username, password, privacy)
        ).isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("usuário já está cadastrado");
    }

    @Test
    void shouldNotSaveUserWithInvalidName() {

        String name = "";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928$";

        assertThatThrownBy(() ->
                service.create(name, email, username, password, PrivacyTypes.PRIVATE)
        ).isInstanceOf(InvalidNameException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldNotSaveUserWithInvalidEmail() {

        String name = "Gabriel Gattini";
        String email = "testeexample.com";
        String username = "gattini0928";
        String password = "Password0928$";

        assertThatThrownBy(() ->
                service.create(name, email, username, password, PrivacyTypes.PRIVATE)
        ).isInstanceOf(InvalidEmailException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldNotSaveUserWithInvalidUsername() {

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "ga";
        String password = "Password0928$";

        assertThatThrownBy(() ->
                service.create(name, email, username, password, PrivacyTypes.PRIVATE)
        ).isInstanceOf(InvalidUsernameException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldNotSaveUserWithInvalidPassword() {

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928";

        assertThatThrownBy(() ->
                service.create(name, email, username, password, PrivacyTypes.PRIVATE)
        ).isInstanceOf(InvalidCreatePasswordException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldAcceptNullPrivacy() {

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928$";

        service.create(name, email, username, password, null);

        verify(repository).save(argThat(user ->
                user.getPrivacy() == PrivacyTypes.PUBLIC
        ));

    }

    @Test
    void shouldLogIn() {

        String email = "teste@example.com";
        String password = "Password0928$";

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setPassword("encodedPassword");

        when(repository.findByEmailOrUsername(email, email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, user.getPassword()))
                .thenReturn(true);

        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue())
                .thenReturn("fake-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        LoginResponseDTO loginResponseDTO = service.login(email, password);

        assertThat(loginResponseDTO)
                .isEqualTo(new LoginResponseDTO("fake-token", "Bearer"));

        verify(jwtEncoder).encode(argThat(parameters ->
                parameters.getClaims().getSubject()
                        .equals(user.getUuid().toString())

                        && parameters.getClaims().getExpiresAt()
                        .equals(parameters.getClaims().getIssuedAt()
                                .plus(1, ChronoUnit.HOURS))

                        && parameters.getJwsHeader().getAlgorithm()
                        .equals(MacAlgorithm.HS256)
        ));
    }

    @Test
    void shouldRejectEmailDoesNotExist() {

        String email = "teste@example.com";
        String password = "Password0928$";

        when(repository.findByEmailOrUsername(email, email))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.login(email, password)
        ).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    void shouldRejectUsernameDoesNotExist() {

        String username = "teste@example.com";
        String password = "Password0928$";

        when(repository.findByEmailOrUsername(username, username))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.login(username, password)
        ).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    void shouldRejectInvalidPassword() {

        String email = "teste@example.com";
        String password = "Password0928$";

        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setPassword("encodedPassword");

        when(repository.findByEmailOrUsername(email, email))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(password, user.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() ->
                service.login(email, password)
        ).isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    void shouldReturnUserFindByID(){
        User user = new User();
        user.setUuid(UUID.randomUUID());

        UserResponseDTO expectedDTO = mock(UserResponseDTO.class);

        when(repository.findById(user.getUuid()))
                .thenReturn(Optional.of(user));

        when(mapper.toDTO(user))
                .thenReturn(expectedDTO);

        UserResponseDTO userResponseDTO = service.findUserById(user.getUuid());

        assertThat(userResponseDTO).isEqualTo(expectedDTO);
    }

    @Test
    void shouldRaiseUserNotFoundException() {

        UUID uuid = UUID.randomUUID();

        when(repository.findById(uuid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.findUserById(uuid)
        ).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void shouldUpdateProfile() {

        User user = new User();
        user.setUuid(UUID.randomUUID());

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String bio = "Desenvolvedor de Software";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        UpdateProfileDTO updateProfileDTO =
                new UpdateProfileDTO(name, email, username, bio, privacy);


        authenticateUser(user.getUuid());

        when(repository.findById(user.getUuid()))
                .thenReturn(Optional.of(user));

        service.updateProfile(user.getUuid(), updateProfileDTO);

        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getBio()).isEqualTo(bio);
        assertThat(user.getPrivacy()).isEqualTo(privacy);

        verify(repository).save(user);
    }

    @Test
    void shouldRejectUpdateProfileUserNotFound() {

        UUID uuid = UUID.randomUUID();

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String bio = "Desenvolvedor de Software";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        UpdateProfileDTO updateProfileDTO =
                new UpdateProfileDTO(name, email, username, bio, privacy);

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateProfile(uuid, updateProfileDTO)
        ).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void shouldRejectUpdateProfileEmailAlreadyExists() {

        User user = new User();
        UUID uuid = UUID.randomUUID();

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String bio = "Desenvolvedor de Software";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        UpdateProfileDTO updateProfileDTO =
                new UpdateProfileDTO(name, email, username, bio, privacy);

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        when(repository.existsByEmail(email))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.updateProfile(uuid, updateProfileDTO)
        ).isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("já está cadastrado");
    }

    @Test
    void shouldRejectUpdateProfileUsernameAlreadyExists() {

        User user = new User();
        UUID uuid = UUID.randomUUID();

        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String bio = "Desenvolvedor de Software";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        UpdateProfileDTO updateProfileDTO =
                new UpdateProfileDTO(name, email, username, bio, privacy);

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        when(repository.existsByUsername(username))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.updateProfile(uuid, updateProfileDTO)
        ).isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("já está cadastrado");
    }

    @Test
    void shouldKeepUserInfosWhenGivenNullValues() {

        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(uuid);
        user.setName("Gabriel Gattini");
        user.setEmail("teste@example.com");
        user.setUsername("gattini0928");
        user.setBio("Desenvolvedor de Software");
        user.setPrivacy(PrivacyTypes.PRIVATE);

        UpdateProfileDTO dto =
                new UpdateProfileDTO(null, null, null, null, null);

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        service.updateProfile(uuid, dto);

        assertThat(user.getName()).isEqualTo("Gabriel Gattini");
        assertThat(user.getEmail()).isEqualTo("teste@example.com");
        assertThat(user.getUsername()).isEqualTo("gattini0928");
        assertThat(user.getBio()).isEqualTo("Desenvolvedor de Software");
        assertThat(user.getPrivacy()).isEqualTo(PrivacyTypes.PRIVATE);

        verify(repository).save(user);
    }

    @Test
    void shouldClearBioWhenBlank() {

        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(uuid);
        user.setBio("Minha bio anterior");

        UpdateProfileDTO updateProfileDTO =
                new UpdateProfileDTO(null, null, null, "   ", null);

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        service.updateProfile(uuid, updateProfileDTO);

        assertThat(user.getBio()).isNull();

        verify(repository).save(user);
    }

    @Test
    void shouldTrimBio() {

        UUID uuid = UUID.randomUUID();

        User user = new User();
        user.setUuid(uuid);

        UpdateProfileDTO updateProfileDTO =
                new UpdateProfileDTO(null, null, null,
                        "  Desenvolvedor de Software  ", null);

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        service.updateProfile(uuid, updateProfileDTO);

        assertThat(user.getBio())
                .isEqualTo("Desenvolvedor de Software");

        verify(repository).save(user);
    }

    @Test
    void shouldRejectUnauthorizedProfileUpdate() {

        UUID ownerUuid = UUID.randomUUID();
        UUID anotherUserUuid = UUID.randomUUID();

        UpdateProfileDTO updateProfileDTO =
                new UpdateProfileDTO(null, null, null, "Nova bio", null);

        authenticateUser(anotherUserUuid);

        assertThatThrownBy(() ->
                service.updateProfile(ownerUuid, updateProfileDTO)
        ).isInstanceOf(AccessDeniedException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdatePassword() {

        UUID uuid = UUID.randomUUID();

        String currentPassword = "Password0928$";
        String newPassword = "NewPassword123#";

        User user = new User();
        user.setUuid(uuid);
        user.setPassword("encodedOldPassword");

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(currentPassword, user.getPassword()))
                .thenReturn(true);

        when(passwordEncoder.encode(newPassword))
                .thenReturn("encodedNewPassword");

        service.updatePassword(uuid, currentPassword, newPassword);

        assertThat(user.getPassword())
                .isEqualTo("encodedNewPassword");

        verify(repository).save(user);
    }

    @Test
    void shouldRejectPasswordUpdateUserNotFound() {

        UUID uuid = UUID.randomUUID();

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updatePassword(
                        uuid, "Password0928$", "NewPassword123#"
                )
        ).isInstanceOf(UserNotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectIncorrectCurrentPassword() {

        UUID uuid = UUID.randomUUID();

        String currentPassword = "WrongPassword123#";
        String newPassword = "NewPassword123#";

        User user = new User();
        user.setUuid(uuid);
        user.setPassword("encodedOldPassword");

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(currentPassword, user.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() ->
                service.updatePassword(uuid, currentPassword, newPassword)
        ).isInstanceOf(InvalidPasswordException.class);

        assertThat(user.getPassword())
                .isEqualTo("encodedOldPassword");

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectInvalidNewPassword() {

        UUID uuid = UUID.randomUUID();

        String currentPassword = "Password0928$";
        String newPassword = "123";

        User user = new User();
        user.setUuid(uuid);
        user.setPassword("encodedOldPassword");

        authenticateUser(uuid);

        when(repository.findById(uuid))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(currentPassword, user.getPassword()))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.updatePassword(uuid, currentPassword, newPassword)
        ).isInstanceOf(InvalidCreatePasswordException.class);

        assertThat(user.getPassword())
                .isEqualTo("encodedOldPassword");

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectUnauthorizedPasswordUpdate() {

        UUID ownerUuid = UUID.randomUUID();
        UUID anotherUserUuid = UUID.randomUUID();

        authenticateUser(anotherUserUuid);

        assertThatThrownBy(() ->
                service.updatePassword(
                        ownerUuid, "Password0928$", "NewPassword123#"
                )
        ).isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(repository, passwordEncoder);
    }

}
