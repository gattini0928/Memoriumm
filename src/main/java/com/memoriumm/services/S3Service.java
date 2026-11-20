
package com.memoriumm.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucket;

    public S3Service(
            S3Client s3Client,
            @Value("${AWS_S3_BUCKET}") String bucket
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public String uploadProfilePicture(MultipartFile photo) {

        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Imagem vazia");
        }

        if (photo.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Imagem maior que 5 MB");
        }

        String contentType = photo.getContentType();

        if (contentType == null || !Set.of(
                "image/jpeg",
                "image/png",
                "image/webp"
        ).contains(contentType)) {
            throw new IllegalArgumentException("Formato inválido");
        }

        String extension = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new IllegalArgumentException("Formato inválido");
        };

        String key = "profiles/" + UUID.randomUUID() + "." + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(photo.getBytes())
            );

            return key;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao ler a imagem", e
            );
        }
    }
}
