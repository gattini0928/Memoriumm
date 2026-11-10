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
@Table(name = "memories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID uuid;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, length = 100)
    @NotBlank
    private String title;
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String photo;
    @Column(nullable = false, length = 1000)
    @NotBlank
    private String description;
    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private PrivacyTypes visibility;
    private boolean pinned;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "memory_likes",
            joinColumns = @JoinColumn(name = "memory_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likes;

    @ManyToMany
    @JoinTable(
            name = "memory_autumn_hypes",
            joinColumns = @JoinColumn(name = "memory_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> autumnHypes;

    @ManyToMany
    @JoinTable(
            name = "memory_tags",
            joinColumns = @JoinColumn(name = "memory_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @OneToMany(mappedBy = "memory")
    private Set<Comment> comments;

}
