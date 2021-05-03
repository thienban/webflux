package com.example.demo.event;

 import com.example.demo.entity.TransportRequest;
 import org.springframework.context.ApplicationEvent;

public class RequestTransportCreatedEvent extends ApplicationEvent {

    public RequestTransportCreatedEvent(TransportRequest source) {
        super(source);
    }
}
