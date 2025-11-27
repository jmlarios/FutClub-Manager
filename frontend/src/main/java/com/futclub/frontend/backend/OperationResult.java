package com.futclub.frontend.backend;

/**
 * Generic wrapper to report success or failure for backend-triggered UI actions.
 */
public record OperationResult<T>(boolean success, String message, T payload) {

    public static <T> OperationResult<T> success(String message, T payload) {
        return new OperationResult<>(true, message, payload);
    }

    public static <T> OperationResult<T> success(String message) {
        return new OperationResult<>(true, message, null);
    }

    public static <T> OperationResult<T> failure(String message) {
        return new OperationResult<>(false, message, null);
    }
}
