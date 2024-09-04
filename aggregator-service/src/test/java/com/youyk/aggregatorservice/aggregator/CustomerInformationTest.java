package com.youyk.aggregatorservice.aggregator;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
public class CustomerInformationTest extends AbstractIntegrationTest{

    @Test
    public void customerInformation() throws IOException {

    }

    @Test
    public void customerNotFound(){
        // when
        mockCustomerInformation("customer-service/customer-information-404.json", 404);

        //then
        getCustomerInformation(HttpStatus.NOT_FOUND)
                .jsonPath("$.detail").isEqualTo("Customer [id=1] is not found")
                .jsonPath("$.title").isNotEmpty();
    }

    private void mockCustomerInformation(String path, int responseCode){
        // mock customer service
        String responseBody = this.resourceToString(path);
        mockServerClient
                //이 부분은 MockServerClient에게 "/customers/1" 경로로 들어오는 HTTP 요청을 감지하도록 지시
                //감지된 요청에 대해 어떻게 응답할지를 설정합니다. 여기서는 HTTP 상태 코드 200과 함께, responseBody라는 변수에 저장된 JSON 형식의 응답 본문을 반환하도록 설정하고 있습니다.
                .when(HttpRequest.request("/customers/1"))
                .respond(
                        HttpResponse.response(responseBody)
                                .withStatusCode(responseCode)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private WebTestClient.BodyContentSpec getCustomerInformation(HttpStatus expectedStatus){
        return this.client.get()
                .uri("/customers/1")
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(e.getResponseBody())));
    }
}
