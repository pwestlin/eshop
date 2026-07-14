CREATE TABLE IF NOT EXISTS orders
(
    id          UUID PRIMARY KEY,
    customer_id UUID        NOT NULL,
    status      VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_line_items
(
    id         BIGSERIAL PRIMARY KEY,
    order_id   UUID    NOT NULL,
    product_id INTEGER NOT NULL CHECK (id > 0),
    quantity   INT     NOT NULL,
    price      INT     NOT NULL,

    CONSTRAINT fk_order_line_items_orders
        FOREIGN KEY (order_id)
            REFERENCES orders (id)
            ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS customers
(
    id    UUID PRIMARY KEY,
    name  VARCHAR(20) NOT NULL,
    email VARCHAR(40) NOT NULL
);

CREATE TABLE IF NOT EXISTS products
(
    id          INTEGER     NOT NULL CHECK (id > 0) PRIMARY KEY,
    name        VARCHAR(20) NOT NULL,
    description VARCHAR(40) NOT NULL
);
