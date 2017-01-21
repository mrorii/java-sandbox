package com.mrorii.javasandbox.concurrency;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.mrorii.javasandbox.concurrency.TimeoutUtil.within;

@Slf4j
public class CompletableFutureTimeoutWithCollections {

    @Value
    private static class Response {
        private final String message;
    }

    private static CompletableFuture<Response> asyncCode(String message, long millis) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                // no-op
            }
            return new Response(message);
        });
    }

    /**
     * @see <a href="http://www.nurkiewicz.com/2013/05/java-8-completablefuture-in-action.html">Java 8: CompletableFuture in action</a>
     */
    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(CompletableFuture::join).
                        collect(Collectors.toList())
        );
    }

    public static void main(String[] args) {
        List<CompletableFuture<Response>> futures = Arrays.asList(
                within(asyncCode("foo", 1200), Duration.ofSeconds(1)),
                within(asyncCode("bar", 120), Duration.ofSeconds(1)),
                within(asyncCode("baz", 300), Duration.ofSeconds(1)),
                within(asyncCode("qux", 800), Duration.ofSeconds(1))
        );

        List<CompletableFuture<Response>> futuresWithExceptionally = futures.stream()
                .map(future -> future.exceptionally(throwable -> {
                    log.warn("Unrecoverable error", throwable);
                    return null;
                }))
                .collect(Collectors.toList());

        CompletableFuture<List<Response>> sequence = sequence(futuresWithExceptionally);
        List<Response> join = sequence.join();
        join.stream()
                .filter(response -> response != null)
                .forEach(response -> log.info("Response: {}", response));
    }

}
