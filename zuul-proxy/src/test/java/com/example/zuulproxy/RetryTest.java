package com.example.zuulproxy;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class RetryTest {

    private static final String HOST = "http://localhost:";

    private static final String USER_SERVICE = "/user-service/users";

    private final RestTemplate template = new RestTemplate();

    @Value("${server.port:9081}")
    int port;

    @Test
    public void sendGetRequestToUserService_shouldRetryIfInstanceFails() {
        String message = template.getForObject(HOST + port + USER_SERVICE, String.class);
        assertThat(message).isEqualTo("this one works");
    }
}
