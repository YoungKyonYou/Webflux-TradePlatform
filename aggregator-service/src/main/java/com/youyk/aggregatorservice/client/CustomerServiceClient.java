package com.youyk.aggregatorservice.client;

import com.youyk.aggregatorservice.dto.CustomerInformation;
import com.youyk.aggregatorservice.dto.StockTradeRequest;
import com.youyk.aggregatorservice.dto.StockTradeResponse;
import com.youyk.aggregatorservice.exception.ApplicationExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class CustomerServiceClient {
    private final WebClient client;

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId){
        return this.client.get()
                .uri("/customers/{customerId}", customerId)
                .retrieve()
                .bodyToMono(CustomerInformation.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> ApplicationExceptions.customerNotFound(customerId));
    }

    public Mono<StockTradeResponse> trade(Integer customerId, StockTradeRequest request){
        return this.client.post()
                .uri("/customers/{customerId}/trade", customerId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(StockTradeResponse.class)
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> ApplicationExceptions.customerNotFound(customerId))
                .onErrorResume(WebClientResponseException.BadRequest.class, this::handleException);
    }

    private <T> Mono<T> handleException(WebClientResponseException.BadRequest exception){
        ProblemDetail pd = exception.getResponseBodyAs(ProblemDetail.class);
        String message = Objects.nonNull(pd) ? pd.getDetail() : exception.getMessage();
        log.error("customer service problem detail : {}", pd);
        return ApplicationExceptions.invalidTradeRequest(message);
    }
}
