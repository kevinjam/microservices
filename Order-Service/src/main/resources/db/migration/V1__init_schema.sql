CREATE TABLE IF NOT EXISTS order_details (
   id SERIAL PRIMARY KEY,
   product_id integer,
   quantity bigint,
   order_date timestamp,
   order_status character varying(250) NOT NULL,
   amount bigint,
   active boolean NOT NULL
);
