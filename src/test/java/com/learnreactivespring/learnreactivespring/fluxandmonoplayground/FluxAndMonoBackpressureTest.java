package com.learnreactivespring.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackpressureTest {

    @Test
    public void backpressureTest() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();

    }

    @Test
    public void backpressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe( (element) -> System.out.println("Element is:" + element)
                , e -> System.out.println("Exception is: " + e)
                , () -> System.out.println("Done")
                , subscription -> subscription.request(2)
        );

    }

    @Test
    public void backpressure_cancel() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe( (element) -> System.out.println("Element is:" + element)
                , e -> System.out.println("Exception is: " + e)
                , () -> System.out.println("Done")
                , subscription -> subscription.cancel()
        );
    }

    @Test
    public void customized_backpressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
             @Override
             protected void hookOnNext(Integer value) {
                 super.hookOnNext(value);
                 request(1);
                 System.out.println("Value received is: " + value);
                 if (value == 4) {
                     cancel();
                 }
             }
         }
        );
    }
}
