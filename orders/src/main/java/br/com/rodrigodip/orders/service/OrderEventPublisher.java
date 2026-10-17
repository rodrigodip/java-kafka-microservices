package br.com.rodrigodip.orders.service;

import br.com.rodrigodip.orders.entity.Order;

public interface OrderEventPublisher {

    void publishPaid(Order order);

    void publishInvoiced(Order order);

    void publishShipped(Order order);
}
