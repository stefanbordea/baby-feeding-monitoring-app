-- Create USERS table
CREATE TABLE USERS
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255)
);

-- Create FEEDING_SESSIONS table
CREATE TABLE FEEDING_SESSIONS
(
    id           SERIAL PRIMARY KEY,
    milkConsumed DOUBLE PRECISION NOT NULL,
    startTime    TIMESTAMP        NOT NULL,
    endTime      TIMESTAMP        NOT NULL,
    user_id      INT,
    FOREIGN KEY (user_id) REFERENCES USERS (id)
);

-- Create ENUM type for Role
CREATE TYPE role_enum AS ENUM ('ADMIN', 'PHYSICIAN');