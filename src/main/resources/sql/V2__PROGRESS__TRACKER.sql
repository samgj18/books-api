CREATE TABLE progress_tracker (
    table_name VARCHAR UNIQUE NOT NULL,
    last_pushed_record TIMESTAMP NOT NULL,
    CONSTRAINT progress_tracker_pkey PRIMARY KEY (table_name)
);