package io.aios.kernel.repository;

import io.aios.kernel.model.AiTool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ToolRepository extends JpaRepository<AiTool, UUID> {
}
