package com.memoriumm.services;

import com.memoriumm.dtos.UserResponseDTO;
import com.memoriumm.entities.User;
import com.memoriumm.enums.PrivacyTypes;
import com.memoriumm.exception.EmailAlreadyExistsException;
import com.memoriumm.exception.UsernameAlreadyExistsException;
import com.memoriumm.mappers.UserMapper;
import com.memoriumm.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;

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
                org.mockito.Mockito.mock(UserResponseDTO.class);

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
}
