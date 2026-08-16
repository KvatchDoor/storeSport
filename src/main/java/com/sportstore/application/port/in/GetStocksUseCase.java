package com.sportstore.application.port.in;

import com.sportstore.domain.model.Stock;

import java.util.List;

public interface GetStocksUseCase {

    List<Stock> getAll();
}
