package com.youyk.customerservice.entity;

import com.youyk.customerservice.domain.Ticker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PortfolioItem {
    @Id
    private Integer id;
    private Integer customerId;
    private Ticker ticker;
    private Integer quantity;
}
