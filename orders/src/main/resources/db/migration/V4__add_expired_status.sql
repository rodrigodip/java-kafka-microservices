-- Adds the EXPIRED status: a PLACED order that never received a PSP callback
-- within the TTL is marked EXPIRED (limitations #4 and #5).
ALTER TABLE orders DROP CONSTRAINT orders_status_check;

ALTER TABLE orders ADD CONSTRAINT orders_status_check CHECK (
    status in (
        'PLACED',
        'PAID',
        'INVOICED',
        'SHIPPED',
        'PAYMENT_FAILED',
        'PREPARING_SHIPMENT',
        'CANCELLED',
        'EXPIRED'
    )
);
