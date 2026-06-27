package com.cityride.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

/**
 * Format unifie des reponses d'erreur de l'API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, null);
    }

    public static ApiError withFields(int status, String error, String message, String path,
                                      Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, path, fieldErrors);
    }
}
