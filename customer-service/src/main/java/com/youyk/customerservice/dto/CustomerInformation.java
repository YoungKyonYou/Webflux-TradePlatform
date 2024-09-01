package com.youyk.customerservice.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record CustomerInformation(
        Integer id,
        String name,
        Integer balance,
        List<Holding> holdings
) {
}
