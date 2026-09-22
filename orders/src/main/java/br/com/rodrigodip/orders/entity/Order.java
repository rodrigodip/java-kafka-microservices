package br.com.rodrigodip.orders.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.rodrigodip.orders.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime placedAt;

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

    @Transient
    private PaymentData paymentData;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> itensList;

    public void place() {
        this.status = OrderStatus.PLACED;
        this.placedAt = LocalDateTime.now();
        this.itensList.forEach(item -> item.setOrder(this));
        this.total = calculateTotal();
    }

    private BigDecimal calculateTotal() {
        return itensList.stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
