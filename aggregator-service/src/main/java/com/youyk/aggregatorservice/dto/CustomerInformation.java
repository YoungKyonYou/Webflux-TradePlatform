package com.youyk.aggregatorservice.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CustomerInformation(
        Integer id,
        String name,
        Integer balance,
        List<Holding> holdings
) {
}
