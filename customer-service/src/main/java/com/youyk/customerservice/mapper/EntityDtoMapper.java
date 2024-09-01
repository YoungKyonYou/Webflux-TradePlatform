package com.youyk.customerservice.mapper;

import com.youyk.customerservice.domain.Ticker;
import com.youyk.customerservice.domain.TradeAction;
import com.youyk.customerservice.dto.CustomerInformation;
import com.youyk.customerservice.dto.Holding;
import com.youyk.customerservice.dto.StockTradeRequest;
import com.youyk.customerservice.dto.StockTradeResponse;
import com.youyk.customerservice.entity.Customer;
import com.youyk.customerservice.entity.PortfolioItem;
import java.util.List;

public class EntityDtoMapper {
    public static CustomerInformation toCustomerInformation(Customer customer, List<PortfolioItem> items){
        List<Holding> holdings = items.stream()
                .map(i -> Holding.builder()
                        .ticker(i.getTicker())
                        .quantity(i.getQuantity())
                        .build()
                ).toList();

        return CustomerInformation.builder()
                .id(customer.getId())
                .name(customer.getName())
                .balance(customer.getBalance())
                .holdings(holdings)
                .build();
    }

    public static PortfolioItem toPortfolioItem(Integer customerId, Ticker ticker){
        return PortfolioItem.builder()
                .customerId(customerId)
                .ticker(ticker)
                //처음 만드는 거니까 0
                .quantity(0)
                .build();
    }

    public static StockTradeResponse toStockTradeResponse(StockTradeRequest request, Integer customerId, Integer balance){
        return StockTradeResponse.builder()
                .customerId(customerId)
                .ticker(request.ticker())
                .price(request.price())
                .quantity(request.quantity())
                .action(request.action())
                .totalPrice(request.totalPrice())
                .balance(balance)
                .build();
    }
}
