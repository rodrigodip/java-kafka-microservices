package br.com.rodrigodip.orders.exceptions;

import java.math.BigDecimal;

public class PriceMismatchException extends RuntimeException {

    public PriceMismatchException(Long productId, BigDecimal expected, BigDecimal received) {
        super("Price mismatch for product [" + productId + "]: expected [" + expected + "] but received ["
                + received + "]");
    }
}
