package com.booking.member.global.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingAdapter {

    @Value("${gateway.url")
    private String GATEWAY_URL;
    private final String BookingServiceUrl="/api/booking";
    private final String AUTH="Authorization";

    public Mono<String> noticeMemberDeleted(Integer memberPk,String token) {
        WebClient webClient=WebClient.create(GATEWAY_URL);
        return webClient.get()
                .uri(BookingServiceUrl+"/meeting/member/{memberPk}",memberPk)
                .header(AUTH,token)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        response -> Mono.error(new RuntimeException("Booking Adapter Error 4xx")))
                .onStatus(HttpStatus::is5xxServerError,
                        response -> Mono.error(new RuntimeException("Booking Adapter Error 5xx")))
                .bodyToMono(Void.class)
                .then(Mono.just("Member deletion notice sent successfully"));
    }
}
