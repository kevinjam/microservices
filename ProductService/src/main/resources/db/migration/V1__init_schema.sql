CREATE TABLE IF NOT EXISTS product (
   id SERIAL PRIMARY KEY,
   product_name character varying (250) NOT NULL,
   price DOUBLE PRECISION,
   quantity DOUBLE PRECISION,
   active boolean NOT NULL
);
