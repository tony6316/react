CREATE DATABASE files;

\c files

CREATE TABLE file_uploads (
    file_id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) UNIQUE NOT NULL,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
