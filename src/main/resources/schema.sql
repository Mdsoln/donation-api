DROP TABLE IF EXISTS appointments CASCADE;
DROP TABLE IF EXISTS slots CASCADE;
DROP TABLE IF EXISTS urgent_request CASCADE;
DROP TABLE IF EXISTS donations CASCADE;
DROP TABLE IF EXISTS donors CASCADE;
DROP TABLE IF EXISTS hospitals CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
     user_id BIGSERIAL PRIMARY KEY AUTOINCREMENT,
     username VARCHAR(255) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     roles VARCHAR(50) NOT NULL,
     created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE hospitals (
     hospital_id BIGSERIAL PRIMARY KEY,
     user_id BIGINT REFERENCES users(user_id),
     hospital_name VARCHAR(255),
     hospital_address VARCHAR(255),
     hospital_city VARCHAR(255),
     latitude DOUBLE PRECISION,
     longitude DOUBLE PRECISION
);

CREATE TABLE donors (
     donor_id BIGSERIAL PRIMARY KEY AUTOINCREMENT,
     user_id BIGINT REFERENCES users(user_id),
     full_name VARCHAR(255),
     email VARCHAR(255),
     phone VARCHAR(50),
     blood_type VARCHAR(10),
     age VARCHAR(10),
     height DOUBLE PRECISION,
     weight DOUBLE PRECISION,
     birth_date DATE,
     gender VARCHAR(10),
     image VARCHAR(255),
     address VARCHAR(255),
     created_at TIMESTAMP DEFAULT now(),
     updated_at TIMESTAMP
);

CREATE TABLE slots (
     id BIGSERIAL PRIMARY KEY,
     hospital_id BIGINT REFERENCES hospitals(hospital_id),
     start_time TIMESTAMP,
     end_time TIMESTAMP,
     max_capacity INT,
     current_bookings INT,
     is_booked BOOLEAN
);

CREATE TABLE appointment (
     id BIGSERIAL PRIMARY KEY AUTOINCREMENT,
     donor_id BIGINT REFERENCES donors(donor_id),
     slot_id BIGINT REFERENCES slots(id),
     status VARCHAR(50),
     description VARCHAR(255),
     blood_donated BOOLEAN,
     status_changed_at TIMESTAMP,
     overdue BOOLEAN,
     notification_sent BOOLEAN,
     date DATE
);

CREATE TABLE donations (
    donation_id BIGSERIAL PRIMARY KEY,
    donor_id BIGINT REFERENCES donors(donor_id),
    appointment_id BIGINT REFERENCES appointment(id),
    donation_date DATE,
    blood_type VARCHAR(10),
    volume INT,
    status VARCHAR(50)
);


CREATE TABLE urgent_requests (
    urgent_id BIGSERIAL PRIMARY KEY,
    hospital_id BIGINT REFERENCES hospitals(hospital_id),
    donor_id BIGINT REFERENCES donors(donor_id),
    patient_name VARCHAR(255),
    blood_type VARCHAR(10),
    notes TEXT,
    status VARCHAR(50),
    request_date DATE,
    request_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP
);
