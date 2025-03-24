CREATE TABLE cities (
    id BIGINT PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,
    latitude DECIMAL(9, 6) NOT NULL,
    UNIQUE uq_cities_country_city (country, city),
    INDEX cities_country (country),
    INDEX cities_city (city),
    FULLTEXT (country),
    FULLTEXT (city)
);