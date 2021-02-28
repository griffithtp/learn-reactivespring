package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();
        StepVerifier.create((namesFlux.log()))
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }


    @Test
    public void transformUsingMap_length() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();
        StepVerifier.create((namesFlux.log()))
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_length_repeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();
        StepVerifier.create((namesFlux.log()))
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_filter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(s -> s.toUpperCase())
//                .repeat(1)
                .log();
        StepVerifier.create((namesFlux.log()))
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {

        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                })
                .log();

        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Arrays.asList(s, "newValue");
    }


    @Test
    public void transformUsingFlatMap_usingParallel() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .flatMap( (s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // <Flux<List<String>>
                .flatMap(sfm -> Flux.fromIterable(sfm))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_parallel_maintain_order() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
//                .concatMap( (s) ->
//                        s.map(this::convertToList).subscribeOn(parallel())) // <Flux<List<String>>
                .flatMapSequential( (s) ->
                        s.map(this::convertToList).subscribeOn(parallel())) // <Flux<List<String>>
                .flatMap(sfm -> Flux.fromIterable(sfm))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

}
