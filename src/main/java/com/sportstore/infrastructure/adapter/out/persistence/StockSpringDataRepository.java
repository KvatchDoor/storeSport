package com.sportstore.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface StockSpringDataRepository extends JpaRepository<StockJpaEntity, UUID> {
}
