package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportRequest {

    @Id
    private String id;

    private LocalDateTime departureDate;

    private LocalDateTime arrivalDate;

    private String departureAddress;

    private String arrivalAddress;

}
