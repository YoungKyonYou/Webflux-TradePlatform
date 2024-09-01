package com.youyk.customerservice.service;

import com.youyk.customerservice.domain.Ticker;
import com.youyk.customerservice.dto.StockTradeRequest;
import com.youyk.customerservice.dto.StockTradeResponse;
import com.youyk.customerservice.entity.Customer;
import com.youyk.customerservice.entity.PortfolioItem;
import com.youyk.customerservice.exceptions.ApplicationExceptions;
import com.youyk.customerservice.mapper.EntityDtoMapper;
import com.youyk.customerservice.repository.CustomerRepository;
import com.youyk.customerservice.repository.PortfolioItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class TradeService {
    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    @Transactional
    public Mono<StockTradeResponse> trade(Integer customerId, StockTradeRequest request) {
        return switch (request.action()) {
            case BUY -> this.buyStock(customerId, request);
            case SELL -> this.sellStock(customerId, request);
        };
    }

    private Mono<StockTradeResponse> sellStock(Integer customerId, StockTradeRequest request) {
        Mono<Customer> customerMono = this.customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId));

        Mono<PortfolioItem> portfolioItemMono = this.portfolioItemRepository.findByCustomerIdAndTicker(customerId,
                        request.ticker()).filter(pi -> pi.getQuantity() >= request.quantity())
                .switchIfEmpty(ApplicationExceptions.insufficientShares(customerId));

        //이건 zip으로 하지 않을 것이다. 왜먀녀 만약 customerMono에서 customer를 찾지 못했을 때를 대비해서 sequantial하게 실행되어야 하기 때문이다.
        //그래서 zip를 사용하지 않고 sequantial하게 실행되도록 할 것이다.
        //Mono.zip(customerMono, portfolioItemMono)

        //zipWhen은 customer가 find 되면 portfolioItem을 찾는다.(sequantial하게 실행된다.)
        return customerMono.zipWhen(customer -> portfolioItemMono)
                //여기서 t는 customer하고 portfolioItem이다.
                .flatMap(t -> this.executeSell(t.getT1(), t.getT2(), request));
    }

    private Mono<StockTradeResponse> buyStock(Integer customerId, StockTradeRequest request) {
        Mono<Customer> customerMono = this.customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .filter(c -> c.getBalance() >= request.totalPrice())
                .switchIfEmpty(ApplicationExceptions.insufficientBalance(customerId));

        Mono<PortfolioItem> portfolioItemMono = this.portfolioItemRepository.findByCustomerIdAndTicker(customerId,
                request.ticker()).defaultIfEmpty(EntityDtoMapper.toPortfolioItem(customerId, request.ticker()));

        //이건 zip으로 하지 않을 것이다. 왜먀녀 만약 customerMono에서 customer를 찾지 못했을 때를 대비해서 sequantial하게 실행되어야 하기 때문이다.
        //그래서 zip를 사용하지 않고 sequantial하게 실행되도록 할 것이다.
        //Mono.zip(customerMono, portfolioItemMono)

        //zipWhen은 customer가 find 되면 portfolioItem을 찾는다.(sequantial하게 실행된다.)
        return customerMono.zipWhen(customer -> portfolioItemMono)
                //여기서 t는 customer하고 portfolioItem이다.
                .flatMap(t -> this.executeBuy(t.getT1(), t.getT2(), request));


    }


    private Mono<StockTradeResponse> executeBuy(Customer customer, PortfolioItem portfolioItem,
                                                StockTradeRequest request) {
        customer.setBalance(customer.getBalance() - request.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() + request.quantity());
        StockTradeResponse response = EntityDtoMapper.toStockTradeResponse(request, customer.getId(),
                customer.getBalance());
        //mono.zip은 두개의 publisher를 합쳐서 하나의 publisher로 만들어준다.
        // parallel하게 실행된다
        // save하기 전에 response를 만드는 게 맞냐라고 질문할 수도 있는데 그럴 땐 thenReturn 대신 map 를 사용해서 response를 만들면 된다.
        // 그래서 아래와 같이 한다
      /*  return Mono.zip(this.customerRepository.save(customer), this.portfolioItemRepository.save(portfolioItem))
                .thenReturn(response);*/

        return this.saveAndBuildResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> executeSell(Customer customer, PortfolioItem portfolioItem,
                                                StockTradeRequest request) {
        customer.setBalance(customer.getBalance() + request.totalPrice());
        portfolioItem.setQuantity(portfolioItem.getQuantity() - request.quantity());
        StockTradeResponse response = EntityDtoMapper.toStockTradeResponse(request, customer.getId(),
                customer.getBalance());
        //mono.zip은 두개의 publisher를 합쳐서 하나의 publisher로 만들어준다.
        // parallel하게 실행된다
        // save하기 전에 response를 만드는 게 맞냐라고 질문할 수도 있는데 그럴 땐 thenReturn 대신 map 를 사용해서 response를 만들면 된다.
        return this.saveAndBuildResponse(customer, portfolioItem, request);
    }

    private Mono<StockTradeResponse> saveAndBuildResponse(Customer customer, PortfolioItem portfolioItem,
                                                 StockTradeRequest request) {
        StockTradeResponse response = EntityDtoMapper.toStockTradeResponse(request, customer.getId(),
                customer.getBalance());

        return Mono.zip(this.customerRepository.save(customer), this.portfolioItemRepository.save(portfolioItem))
                .thenReturn(response);
    }


}
