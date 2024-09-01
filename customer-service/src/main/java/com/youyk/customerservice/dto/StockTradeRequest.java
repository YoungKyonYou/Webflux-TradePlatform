package com.youyk.customerservice.dto;

import com.youyk.customerservice.domain.Ticker;
import com.youyk.customerservice.domain.TradeAction;

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
