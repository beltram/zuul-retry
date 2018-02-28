package com.example.userservice;

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

        @GetMapping
        public Mono<String> get() {
            return Mono.just("this one works")
                       .flatMap(this::failIfRequested);
        }

        @PostMapping
        public Mono<String> post() {
            return Mono.just("this one fails")
                       .flatMap(this::failIfRequested);
        }

        @PutMapping
        public Mono<String> put() {
            return Mono.just("this one fails")
                       .flatMap(this::failIfRequested);
        }

        @GetMapping("/fail/{duration}")
        public Mono<LocalDateTime> fail(@PathVariable Integer duration) {
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
