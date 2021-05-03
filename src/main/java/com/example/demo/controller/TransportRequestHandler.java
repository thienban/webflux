package com.example.demo.controller;

import com.example.demo.entity.Profile;
import com.example.demo.entity.TransportRequest;
import com.example.demo.service.RequestService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class TransportRequestHandler {


    private final RequestService requestService;

    TransportRequestHandler(RequestService requestService) {
        this.requestService = requestService;
    }


    public Mono<ServerResponse> getById(ServerRequest r) {
        return defaultReadResponse(this.requestService.get(id(r)));
    }

    public Mono<ServerResponse> all(ServerRequest r) {
        return defaultReadResponse(this.requestService.all());
    }

    public Mono<ServerResponse> deleteById(ServerRequest r) {
        return defaultReadResponse(this.requestService.delete(id(r)));
    }

    public Mono<ServerResponse> updateById(ServerRequest r) {
        Flux<TransportRequest> id = r.bodyToFlux(TransportRequest.class)
                .flatMap(p -> this.requestService.update(id(r), p.getArrivalDate(), p.getDepartureDate(), p.getArrivalAddress(), p.getDepartureAddress()));
        return defaultReadResponse(id);
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Flux<TransportRequest> flux = request
                .bodyToFlux(TransportRequest.class)
                .flatMap(toWrite -> this.requestService.create(toWrite.getArrivalDate(), toWrite.getDepartureDate(), toWrite.getArrivalAddress(), toWrite.getDepartureAddress()));
        return defaultWriteResponse(flux);
    }


    private static Mono<ServerResponse> defaultWriteResponse(Publisher<TransportRequest> requests) {
        return Mono
                .from(requests)
                .flatMap(p -> ServerResponse
                        .created(URI.create("/requests/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                );
    }


    private static Mono<ServerResponse> defaultReadResponse(Publisher<TransportRequest> requests) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requests, Profile.class);
    }

    private static String id(ServerRequest r) {
        return r.pathVariable("id");
    }
}
