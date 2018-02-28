package com.example.zuulproxy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class RetryTest {

    private static final String HOST = "http://localhost:";

    private static final String USER_SERVICE = "/user-service/users";

    private static final int port = 9081;

    private static final String URL = HOST + port + USER_SERVICE;

    private static final String GET_MSG = "this one works";

    private static final String POST_MSG = "[\"this one fails\"]";

    private final RestTemplate template = new RestTemplate();

    private Logger logger = LoggerFactory.getLogger(RetryTest.class);

    @Test
    public void getFor1min_shouldRetryIfInstanceFails() {
        sendMany(1, 20, this::sendGetRequest);
    }

    @Test
    public void postFor1min_shouldRetryIfInstanceFails() {
        sendMany(1, 20, this::sendPostRequest);
    }

    @Test
    public void get_shouldRetryIfInstanceFails() {
        String response = template.getForObject(URL, String.class);
        assertThat(response).isEqualTo(GET_MSG);
    }

    @Test
    public void post_shouldRetryIfInstanceFails() {
        ResponseEntity<String> response = template.postForEntity(URL, null, String.class);
        assertThat(response).extracting(HttpEntity::getBody)
                            .extracting(Object::toString)
                            .isEqualTo("[\"this one fails\"]");
    }

    @Test
    public void put_shouldRetryIfInstanceFails() {
        template.put(URL, null);
    }

    private void sendMany(long intervalSeconds, int duration, Function<Long, String> sendFunc) {
        Flux<Long> sample = Flux.interval(Duration.ofSeconds(intervalSeconds))
                                .doOnNext(i -> {
                                    if (i == Math.floor(duration)) {
                                        forceFailure(10);
                                    }
                                })
                                .doOnNext(sendFunc::apply)
                                .takeWhile(i -> i < duration);
        StepVerifier.create(sample)
                    .expectNextCount(duration)
                    .verifyComplete();
    }

    private String sendGetRequest(Long i) {
        logger.info("sending get request number {}", i);
        return template.getForObject(URL, String.class);
    }

    private String sendPostRequest(Long i) {
        logger.info("sending post request number {}", i);
        return template.postForEntity(URL, null, String.class).getBody();
    }

    private void forceFailure(int duration) {
        template.getForObject(URL + "/fail/" + duration, String.class);
    }
}
