package com.youyk.aggregatorservice.validator;
import com.youyk.aggregatorservice.exception.ApplicationExceptions;
import reactor.core.publisher.Mono;
import com.youyk.aggregatorservice.dto.TradeRequest;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {

    public static UnaryOperator<Mono<TradeRequest>> validate(){

        return mono -> mono.filter(hasTicker())
                .switchIfEmpty(ApplicationExceptions.missingTicker())
                .filter(hasTradeAction())
                .switchIfEmpty(ApplicationExceptions.missingTradeAction())
                .filter(isValidQuantity())
                .switchIfEmpty(ApplicationExceptions.invalidQuantity());
    }

    private static Predicate<TradeRequest> hasTicker(){
        return dto -> Objects.nonNull(dto.ticker());
    }

    private static Predicate<TradeRequest> hasTradeAction(){
        return dto -> Objects.nonNull(dto.action());
    }

    private static Predicate<TradeRequest> isValidQuantity(){
        return dto -> Objects.nonNull(dto.quantity()) && dto.quantity() >0;
    }
}
