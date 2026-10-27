package br.com.rodrigodip.logistics.service;

import br.com.rodrigodip.logistics.entity.Shipment;

public interface ShipmentEventPublisher {

    void publishShipped(Shipment shipment);
}
