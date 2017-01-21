package com.mrorii.javasandbox.concurrency;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static com.mrorii.javasandbox.concurrency.TimeoutUtil.within;

@Slf4j
public class CompletableFutureTimeout {

    @Value
    private static class Response {
        private final String message;
    }

    private static CompletableFuture<Response> asyncCode() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                // no-op
            }
            return new Response("foobar");
        });
    }

    private static void send(Response response) {
        log.info("Sending {}", response);
    }

    public static void main(String[] args) {
        final CompletableFuture<Response> responseFuture = within(asyncCode(), Duration.ofSeconds(1));

        responseFuture
                .thenAccept(CompletableFutureTimeout::send)
                .exceptionally(throwable -> {
                    log.error("Unrecoverable error", throwable);
                    return null;
                });

        responseFuture.join();
    }
}
