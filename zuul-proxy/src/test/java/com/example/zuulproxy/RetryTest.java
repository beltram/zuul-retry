package com.example.zuulproxy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static java.time.Duration.ofSeconds;

public class RetryTest {

    private static final String HOST = "http://localhost:";

    private static final String USER_SERVICE = "/user-service/users";

    private static final int port = 9081;

    private static final String URL = HOST + port + USER_SERVICE + "/";

    private final RestTemplate template = new RestTemplate();

    private Logger logger = LoggerFactory.getLogger(RetryTest.class);

    @Test
    public void getFor1min_shouldRetryIfInstanceFails() {
        sendMany(1, 20, 10, this::sendGetRequest);
    }

    @Test
    public void postFor1min_shouldRetryIfInstanceFails() {
        sendMany(2, 30, 10, this::sendPostRequest);
    }

    private void sendMany(long intervalSeconds, int duration, int failureDuring, Function<Long, String> sendFunc) {
        long requestNb = duration / intervalSeconds;
        Flux<String> sample = Flux.interval(ofSeconds(intervalSeconds))
                                  .doOnNext(i -> forceFailureWhen(i, duration/2, failureDuring))
                                  .takeWhile(i -> i < requestNb)
                                  .map(sendFunc);
        StepVerifier.create(sample)
                    .expectSubscription()
                    .expectNextCount(requestNb)
                    .verifyComplete();
    }

    private void forceFailureWhen(Long i, int duration, int during) {
        if (i == Math.floor(duration)) {
            logger.info("SENDING FAILURE SIGNAL");
            forceFailure(during);
        }
    }

    private String sendGetRequest(Long i) {
        logger.info("sending get request number {}", i);
        return template.getForObject(URL + i, String.class);
    }

    private String sendPostRequest(Long i) {
        logger.info("sending post request number {}", i);
        return template.postForEntity(URL + i, null, String.class).getBody();
    }

    private void forceFailure(int duration) {
        template.getForObject(URL + "/fail/" + duration, String.class);
    }
}
