package com.alibaba.cloud.ai.rag.parallel.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;

import java.util.Map;
import java.util.Objects;

/**
 * @Author NGshiyu
 * @Description 定制化知识文档模型
 * @CreateTime 2026/2/3 16:43
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class KnowledgeBaseDocument extends Document {
    private String workspaceId;
    private String indexName;
    private Double score;
    private Double rerankScore;
    private Boolean structure = false;

    public KnowledgeBaseDocument(String content) {
        super(content);
    }

    public KnowledgeBaseDocument(String text, Map<String, Object> metadata) {
        super(text, metadata);
    }

    public KnowledgeBaseDocument(String id, String text, Map<String, Object> metadata) {
        super(id, text, metadata);
    }

    public KnowledgeBaseDocument(Media media, Map<String, Object> metadata) {
        super(media, metadata);
    }

    public KnowledgeBaseDocument(String id, Media media, Map<String, Object> metadata) {
        super(id, media, metadata);
    }

    public KnowledgeBaseDocument(String id, Media media, Map<String, Object> metadata, Double score) {
        super(id, media, metadata);
        this.score = score;
    }

    public KnowledgeBaseDocument(String id, String text, Map<String, Object> metadata, Double score) {
        super(id, text, metadata);
        this.score = score;
    }

    public Double getRerankScore() {
        if (Objects.isNull(this.rerankScore)) {
            return this.score;
        }
        return this.rerankScore;
    }
}


