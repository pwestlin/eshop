-- Tabell för Order (Aggregate Root)
CREATE TABLE IF NOT EXISTS orders
(
    id          UUID PRIMARY KEY,
    customer_id UUID        NOT NULL,
    status      VARCHAR(20) NOT NULL
);

-- Tabell för OrderLineItem (Child Entity)
CREATE TABLE IF NOT EXISTS order_line_items
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   UUID NOT NULL, -- Matchar nu orders.id som UUID
    product_id UUID NOT NULL,
    quantity   INT  NOT NULL,
    price      INT  NOT NULL,

    CONSTRAINT fk_order_line_items_orders
        FOREIGN KEY (order_id)
            REFERENCES orders (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS customers
(
    id   UUID PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
