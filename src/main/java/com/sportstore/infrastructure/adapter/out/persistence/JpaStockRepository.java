package com.sportstore.infrastructure.adapter.out.persistence;

import com.sportstore.application.port.out.ArticleStorageException;
import com.sportstore.application.port.out.StockRepository;
import com.sportstore.domain.model.ArticleId;
import com.sportstore.domain.model.Stock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaStockRepository implements StockRepository {

    private final StockSpringDataRepository springDataRepository;
    private final StockPersistenceMapper mapper;

    public JpaStockRepository(StockSpringDataRepository springDataRepository, StockPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Stock> findByArticleId(ArticleId articleId) {
        try {
            return springDataRepository.findById(articleId.value())
                    .map(mapper::toDomain);
        } catch (Exception e) {
            throw new ArticleStorageException("Error finding stock by article id", e);
        }
    }

    @Override
    public List<Stock> findAll() {
        try {
            return springDataRepository.findAll().stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (Exception e) {
            throw new ArticleStorageException("Error reading stocks", e);
        }
    }

    @Override
    public Stock save(Stock stock) {
        try {
            StockJpaEntity entity = mapper.toEntity(stock);
            StockJpaEntity saved = springDataRepository.save(entity);
            return mapper.toDomain(saved);
        } catch (Exception e) {
            throw new ArticleStorageException("Error saving stock", e);
        }
    }

    @Override
    public void delete(ArticleId articleId) {
        try {
            springDataRepository.deleteById(articleId.value());
        } catch (Exception e) {
            throw new ArticleStorageException("Error deleting stock", e);
        }
    }
}
