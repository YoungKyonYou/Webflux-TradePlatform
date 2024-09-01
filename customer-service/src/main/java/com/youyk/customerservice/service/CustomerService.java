package com.youyk.customerservice.service;

import com.youyk.customerservice.dto.CustomerInformation;
import com.youyk.customerservice.entity.Customer;
import com.youyk.customerservice.exceptions.ApplicationExceptions;
import com.youyk.customerservice.mapper.EntityDtoMapper;
import com.youyk.customerservice.repository.CustomerRepository;
import com.youyk.customerservice.repository.PortfolioItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PortfolioItemRepository portfolioItemRepository;

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId){
        return this.customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .flatMap(this::buildCustomerInformation);
    }

    private Mono<CustomerInformation> buildCustomerInformation(Customer customer){
        return this.portfolioItemRepository.findAllByCustomerId(customer.getId())
                .collectList()
                .map(list -> EntityDtoMapper.toCustomerInformation(customer, list));
    }
}
