package com.sportstore.infrastructure.adapter.out.persistence;

import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.Quantity;
import com.sportstore.domain.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockPersistenceMapper {

    public Stock toDomain(StockJpaEntity entity) {
        return new Stock(
                new ArticleId(entity.getArticleId()),
                new Quantity(entity.getQuantity())
        );
    }

    public StockJpaEntity toEntity(Stock stock) {
        return new StockJpaEntity(stock.articleId().value(), stock.quantity().value());
    }
}
