package com.example.userservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @RestController
    @RequestMapping("/users")
    class UserRessource {

        private LocalDateTime failUntil = now();

        private Logger logger = LoggerFactory.getLogger(UserRessource.class);

        @GetMapping("/{id}")
        public Mono<String> get(@PathVariable String id) {
            logger.info("get request for id {}", id);
            return Mono.just("works for " + id)
                       .flatMap(this::failIfRequested);
        }

        @PostMapping("/nobody/{id}")
        public Mono<String> postNoBody(@PathVariable String id) {
            logger.info("post without body request for id {}", id);
            return Mono.just("works without body for " + id)
                       .flatMap(this::failIfRequested);
        }

        @PostMapping("/{id}")
        public Mono<String> post(@PathVariable String id, @RequestBody String body) {
            logger.info("post request for id {}", id);
            return Mono.just("works for " + body)
                       .flatMap(this::failIfRequested);
        }

        @PutMapping("/{id}")
        public Mono<String> put(@PathVariable String id, @RequestBody String body) {
            logger.info("put request for id {}", id);
            return Mono.just("works for " + body)
                       .flatMap(this::failIfRequested);
        }

        @PutMapping("/nobody/{id}")
        public Mono<String> putNoBody(@PathVariable String id) {
            logger.info("put without body request for id {}", id);
            return Mono.just("works for " + id)
                       .flatMap(this::failIfRequested);
        }

        @GetMapping("/fail/{duration}")
        public Mono<LocalDateTime> fail(@PathVariable int duration) {
            logger.info("Failure request received for duration {}", duration);
            failUntil = now().plusSeconds(duration);
            return Mono.just(failUntil);
        }

        private Mono<String> failIfRequested(String t) {
            return (Boolean) now().isBefore(failUntil) ?
                    Mono.error(new IllegalStateException("Fails on purpose")) :
                    Mono.just(t);
        }
    }
}
