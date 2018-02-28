package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @RestController
    @RequestMapping("/users")
    class UserRessource {

        private LocalDateTime failUntil = now();

        @GetMapping
        public Mono<User> get() {
            return Mono.just(new User().setName("this one works"))
                       .flatMap(this::failIfRequested);
        }

        @PostMapping
        public Mono<User> post() {
            return Mono.just(new User().setName("this one fails"))
                       .flatMap(this::failIfRequested);
        }

        @PutMapping
        public Mono<User> put() {
            return Mono.just(new User().setName("this one fails"))
                       .flatMap(this::failIfRequested);
        }

        @GetMapping("/fail/{duration}")
        public Mono<LocalDateTime> fail(@PathVariable Integer duration) {
            failUntil = now().plusSeconds(duration);
            return Mono.just(failUntil);
        }

        private Mono<User> failIfRequested(User t) {
            return (Boolean) now().isBefore(failUntil) ?
                    Mono.error(new IllegalStateException("Fails on purpose")) :
                    Mono.just(t);
        }
    }
}
