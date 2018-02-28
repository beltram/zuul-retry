package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @RestController
    @RequestMapping("/users")
    class UserRessource {

        @GetMapping
        public Mono<User> get() {
            return Mono.just(new User().setName("this one works"));
        }

        @PostMapping
        public Mono<User> post() {
            return Mono.just(new User().setName("this one fails"));
        }

        @PutMapping
        public Mono<User> put() {
            return Mono.just(new User().setName("this one fails"));
        }
    }
}
