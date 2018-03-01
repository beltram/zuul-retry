package com.example.zuulproxylatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ZuulProxyLatestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulProxyLatestApplication.class, args);
    }
}
