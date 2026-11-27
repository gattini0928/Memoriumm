package com.memoriumm.services;

import com.memoriumm.dtos.LoginResponseDTO;
import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import com.memoriumm.enums.PrivacyTypes;
import com.memoriumm.exception.*;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
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

        // Arrange
        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928$";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        when(repository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() ->
                service.create(name, email, username, password, privacy)
        ).isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("já esta cadastrado");
    }

    @Test
    void shouldRaiseUsernameAlreadyExistsException() {

        // Arrange
        String name = "Gabriel Gattini";
        String email = "teste@example.com";
        String username = "gattini0928";
        String password = "Password0928$";
        PrivacyTypes privacy = PrivacyTypes.PRIVATE;

        when(repository.existsByUsername(username)).thenReturn(true);

        assertThatThrownBy(() ->
                service.create(name, email, username, password, privacy)
        ).isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("usuário já esta cadastrado");
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



}
