package com.youyk.aggregatorservice.controller;

import com.youyk.aggregatorservice.dto.CustomerInformation;
import com.youyk.aggregatorservice.dto.StockTradeResponse;
import com.youyk.aggregatorservice.dto.TradeRequest;
import com.youyk.aggregatorservice.service.CustomerPortfolioService;
import com.youyk.aggregatorservice.validator.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("customers")
public class CustomerPortfolioController {

    private final CustomerPortfolioService customerPortfolioService;

    @GetMapping("/{customerId}")
    public Mono<CustomerInformation> getCustomerInformation(Integer customerId){
        return this.customerPortfolioService.getCustomerInformation(customerId);
    }

    @PostMapping("/{customerId}/trade")
    public Mono<StockTradeResponse> trade(@PathVariable("customerId") Integer customerId, @RequestBody Mono<TradeRequest> mono){
        return mono.transform(RequestValidator.validate())
                .flatMap(req -> this.customerPortfolioService.trade(customerId, req));
    }
}
