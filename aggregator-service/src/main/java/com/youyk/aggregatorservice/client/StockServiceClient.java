package com.youyk.aggregatorservice.client;

import com.youyk.aggregatorservice.domain.Ticker;
import com.youyk.aggregatorservice.dto.PriceUpdate;
import com.youyk.aggregatorservice.dto.StockPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class StockServiceClient {
    private final WebClient client;

    public StockServiceClient(WebClient client) {
        this.client = client;
    }

    private Flux<PriceUpdate> flux;

    //stock service client - stock-service 프로젝트에서 가져옴
    public Mono<StockPriceResponse> getStockPrice(Ticker ticker){
        return this.client.get()
                .uri("/stock/{ticker}", ticker)
                .retrieve()
                .bodyToMono(StockPriceResponse.class);
    }

    public Flux<PriceUpdate> priceUpdatesStream(){
        if(Objects.isNull(this.flux)){
            //이 flux는 최초 한번만 초기화
            this.flux = this.getPriceUpdates();
        }
        //singleton으로 생성된 flux를 반환
        return this.flux;
    }

    //stock service client - stock-service 프로젝트에서 가져옴
    //아래 publisher는 한번만 create 되야 하기 때문에 private로 선언
    private Flux<PriceUpdate> getPriceUpdates(){
        return this.client.get()
                .uri("/stock/price-stream")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(PriceUpdate.class)
                .retryWhen(retry())
                //이 연산자는 Flux의 데이터를 캐시에 저장하고, 이후에 같은 Flux를 구독할 때 캐시된 데이터를 재사용합니다.
                //여기서는 가장 최근의 1개의 데이터만 캐시에 저장하고, 이후에 같은 Flux를 구독할 때 이 캐시된 데이터를 재사용하게 됩니다.
                .cache(1);
    }

    private Retry retry(){
        //여기서는 1초 간격으로 최대 100번 재시도하도록 설정되어 있습니다.
        return Retry.fixedDelay(100, Duration.ofSeconds(1))
                .doBeforeRetry(rs -> log.info("stock service price stream call failed.", rs.failure().getMessage()));
    }
}
