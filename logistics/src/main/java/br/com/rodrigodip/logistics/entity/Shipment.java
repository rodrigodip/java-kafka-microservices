package br.com.rodrigodip.logistics.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.rodrigodip.logistics.enums.ShipmentStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "client_name", length = 150)
    private String clientName;

    @Column(name = "client_cpf", length = 11)
    private String clientCpf;

    @Column(name = "client_email", length = 100)
    private String clientEmail;

    @Column(name = "client_phone", length = 20)
    private String clientPhone;

    @Column(name = "client_address", length = 100)
    private String clientAddress;

    @Column(name = "client_address_number", length = 10)
    private String clientAddressNumber;

    @Column(name = "client_zipcode", length = 8)
    private String clientZipcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ShipmentStatus status;

    @Column(name = "total", nullable = false, precision = 16, scale = 2)
    private BigDecimal total;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "tracking_code", length = 255)
    private String trackingCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShipmentItem> itensList = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void markShipped(String trackingCode) {
        this.trackingCode = trackingCode;

        if (this.status != ShipmentStatus.SHIPPED) {
            this.status = ShipmentStatus.SHIPPED;
            this.shippedAt = LocalDateTime.now();
        }
    }

    public void addItem(ShipmentItem item) {
        item.setShipment(this);
        this.itensList.add(item);
    }
}
