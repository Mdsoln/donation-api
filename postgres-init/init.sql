-- This script initializes the PostgreSQL database for the Blood Donation System.

-- Insert users (the passwords are hashed for security: 1234567)
INSERT INTO users (user_id, username, password, roles, created_at) VALUES
(1, 'muddyfakih98@gmail.com', '$2a$10$zs8XYp9A8uVrEMuOm3/fmOzy4ntkr48lmUT6nQbIT26mDW/bQxtoi', 'DONOR', now()),
(2, 'aishamussamzava@gmail.com', '$2a$10$4doGpdq/KWBMTXI3XjMwqOtX5I2o2E9NEgECRDpy5g6RlQ7I6ixP.', 'DONOR', now()),
(3, 'muhimbili@admin', '$2a$10$yaclSMNNSZH.4G7g/xf7Su4S17NcX6ZteI6e5nqfM1cL2hWUmr5pG', 'HOSPITAL', now()),
(4, 'agakhan@admin', '$2a$10$4vt.gUIafaX4Keof2R1IxOdp4DD7XSq6yELnybZTGddZ/Aqnx5FiC', 'HOSPITAL', now()),
(5, 'tumaini@admin', '$2a$10$tbTlbQtL8XqUwEeSOYxusOkhlDpsf3XQaUDdDwAigiF/1Mk.eB7RS', 'HOSPITAL', now()),
(6, 'mwananyamala@admin', '$2a$10$rMUIz3aSf9UnaQfWOLSX/OyuDMbpJq6PfXoNXoDc4HBjnnz.z7Dvm', 'HOSPITAL', now()),
(7, 'temeke@admin', '$2a$10$tafXtgTMc2.SFROn1vgDH.eY3XY0UPhPkl/P7ll9hSfIb01v0w6cO', 'HOSPITAL', now()),
(8, 'amana@admin', '$2a$10$07I0DutHVqJne83UyTHwneNrR3OmL7LsuL7XuFeLIRGPbEU0b8rnC', 'HOSPITAL', now());


-- Insert hospitals
INSERT INTO hospitals (hospital_id, user_id, hospital_name, hospital_address, hospital_city, latitude, longitude) VALUES
(1, 3,'Muhimbili National Hospital', 'United Nations Road, Upanga', 'Dar es Salaam', -6.8031, 39.2726),
(2, 4,'Aga Khan Hospital', 'Ocean Road, Kivukoni', 'Dar es Salaam', -6.803487, 39.287841),
(3, 5,'Tumaini Hospital', 'Ali Hassan Mwinyi Road, Kariakoo', 'Dar es Salaam', -6.8213, 39.28148),
(4, 6,'Mwananyamala Hospital', 'Mwananyamala Road, Kinondoni', 'Dar es Salaam', -6.7872, 39.253),
(5, 7,'Temeke Hospital', 'Temeke St.', 'Dar es Salaam', -6.869, 39.2667),
(6,8,'Amana Regional Hospital', 'Ilala', 'Dar es Salaam', -6.8269, 39.26022);

