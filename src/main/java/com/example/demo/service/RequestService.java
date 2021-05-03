package com.example.demo.service;

import com.example.demo.entity.Profile;
import com.example.demo.entity.Role;
import com.example.demo.entity.TransportRequest;
import com.example.demo.event.RequestTransportCreatedEvent;
import com.example.demo.repository.TransportRequestRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Log4j2
@Service
public class RequestService {

    private final ApplicationEventPublisher publisher;
    private final TransportRequestRepository transportRequestRepository;

    RequestService(ApplicationEventPublisher publisher, TransportRequestRepository transportRequestRepository) {
        this.publisher = publisher;
        this.transportRequestRepository = transportRequestRepository;
    }

    public Flux<TransportRequest> all() {
        return this.transportRequestRepository.findAll();
    }

    public Mono<TransportRequest> get(String id) {
        return this.transportRequestRepository.findById(id);
    }

    public Mono<TransportRequest> update(String id, LocalDateTime dateArrival, LocalDateTime dateDeparture, String addressArrival, String addressDeparture) {
        return this.transportRequestRepository
                .findById(id)
                .map(p -> new TransportRequest(p.getId(), dateArrival, dateDeparture, addressArrival, addressDeparture))
                .flatMap(this.transportRequestRepository::save);
    }

    public Mono<TransportRequest> delete(String id) {
        return this.transportRequestRepository
                .findById(id)
                .flatMap(p -> this.transportRequestRepository.deleteById(p.getId()).thenReturn(p));
    }

    public Mono<TransportRequest> create(LocalDateTime dateArrival, LocalDateTime dateDeparture, String addressArrival, String addressDeparture) {
        return this.transportRequestRepository
                .save(new TransportRequest(null, dateDeparture, dateArrival, addressDeparture, addressArrival))
                .doOnSuccess(request -> this.publisher.publishEvent(new RequestTransportCreatedEvent(request)));
    }
}
