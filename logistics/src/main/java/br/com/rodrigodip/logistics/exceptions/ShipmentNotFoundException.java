package br.com.rodrigodip.logistics.exceptions;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(Long orderId) {
        super("No shipment found for order ID [" + orderId + "]");
    }
}
