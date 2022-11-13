CREATE TABLE IF NOT EXISTS transaction_details (
    id SERIAL PRIMARY KEY,
    order_id bigint,
    payment_mode character varying (250),
    reference_number character varying (250),
    payment_date timestamp,
    payment_status character varying (250),
    amount bigint,
    active boolean NOT NULL
);
