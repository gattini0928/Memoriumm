package com.memoriumm.repositories;

import com.memoriumm.entities.Memory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemoryRepository extends JpaRepository<Memory, UUID> {
}
