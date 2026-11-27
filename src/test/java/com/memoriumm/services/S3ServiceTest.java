package com.memoriumm.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    private S3Service service;

    @BeforeEach
    void setUp() {
        service = new S3Service(s3Client, "test-bucket");
    }

    @Test
    void shouldUploadJpeg() throws IOException {
        assertSuccessfulUpload("image/jpeg", "jpg");
    }

    @Test
    void shouldUploadPng() throws IOException {
        assertSuccessfulUpload("image/png", "png");
    }

    @Test
    void shouldUploadWebp() throws IOException {
        assertSuccessfulUpload("image/webp", "webp");
    }

    @Test
    void shouldRejectNullPhoto() {

        assertThatThrownBy(() ->
                service.uploadProfilePicture(null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Imagem vazia");

        verifyNoInteractions(s3Client);
    }

    @Test
    void shouldRejectEmptyPhoto() {

        MultipartFile photo = mock(MultipartFile.class);

        when(photo.isEmpty()).thenReturn(true);

        assertThatThrownBy(() ->
                service.uploadProfilePicture(photo)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Imagem vazia");

        verifyNoInteractions(s3Client);
    }

    @Test
    void shouldRejectPhotoLargerThan5MB() {

        MultipartFile photo = mock(MultipartFile.class);

        when(photo.getSize())
                .thenReturn(5L * 1024 * 1024 + 1);

        assertThatThrownBy(() ->
                service.uploadProfilePicture(photo)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Imagem maior que 5 MB");

        verifyNoInteractions(s3Client);
    }

    @Test
    void shouldAcceptPhotoWithExactly5MB() throws IOException {

        MultipartFile photo = mock(MultipartFile.class);

        byte[] content = new byte[5 * 1024 * 1024];

        when(photo.getSize())
                .thenReturn((long) content.length);

        when(photo.getContentType())
                .thenReturn("image/png");

        when(photo.getBytes())
                .thenReturn(content);

        String key = service.uploadProfilePicture(photo);

        assertThat(key).startsWith("profiles/")
                .endsWith(".png");

        verify(s3Client).putObject(
                any(PutObjectRequest.class),
                any(RequestBody.class)
        );
    }

    @Test
    void shouldRejectInvalidContentType() {

        MultipartFile photo = mock(MultipartFile.class);

        when(photo.getContentType())
                .thenReturn("application/pdf");

        assertThatThrownBy(() ->
                service.uploadProfilePicture(photo)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Formato inválido");

        verifyNoInteractions(s3Client);
    }

    @Test
    void shouldRejectNullContentType() {

        MultipartFile photo = mock(MultipartFile.class);

        when(photo.getContentType()).thenReturn(null);

        assertThatThrownBy(() ->
                service.uploadProfilePicture(photo)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Formato inválido");

        verifyNoInteractions(s3Client);
    }

    @Test
    void shouldHandlePhotoReadFailure() throws IOException {

        MultipartFile photo = mock(MultipartFile.class);

        when(photo.getContentType())
                .thenReturn("image/png");

        when(photo.getBytes())
                .thenThrow(new IOException("Erro de leitura"));

        assertThatThrownBy(() ->
                service.uploadProfilePicture(photo)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessage("Erro ao ler a imagem")
                .hasCauseInstanceOf(IOException.class);

        verifyNoInteractions(s3Client);
    }

    private void assertSuccessfulUpload(
            String contentType,
            String extension
    ) throws IOException {

        MultipartFile photo = mock(MultipartFile.class);

        when(photo.getContentType())
                .thenReturn(contentType);

        when(photo.getBytes())
                .thenReturn(new byte[]{1, 2, 3});

        String key = service.uploadProfilePicture(photo);

        assertThat(key)
                .startsWith("profiles/")
                .endsWith("." + extension);

        verify(s3Client).putObject(
                argThat((PutObjectRequest request) ->
                        request.bucket().equals("test-bucket")
                                && request.key().equals(key)
                                && request.contentType().equals(contentType)
                ),
                any(RequestBody.class)
        );
    }
}