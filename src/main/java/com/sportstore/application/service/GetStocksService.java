package com.sportstore.application.service;

import com.sportstore.application.port.in.GetStocksUseCase;
import com.sportstore.application.port.out.StockRepository;
import com.sportstore.domain.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetStocksService implements GetStocksUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetStocksService.class);

    private final StockRepository stockRepository;

    public GetStocksService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<Stock> getAll() {
        List<Stock> stocks = stockRepository.findAll();
        log.debug("Consultation des stocks - {} articles trouvés", stocks.size());
        return stocks;
    }
}
