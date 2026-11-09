package com.memoriumm.entities;

import com.memoriumm.enums.TagType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(
        name = "tags",
        uniqueConstraints = {
            @UniqueConstraint(
                    columnNames = {"user_id", "name"}
            )
        }
    )
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    @Column(length = 10)
    private String icon;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;
    @Column(nullable = false)
    @NotBlank
    private String name;
    @Enumerated(EnumType.STRING)
    private TagType visibility;

}
