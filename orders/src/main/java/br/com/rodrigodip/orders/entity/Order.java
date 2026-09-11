package br.com.rodrigodip.orders.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import br.com.rodrigodip.orders.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long customerId;

    @Column(name = "order_date", nullable = false)
    private OffsetDateTime placedAt;

    @Column(name = "payment_key")
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "notes")
    private String notes;

    @Column(name = "total", nullable = false, precision = 16, scale = 2)
    private BigDecimal total;

    @Column(name = "tracking_code")
    private String trackingCode;

    @Column(name = "invoice_url")
    private String invoiceUrl;

}
