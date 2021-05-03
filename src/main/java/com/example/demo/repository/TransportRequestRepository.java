package com.example.demo.repository;

import com.example.demo.entity.TransportRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransportRequestRepository extends ReactiveMongoRepository<TransportRequest, String> {
}
