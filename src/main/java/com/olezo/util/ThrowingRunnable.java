package com.olezo.util;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {
    void run() throws E;

    static <T extends Exception> Runnable wrapper(ThrowingRunnable<T> runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}