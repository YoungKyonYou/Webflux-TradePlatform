package com.youyk.aggregatorservice.service;

import com.youyk.aggregatorservice.client.CustomerServiceClient;
import com.youyk.aggregatorservice.client.StockServiceClient;
import com.youyk.aggregatorservice.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
@RequiredArgsConstructor
@Service
public class CustomerPortfolioService {

    private final StockServiceClient stockServiceClient;
    private final CustomerServiceClient customerServiceClient;

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId){
        return this.customerServiceClient.getCustomerInformation(customerId);
    }

    public Mono<StockTradeResponse> trade(Integer customerId, TradeRequest request){
        return this.stockServiceClient.getStockPrice(request.ticker())
                .map(StockPriceResponse::price)
                .map(price -> this.toStockTradeRequest(request, price))
                .flatMap(req -> this.customerServiceClient.trade(customerId, req));

    }

    private StockTradeRequest toStockTradeRequest(TradeRequest request, Integer price){
        return new StockTradeRequest(
                request.ticker(),
                price,
                request.quantity(),
                request.action()
        );
    }
}
