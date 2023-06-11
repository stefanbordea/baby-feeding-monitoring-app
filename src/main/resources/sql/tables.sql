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
    endTime      TIMESTAMP        NOT NULL
);

CREATE TABLE FEEDING_SESSION_USERS
(
    id                 SERIAL PRIMARY KEY,
    feeding_session_id INT REFERENCES FEEDING_SESSIONS (id),
    user_id            INT REFERENCES USERS (id),
    CONSTRAINT unique_feeding_session_user UNIQUE (feeding_session_id, user_id)
);

-- Create ENUM type for Role
CREATE TYPE role_enum AS ENUM ('ADMIN', 'PHYSICIAN');