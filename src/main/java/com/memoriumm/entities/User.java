package com.memoriumm.entities;

import com.memoriumm.enums.PrivacyTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID uuid;
    @Column(nullable = false, length = 100)
    @NotBlank
    private String name;
    @Column(nullable = false)
    @NotBlank
    private String email;
    @Column(nullable = false, length = 100)
    @NotBlank
    private String username;
    @Column(nullable = false)
    @NotBlank
    private String password;
    @Column(columnDefinition = "TEXT")
    private String profilePicture;
    @Column(length = 300)
    private String bio;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private PrivacyTypes privacy;

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers;

    @ManyToMany(mappedBy = "followers")
    private Set<User> following;

    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "memory_id")
    )
    private Set<Memory> favorites;

}
