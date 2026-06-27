package com.cityride.common.exception;

/**
 * Levee lors d'un conflit d'etat (-> HTTP 409).
 * Exemples : email deja utilise, reservation deja existante.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
