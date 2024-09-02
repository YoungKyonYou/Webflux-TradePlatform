package com.youyk.aggregatorservice.controller;

import com.youyk.aggregatorservice.client.StockServiceClient;
import com.youyk.aggregatorservice.dto.PriceUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RequestMapping("stock")
@RestController
public class StockPriceStreamController {
    private final StockServiceClient stockServiceClient;

    @GetMapping(value  ="/price-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PriceUpdate> priceUpdatesStream(){
        return this.stockServiceClient.priceUpdatesStream();
    }
}
