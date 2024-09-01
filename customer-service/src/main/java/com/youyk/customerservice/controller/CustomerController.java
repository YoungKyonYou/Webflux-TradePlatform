package com.youyk.customerservice.controller;

import com.youyk.customerservice.dto.CustomerInformation;
import com.youyk.customerservice.dto.StockTradeRequest;
import com.youyk.customerservice.dto.StockTradeResponse;
import com.youyk.customerservice.service.CustomerService;
import com.youyk.customerservice.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@RequestMapping("customers")
@RestController
public class CustomerController {
    private final CustomerService customerService;
    private final TradeService tradeService;

    @GetMapping("/{customerId}")
    public Mono<CustomerInformation> getCustomerInformation(@PathVariable("customerId") Integer customerId){
        return this.customerService.getCustomerInformation(customerId);

    }

    /**
     * RequestBody Mono<StockTradeRequest> mono를 사용하는 이유는 Spring WebFlux가 비동기 및 논블로킹 모델을 사용하기 때문입니다.
     * 이 모델에서, HTTP 요청과 응답은 Mono나 Flux와 같은 리액티브 타입으로 처리됩니다. 이렇게 하면 서버가 요청을 처리하는 동안 다른 작업을 수행할 수 있어 효율성이 향상됩니다.
     * Mono<StockTradeRequest>는 HTTP 요청 본문을 StockTradeRequest 객체로 비동기적으로 변환하는 작업을 나타냅니다.
     * 이 변환 작업이 완료되면 Mono가 완료되고 결과 StockTradeRequest 객체가 방출됩니다.
     * flatMap 연산자는 이 Mono가 완료되기를 기다린 후, 결과 StockTradeRequest 객체를 사용하여 tradeService.trade(customerId, req)를 호출합니다.
     * 이 호출은 또 다른 Mono를 반환하며, 이 Mono는 최종 HTTP 응답을 나타냅니다.
     * 만약 @RequestBody StockTradeRequest request와 같이 Mono를 사용하지 않고 요청 본문을 직접 받았다면, 요청 본문의 변환 작업이 블로킹되어 서버의 효율성이 저하될 수 있습니다.
     * 또한, tradeService.trade(customerId, request) 호출은 비동기적으로 수행되지 않을 수 있습니다. 이는 Spring WebFlux의 비동기 및 논블로킹 모델과는 맞지 않습니다.
     * 따라서 Mono를 사용하여 요청 본문을 처리하고 flatMap을 사용하여 비동기적으로 tradeService.trade(customerId, req)를 호출하는 것이 좋습니다.
     */
    @PostMapping("/{customerId}/trade")
    public Mono<StockTradeResponse> trade(@PathVariable("customerId") Integer customerId, @RequestBody Mono<StockTradeRequest> mono){
        return mono.flatMap(req -> this.tradeService.trade(customerId, req));

    }
}
