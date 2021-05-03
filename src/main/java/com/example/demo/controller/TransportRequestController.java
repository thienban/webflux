package com.example.demo.controller;

import com.example.demo.entity.TransportRequest;
import com.example.demo.service.RequestService;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping(value = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransportRequestController {

    private final MediaType mediaType = MediaType.APPLICATION_JSON;
    private final RequestService requestService;

    TransportRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    Publisher<TransportRequest> getAll() {
        return this.requestService.all();
    }


    @GetMapping("/{id}")
    Publisher<TransportRequest> getById(@PathVariable("id") String id) {
        return this.requestService.get(id);
    }


    @DeleteMapping("/{id}")
    Publisher<TransportRequest> deleteById(@PathVariable String id) {
        return this.requestService.delete(id);
    }

    @PostMapping
    Publisher<ResponseEntity<TransportRequest>> create(@RequestBody TransportRequest transportRequest) {
        return this.requestService
                .create(transportRequest.getArrivalDate(), transportRequest.getArrivalDate(), transportRequest.getArrivalAddress(), transportRequest.getDepartureAddress())
                .map(p -> ResponseEntity.created(URI.create("/profiles/" + p.getId()))
                        .contentType(mediaType)
                        .build());
    }

    @PutMapping("/{id}")
    Publisher<ResponseEntity<TransportRequest>> updateById(@PathVariable String id, @RequestBody TransportRequest transportRequest) {
        return Mono
                .just(transportRequest)
                .flatMap(request -> this.requestService.update(id, request.getArrivalDate(), request.getArrivalDate(), request.getArrivalAddress(), request.getDepartureAddress()))
                        .map(p -> ResponseEntity
                                .ok()
                                .contentType(this.mediaType)
                                .build());
    }
}
