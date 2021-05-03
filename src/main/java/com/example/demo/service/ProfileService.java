package com.example.demo.service;

import com.example.demo.entity.Profile;
import com.example.demo.entity.Role;
import com.example.demo.event.ProfileCreatedEvent;
import com.example.demo.repository.ProfileRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
public class ProfileService {

    private final ApplicationEventPublisher publisher;
    private final ProfileRepository profileRepository;

    ProfileService(ApplicationEventPublisher publisher, ProfileRepository profileRepository) {
        this.publisher = publisher;
        this.profileRepository = profileRepository;
    }

    public Flux<Profile> all() {
        return this.profileRepository.findAll();
    }

    public Mono<Profile> get(String id) {
        return this.profileRepository.findById(id);
    }

    public Mono<Profile> update(String id, Role role, String email) {
        return this.profileRepository
                .findById(id)
                .map(p -> new Profile(p.getId(), role, email))
                .flatMap(this.profileRepository::save);
    }

    public Mono<Profile> delete(String id) {
        return this.profileRepository
                .findById(id)
                .flatMap(p -> this.profileRepository.deleteById(p.getId()).thenReturn(p));
    }

    public Mono<Profile> create(Role role, String email) {
        return this.profileRepository
                .save(new Profile(null, role, email))
                .doOnSuccess(profile -> this.publisher.publishEvent(new ProfileCreatedEvent(profile)));
    }
}
