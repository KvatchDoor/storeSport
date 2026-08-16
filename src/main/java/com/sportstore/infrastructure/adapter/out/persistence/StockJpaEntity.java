package com.sportstore.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock")
public class StockJpaEntity {

    @Id
    @Column(name = "article_id", nullable = false, updatable = false)
    private UUID articleId;

    @Column(name = "quantity", nullable = false)
    private long quantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne
    @JoinColumn(name = "article_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_stock_article"))
    private ArticleJpaEntity article;

    protected StockJpaEntity() {
    }

    public StockJpaEntity(UUID articleId, long quantity) {
        this.articleId = articleId;
        this.quantity = quantity;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getArticleId() {
        return articleId;
    }

    public long getQuantity() {
        return quantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
