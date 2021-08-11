CREATE TABLE books
(
    book_id     SERIAL,
    name        VARCHAR NOT NULL,
    author      VARCHAR NOT NULL,
    year        INTEGER NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT book_pkey PRIMARY KEY (book_id)
);

CREATE INDEX books_updated_at_idx ON books(updated_at ASC);