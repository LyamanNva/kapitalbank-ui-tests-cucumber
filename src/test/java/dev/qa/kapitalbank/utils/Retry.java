package dev.qa.kapitalbank.utils;

import dev.qa.kapitalbank.config.Config;
import org.openqa.selenium.StaleElementReferenceException;

import java.time.Duration;

/**
 * Re-runs an assertion until it passes or the timeout expires.
 * The calculators on the page recalculate asynchronously, so a fixed sleep would be
 * either slow or flaky; polling the assertion itself is neither.
 */
public final class Retry {

    private static final Duration POLL = Duration.ofMillis(250);

    private Retry() {
    }

    public static void eventually(Runnable assertion) {
        long deadline = System.nanoTime() + Config.timeout().toNanos();
        while (true) {
            try {
                assertion.run();
                return;
            } catch (AssertionError | StaleElementReferenceException e) {
                if (System.nanoTime() >= deadline) {
                    throw e;
                }
                pause();
            }
        }
    }

    private static void pause() {
        try {
            Thread.sleep(POLL.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting", e);
        }
    }
}
