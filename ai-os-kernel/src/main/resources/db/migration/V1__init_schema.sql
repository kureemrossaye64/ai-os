CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE ai_tool (
    id UUID PRIMARY KEY,
    class_name VARCHAR(255) NOT NULL,
    source_code TEXT NOT NULL,
    description TEXT,
    json_schema TEXT,
    usage_count INT DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE
);

CREATE TABLE tool_embeddings (
    id UUID PRIMARY KEY,
    embedding VECTOR(384),
    text TEXT,
    metadata JSONB
);