-- Insert slots for each hospital
INSERT INTO slots (hospital_id, max_capacity, current_bookings, start_time, end_time, is_booked) VALUES
(1, 10, 3, '2025-07-05 09:00:00', '2025-07-05 10:30:00', FALSE),
(1, 10, 5, '2025-07-10 11:00:00', '2025-07-10 12:30:00', FALSE),
(1, 10, 2, '2025-07-15 10:00:00', '2025-07-15 11:30:00', FALSE),
(1, 10, 6, '2025-07-25 14:00:00', '2025-07-25 15:30:00', FALSE),
(1, 10, 0, '2025-07-05 13:00:00', '2025-07-05 14:30:00', FALSE),
(1, 10, 4, '2025-07-10 08:00:00', '2025-07-10 09:30:00', FALSE),
(2, 10, 4, '2025-07-03 10:00:00', '2025-07-03 11:30:00', FALSE),
(2, 10, 1, '2025-07-18 09:00:00', '2025-07-18 10:30:00', FALSE),
(2, 10, 2, '2025-07-08 13:00:00', '2025-07-08 14:30:00', FALSE),
(2, 10, 5, '2025-07-20 11:00:00', '2025-07-20 12:30:00', FALSE),
(2, 10, 0, '2025-07-12 15:00:00', '2025-07-12 16:30:00', FALSE),
(2, 10, 3, '2025-07-22 10:00:00', '2025-07-22 11:30:00', FALSE),
(3, 10, 3, '2025-07-07 13:00:00', '2025-07-07 14:30:00', FALSE),
(3, 10, 6, '2025-07-22 15:00:00', '2025-07-22 16:30:00', FALSE),
(3, 10, 2, '2025-07-10 10:00:00', '2025-07-10 11:30:00', FALSE),
(3, 10, 0, '2025-07-28 09:00:00', '2025-07-28 10:30:00', FALSE),
(3, 10, 1, '2025-07-15 14:00:00', '2025-07-15 15:30:00', FALSE),
(3, 10, 4, '2025-07-27 16:00:00', '2025-07-27 17:30:00', FALSE),
(4, 10, 5, '2025-07-09 08:00:00', '2025-07-09 09:30:00', FALSE),
(4, 10, 3, '2025-07-20 14:00:00', '2025-07-20 15:30:00', FALSE),
(4, 10, 7, '2025-07-12 11:00:00', '2025-07-12 12:30:00', FALSE),
(4, 10, 1, '2025-07-25 10:00:00', '2025-07-25 11:30:00', FALSE),
(4, 10, 0, '2025-07-18 13:00:00', '2025-07-18 14:30:00', FALSE),
(4, 10, 6, '2025-07-30 15:00:00', '2025-07-30 16:30:00', FALSE),
(5, 10, 3, '2025-07-14 14:00:00', '2025-07-14 15:30:00', FALSE),
(5, 10, 5, '2025-07-29 16:00:00', '2025-07-29 17:30:00', FALSE),
(5, 10, 4, '2025-07-07 15:00:00', '2025-07-07 16:30:00', FALSE),
(5, 10, 0, '2025-07-20 13:00:00', '2025-07-20 14:30:00', FALSE),
(5, 10, 2, '2025-07-10 12:00:00', '2025-07-10 13:30:00', FALSE),
(5, 10, 3, '2025-07-26 10:00:00', '2025-07-26 11:30:00', FALSE),
(6, 10, 2, '2025-07-05 07:00:00', '2025-07-05 09:00:00', FALSE),
(6, 10, 0, '2025-07-15 12:00:00', '2025-07-15 14:00:00', FALSE),
(6, 10, 5, '2025-07-07 08:00:00', '2025-07-07 10:00:00', FALSE),
(6, 10, 3, '2025-07-20 11:00:00', '2025-07-20 13:00:00', FALSE),
(6, 10, 1, '2025-07-12 14:00:00', '2025-07-12 16:00:00', FALSE),
(6, 10, 4, '2025-07-30 09:00:00', '2025-07-30 11:00:00', FALSE);

-- Insert Donor details
INSERT INTO donors (donor_id, user_id, full_name, email, phone, blood_type, age, height, weight, birth_date, gender, image, address, created_at) VALUES
(1, 1, 'Muddy Fakih', 'muddyfakih98@gmail.com', '+255717611117', 'O+', 23, 166, 56.5, '2002-08-03', 'Male', 'muddy.jpg', 'Kijitonyama', now()),
(2, 2, 'Aisha Mzava', 'aishamussamzava@gmail.com', '+255717611118', 'A+', 23, 154, 48.6, '2002-08-28', 'Female', 'aisha.jpg', 'Kijitonyama', now());

-- Insert Donor Appointment details
INSERT INTO appointment (id, donor_id, slot_id, status, description, blood_donated, status_changed_at, overdue, notification_sent, date) VALUES
(1, 1, 1, 'SCHEDULED', 'Routine donation', false, now(), false, false, '2025-07-05'),
(2, 1, 1, 'COMPLETED', 'Urgent need', TRUE, now(), false, false, '2023-01-05'),
(3, 1, 2, 'COMPLETED', 'First time donor', TRUE, now(), false, false, '2023-05-06'),
(4, 1, 2, 'CANCELLED', 'Unable to attend', false, now(), false, false, '2023-05-06'),
(4, 1, 4, 'OVERDUE', 'Unable to attend', false, now(), false, false, '2023-04-06'),
(5, 2, 1, 'SCHEDULED', 'Routine donation', false, now(), false, false, '2025-07-05'),
(6, 2, 1, 'COMPLETED', 'Urgent need', TRUE, now(), false, false, '2023-01-05'),
(7, 2, 2, 'COMPLETED', 'First time donor', TRUE, now(), false, false, '2023-05-06'),
(8, 2, 2, 'CANCELLED', 'Unable to attend', false, now(), false, false, '2023-05-06'),
(8, 2, 2, 'OVERDUE', 'Unable to attend', false, now(), false, false, '2023-04-06');

-- Insert Blood Donation details for completed appointments for each donor
INSERT INTO blood_donations (donation_id, donor_id, appointment_id, donation_date, blood_type, volume, status) VALUES
(1, 1, 2, '2023-01-05', 'O+', 450, 'COMPLETED'),
(2, 1, 3, '2023-05-06', 'O+', 450, 'COMPLETED'),
(3, 2, 6, '2023-01-05', 'A+', 450, 'COMPLETED'),
(4, 2, 7, '2023-05-06', 'A+', 450, 'COMPLETED');

-- Insert Urgent Blood Request details
INSERT INTO urgent_request (urgent_id, hospital_id, donor_id, patient_name, blood_type, notes, status, request_date, request_time, created_at) VALUES
(1, 1, 1, 'Muddy Ramadhan', 'A+', 'Emergency surgery', 'PENDING', '2025-07-03', now(), now()),
(2, 2, 2, 'Aisha Mussa', 'O+', 'Accident victim', 'PENDING', '2025-07-03', now(), now());
