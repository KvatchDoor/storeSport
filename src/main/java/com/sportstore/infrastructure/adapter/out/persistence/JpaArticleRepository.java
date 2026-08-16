package com.sportstore.infrastructure.adapter.out.persistence;

import com.sportstore.application.port.out.ArticleRepository;
import com.sportstore.application.port.out.ArticleStorageException;
import com.sportstore.domain.model.Article;
import com.sportstore.domain.model.ArticleName;
import com.sportstore.domain.model.Category;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaArticleRepository implements ArticleRepository {

    private final ArticleSpringDataRepository springDataRepository;
    private final ArticlePersistenceMapper mapper;

    JpaArticleRepository(ArticleSpringDataRepository springDataRepository, ArticlePersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Article> findAll() {
        try {
            return springDataRepository.findAllByOrderByNameAsc().stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DataAccessException e) {
            throw new ArticleStorageException("Lecture du catalogue impossible", e);
        }
    }

    @Override
    public List<ArticleName> findAllNames() {
        try {
            return springDataRepository.findAllNamesOrderByNameAsc().stream()
                    .map(mapper::toArticleName)
                    .toList();
        } catch (DataAccessException e) {
            throw new ArticleStorageException("Lecture des noms d'articles impossible", e);
        }
    }

    @Override
    public List<Article> findByCategory(Category category) {
        try {
            return springDataRepository.findByCategoryOrderByNameAsc(category.value()).stream()
                    .map(mapper::toDomain)
                    .toList();
        } catch (DataAccessException e) {
            throw new ArticleStorageException("Lecture de la categorie " + category.value() + " impossible", e);
        }
    }

    @Override
    public Optional<Article> findByName(ArticleName name) {
        try {
            return springDataRepository.findByName(name.value()).map(mapper::toDomain);
        } catch (DataAccessException e) {
            throw new ArticleStorageException("Lecture de l'article " + name.value() + " impossible", e);
        }
    }

    @Override
    public Article save(Article article) {
        try {
            ArticleJpaEntity entity = springDataRepository.findById(article.id().value())
                    .map(existing -> mapper.updateEntity(existing, article))
                    .orElseGet(() -> mapper.toEntity(article));

            return mapper.toDomain(springDataRepository.save(entity));
        } catch (DataAccessException e) {
            throw new ArticleStorageException("Enregistrement de l'article " + article.name().value() + " impossible", e);
        }
    }

    @Override
    public void delete(Article article) {
        try {
            springDataRepository.deleteById(article.id().value());
        } catch (DataAccessException e) {
            throw new ArticleStorageException("Suppression de l'article " + article.name().value() + " impossible", e);
        }
    }
}
