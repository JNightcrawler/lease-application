-- Enable UUID generation (pgcrypto works on all managed Postgres hosts: Neon, Supabase, Railway, Render, RDS)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE materials (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    material_name          VARCHAR(255) NOT NULL UNIQUE,
    no_of_stocks_available INTEGER NOT NULL DEFAULT 0 CHECK (no_of_stocks_available >= 0),
    total_stocks           INTEGER NOT NULL DEFAULT 0 CHECK (total_stocks >= 0),
    cost_per_day           NUMERIC(12, 2) NOT NULL CHECK (cost_per_day >= 0),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_stock_not_exceeding_total CHECK (no_of_stocks_available <= total_stocks)
);

CREATE TABLE orders (
    order_number               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mobile_number               VARCHAR(20) NOT NULL,
    create_timestamp            TIMESTAMPTZ NOT NULL DEFAULT now(),
    closing_timestamp           TIMESTAMPTZ,
    is_closed                   BOOLEAN NOT NULL DEFAULT FALSE,
    approximate_date_to_return  TIMESTAMPTZ,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_mobile_number ON orders (mobile_number);
CREATE INDEX idx_orders_is_closed ON orders (is_closed);

CREATE TABLE order_details (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number             UUID NOT NULL REFERENCES orders (order_number) ON DELETE RESTRICT,
    material_id              UUID NOT NULL REFERENCES materials (id) ON DELETE RESTRICT,
    material_name            VARCHAR(255) NOT NULL,
    no_of_material_required  INTEGER NOT NULL CHECK (no_of_material_required >= 1),
    lent_timestamp            TIMESTAMPTZ NOT NULL DEFAULT now(),
    return_timestamp          TIMESTAMPTZ,
    cost                      NUMERIC(12, 2) DEFAULT 0,
    created_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_order_details_order_number ON order_details (order_number);
CREATE INDEX idx_order_details_material_id ON order_details (material_id);
