package com.youyk.aggregatorservice.dto;


import com.youyk.aggregatorservice.domain.Ticker;
import com.youyk.aggregatorservice.domain.TradeAction;

public record StockTradeRequest(
        Ticker ticker,
        Integer price,
        Integer quantity,
        TradeAction action
) {
    public Integer totalPrice(){
        return price * quantity;
    }
}
