package com.legendme.login.svc.adapters.out.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

//@Configuration
//public class UserClientConfig {
//    @Bean
//    WebClient userWebClient(@Value("${user.service.base-url}") String baseUrl,
//                            @Value("${user.s2s.shared-secret}") String secret) {
//        return WebClient.builder()
//                .baseUrl(baseUrl)
//                .filter(ExchangeFilterFunction.ofRequestProcessor(req -> {
//                    return reactor.core.publisher.Mono.just(
//                            org.springframework.web.reactive.function.client.ClientRequest.from(req)
//                                    .headers(h -> h.add("Service-Token", secret))
//                                    .build()
//                    );
//                }))
//                .build();
//    }
//}
