package io.aios.kernel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "ai_tool")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTool {

    @Id
    private UUID id;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "json_schema", columnDefinition = "TEXT")
    private String jsonSchema;

    @Column(name = "usage_count")
    private int usageCount;

    @Column(name = "is_verified")
    private boolean isVerified;
}
