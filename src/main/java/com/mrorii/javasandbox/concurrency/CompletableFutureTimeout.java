package com.mrorii.javasandbox.concurrency;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Slf4j
public class CompletableFutureTimeout {

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(
                    1,
                    new ThreadFactoryBuilder()
                            .setDaemon(true)
                            .setNameFormat("failAfter-%d")
                            .build());

    private static <T> CompletableFuture<T> failAfter(Duration duration) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        scheduler.schedule(() -> {
            final TimeoutException ex = new TimeoutException("Timeout after " + duration);
            return promise.completeExceptionally(ex);
        }, duration.toMillis(), TimeUnit.MILLISECONDS);
        return promise;
    }

    private static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
        final CompletableFuture<T> timeout = failAfter(duration);
        return future.applyToEither(timeout, Function.identity());
    }

    @Value
    private static class Response {
        private final String message;
    }

    private static CompletableFuture<Response> asyncCode() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                // e.printStackTrace();
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
