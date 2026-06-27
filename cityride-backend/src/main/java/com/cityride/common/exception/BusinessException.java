package com.cityride.common.exception;

/**
 * Levee lorsqu'une regle metier est violee (-> HTTP 400).
 * Exemples : reserver son propre trajet, pas assez de places, etc.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
