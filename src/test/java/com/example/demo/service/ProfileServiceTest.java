package com.example.demo.service;

import com.example.demo.entity.Profile;
import com.example.demo.entity.Role;
import com.example.demo.repository.ProfileRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Predicate;

@Log4j2
@DataMongoTest
@Import(ProfileService.class)
class ProfileServiceTest {

    private final ProfileService service;
    private final ProfileRepository repository;

    public ProfileServiceTest(@Autowired ProfileService service,
                              @Autowired ProfileRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Test
    void getAll() {
        Flux<Profile> saved = repository.saveAll(Flux.just(new Profile(null, Role.ADMIN, "Josh"), new Profile(null, Role.CUSTOMER, "Matt"), new Profile(null, Role.DRIVER, "Jane")));

        Flux<Profile> composite = service.all().thenMany(saved);

        Predicate<Profile> match = profile -> saved.any(saveItem -> saveItem.equals(profile)).block();

        StepVerifier
                .create(composite)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .verifyComplete();
    }

    @Test
    void save() {
        Mono<Profile> profileMono = this.service.create(Role.ADMIN, "email@email.com");
        StepVerifier
                .create(profileMono)
                .expectNextMatches(saved -> StringUtils.hasText(saved.getId()))
                .verifyComplete();
    }

    @Test
    void delete() {
        String test = "test";
        Mono<Profile> deleted = this.service
                .create(Role.CUSTOMER, test)
                .flatMap(saved -> this.service.delete(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase(test))
                .verifyComplete();
    }

    @Test
    void update() throws Exception {
        Mono<Profile> saved = this.service
                .create(Role.ADMIN, "test")
                .flatMap(p -> this.service.update(p.getId(), Role.DRIVER, "test1"));
        StepVerifier
                .create(saved)
                .expectNextMatches(p -> p.getEmail().equalsIgnoreCase("test1"))
                .verifyComplete();
    }

    @Test
    void getById() {
        String test = UUID.randomUUID().toString();
        Mono<Profile> deleted = this.service
                .create(Role.ADMIN, test)
                .flatMap(saved -> this.service.get(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> StringUtils.hasText(profile.getId()) && test.equalsIgnoreCase(profile.getEmail()))
                .verifyComplete();
    }
}
