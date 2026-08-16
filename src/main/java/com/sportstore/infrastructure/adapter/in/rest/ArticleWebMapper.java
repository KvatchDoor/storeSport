package com.sportstore.infrastructure.adapter.in.rest;

import com.sportstore.application.port.in.UpsertArticleCommand;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.ArticleStock;
import com.sportstore.domain.model.Category;
import com.sportstore.domain.model.Price;
import com.sportstore.infrastructure.adapter.in.rest.dto.ArticleResponse;
import com.sportstore.infrastructure.adapter.in.rest.dto.StockResponse;
import com.sportstore.infrastructure.adapter.in.rest.dto.UpsertArticleRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ArticleWebMapper {

    @Mapping(target = "stock", source = "stock.quantity")
    ArticleResponse toResponse(Article article);

    @BeanMapping(unmappedSourcePolicy = ReportingPolicy.ERROR)
    UpsertArticleCommand toCommand(UpsertArticleRequest request);

    default ArticleName toArticleName(String name) {
        return new ArticleName(name);
    }

    default Category toCategory(String category) {
        return new Category(category);
    }

    default Price toPrice(BigDecimal price) {
        return new Price(price);
    }

    default UUID fromArticleId(ArticleId id) {
        return id.value();
    }

    default String fromArticleName(ArticleName name) {
        return name.value();
    }

    default String fromCategory(Category category) {
        return category.value();
    }

    default BigDecimal fromPrice(Price price) {
        return price.amount();
    }

    default StockResponse toStockResponse(ArticleStock stock) {
        return new StockResponse(
                fromArticleId(stock.articleId()),
                fromArticleName(stock.articleName()),
                stock.stock().quantity()
        );
    }
}
