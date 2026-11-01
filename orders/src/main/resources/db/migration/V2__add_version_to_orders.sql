-- Optimistic locking for concurrent payment retries (limitation #2).
ALTER TABLE orders ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
