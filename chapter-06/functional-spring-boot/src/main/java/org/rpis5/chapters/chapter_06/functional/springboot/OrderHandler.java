package org.rpis5.chapters.chapter_06.functional.springboot;

import java.net.URI;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Service
public class OrderHandler {

    final OrderRepository orderRepository;

    public OrderHandler(OrderRepository repository) {
        orderRepository = repository;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        log.info("create");
        return request
            .bodyToMono(Order.class)
            .log()
            .flatMap(orderRepository::save)
            .log()
            .flatMap(o ->
                ServerResponse.created(URI.create("/orders/" + o.getId()))
                              .build()
            );
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        log.info("get");
        return orderRepository
            .findById(request.pathVariable("id"))
            .log()
            .flatMap(order ->
                ServerResponse
                    .ok()
                    .syncBody(order)
            )
            .log()
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return null;
    }
}
