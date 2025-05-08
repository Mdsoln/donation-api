-- Admin users
INSERT INTO users (username, password, roles, created_at) VALUES
('admin1', '$2a$10$xyz123', 'HOSPITAL', '2023-01-01 08:00:00'),
('hospital_admin1', '$2a$10$xyz123', 'HOSPITAL', '2025-01-02 09:00:00'),
('hospital_admin2', '$2a$10$xyz123', 'HOSPITAL', '2025-01-03 10:00:00'),
('hospital_admin3', '$2a$10$xyz123', 'HOSPITAL', '2025-01-04 11:00:00'),
('hospital_admin4', '$2a$10$xyz123', 'HOSPITAL', '2025-01-05 12:00:00');

-- Donor users
INSERT INTO users (username, password, roles, created_at) VALUES
('john_doe', '$2a$10$xyz123', 'DONOR', '2025-02-01 08:00:00'),
('mary_smith', '$2a$10$xyz123', 'DONOR', '2025-02-02 09:00:00'),
('juma_kim', '$2a$10$xyz123', 'DONOR', '2025-02-03 10:00:00'),
('sarah_james', '$2a$10$xyz123', 'DONOR', '2025-02-04 11:00:00'),
('ali_hassan', '$2a$10$xyz123', 'DONOR', '2025-02-05 12:00:00'),
('grace_michael', '$2a$10$xyz123', 'DONOR', '2025-02-06 13:00:00'),
('david_wilson', '$2a$10$xyz123', 'DONOR', '2025-02-07 14:00:00'),
('fatima_omar', '$2a$10$xyz123', 'DONOR', '2025-02-08 15:00:00'),
('peter_joseph', '$2a$10$xyz123', 'DONOR', '2025-02-09 16:00:00'),
('lucia_george', '$2a$10$xyz123', 'DONOR', '2025-02-10 17:00:00'),
('samuel_edward', '$2a$10$xyz123', 'DONOR', '2025-02-11 18:00:00'),
('rehema_juma', '$2a$10$xyz123', 'DONOR', '2025-02-12 19:00:00'),
('joseph_ben', '$2a$10$xyz123', 'DONOR', '2025-02-13 20:00:00'),
('elizabeth_rose', '$2a$10$xyz123', 'DONOR', '2025-02-14 21:00:00'),
('michael_leo', '$2a$10$xyz123', 'DONOR', '2025-02-15 22:00:00');

--Donor User Details
INSERT INTO donors (user_id, full_name, email, phone, blood_type, height, weight, birth_date, gender, image, address, created_at) VALUES
(6, 'John Doe', 'john.doe@example.com', '+255712345678', 'A+', 175, 70, '1990-05-15', 'Male', 'john.jpg', 'Mikocheni, Dar es Salaam', '2023-02-01 08:00:00'),
(7, 'Mary Smith', 'mary.smith@example.com', '+255713456789', 'O-', 165, 55, '1992-08-20', 'Female', 'mary.jpg', 'Masaki, Dar es Salaam', '2023-02-02 09:00:00'),
(8, 'Juma Kim', 'juma.kim@example.com', '+255714567890', 'B+', 180, 80, '1988-11-10', 'Male', 'juma.jpg', 'Tabata, Dar es Salaam', '2023-02-03 10:00:00'),
(9, 'Sarah James', 'sarah.james@example.com', '+255715678901', 'AB+', 170, 65, '1995-03-25', 'Female', 'sarah.jpg', 'Sinza, Dar es Salaam', '2023-02-04 11:00:00'),
(10, 'Ali Hassan', 'ali.hassan@example.com', '+255716789012', 'O+', 172, 75, '1985-07-30', 'Male', 'ali.jpg', 'Kariakoo, Dar es Salaam', '2023-02-05 12:00:00'),
(11, 'Grace Michael', 'grace.michael@example.com', '+255717890123', 'A-', 168, 60, '1993-09-12', 'Female', 'grace.jpg', 'Ubungo, Dar es Salaam', '2023-02-06 13:00:00'),
(12, 'David Wilson', 'david.wilson@example.com', '+255718901234', 'B-', 182, 85, '1991-01-05', 'Male', 'david.jpg', 'Mbezi, Dar es Salaam', '2023-02-07 14:00:00'),
(13, 'Fatima Omar', 'fatima.omar@example.com', '+255719012345', 'O+', 160, 58, '1994-04-18', 'Female', 'fatima.jpg', 'Kawe, Dar es Salaam', '2023-02-08 15:00:00'),
(14, 'Peter Joseph', 'peter.joseph@example.com', '+255720123456', 'A+', 178, 72, '1989-06-22', 'Male', 'peter.jpg', 'Kinondoni, Dar es Salaam', '2023-02-09 16:00:00'),
(17, 'Lucia George', 'lucia.george@example.com', '+255721234567', 'AB-', 166, 62, '1996-12-08', 'Female', 'lucia.jpg', 'Mbagala, Dar es Salaam', '2023-02-10 17:00:00'),
(18, 'Samuel Edward', 'samuel.edward@example.com', '+255722345678', 'B+', 174, 78, '1987-10-15', 'Male', 'samuel.jpg', 'Ilala, Dar es Salaam', '2023-02-11 18:00:00'),
(19, 'Rehema Juma', 'rehema.juma@example.com', '+255723456789', 'O-', 169, 59, '1997-02-28', 'Female', 'rehema.jpg', 'Buguruni, Dar es Salaam', '2023-02-12 19:00:00'),
(20, 'Joseph Ben', 'joseph.ben@example.com', '+255724567890', 'A-', 176, 74, '1990-07-03', 'Male', 'joseph.jpg', 'Tegeta, Dar es Salaam', '2023-02-13 20:00:00');

--Hospital Details
INSERT INTO hospitals (hospital_id, hospital_name, hospital_address, hospital_city) VALUES
(1, 'Muhimbili National Hospital', 'United Nations Road, Upanga', 'Dar es Salaam'),
(2, 'Aga Khan Hospital', 'Ocean Road, Kivukoni', 'Dar es Salaam'),
(3, 'Tumaini Hospital', 'Ali Hassan Mwinyi Road, Kariakoo', 'Dar es Salaam'),
(4, 'Mwananyamala Hospital', 'Mwananyamala Road, Kinondoni', 'Dar es Salaam'),
(5, 'Temeke Hospital', 'Temeke St.', 'Dar es Salaam'),
(6,'Amana Regional Hospital', 'Ilala', 'Dar es Salaam');

--Slots details

-- Muhimbili National Hospital (ID: 1)
INSERT INTO slots (hospital_id, max_capacity, current_bookings, start_time, end_time, is_booked) VALUES
(1, 10, 3, '2025-04-05 09:00:00', '2025-04-05 10:30:00', FALSE),
(1, 10, 5, '2025-04-10 11:00:00', '2025-04-10 12:30:00', FALSE),
(1, 10, 2, '2025-05-15 10:00:00', '2025-05-15 11:30:00', FALSE),
(1, 10, 6, '2025-05-25 14:00:00', '2025-05-25 15:30:00', FALSE),
(1, 10, 0, '2025-06-05 13:00:00', '2025-06-05 14:30:00', FALSE),
(1, 10, 4, '2025-06-10 08:00:00', '2025-06-10 09:30:00', FALSE);

-- Aga Khan Hospital (ID: 2)
INSERT INTO slots (hospital_id, max_capacity, current_bookings, start_time, end_time, is_booked) VALUES
(2, 10, 4, '2025-04-03 10:00:00', '2025-04-03 11:30:00', FALSE),
(2, 10, 1, '2025-04-18 09:00:00', '2025-04-18 10:30:00', FALSE),
(2, 10, 2, '2025-05-08 13:00:00', '2025-05-08 14:30:00', FALSE),
(2, 10, 5, '2025-05-20 11:00:00', '2025-05-20 12:30:00', FALSE),
(2, 10, 0, '2025-06-12 15:00:00', '2025-06-12 16:30:00', FALSE),
(2, 10, 3, '2025-06-22 10:00:00', '2025-06-22 11:30:00', FALSE);

-- Tumaini Hospital (ID: 3)
INSERT INTO slots (hospital_id, max_capacity, current_bookings, start_time, end_time, is_booked) VALUES
(3, 10, 3, '2025-04-07 13:00:00', '2025-04-07 14:30:00', FALSE),
(3, 10, 6, '2025-04-22 15:00:00', '2025-04-22 16:30:00', FALSE),
(3, 10, 2, '2025-05-10 10:00:00', '2025-05-10 11:30:00', FALSE),
(3, 10, 0, '2025-05-28 09:00:00', '2025-05-28 10:30:00', FALSE),
(3, 10, 1, '2025-06-15 14:00:00', '2025-06-15 15:30:00', FALSE),
(3, 10, 4, '2025-06-27 16:00:00', '2025-06-27 17:30:00', FALSE);

-- Mwananyamala Hospital (ID: 4)
INSERT INTO slots (hospital_id, max_capacity, current_bookings, start_time, end_time, is_booked) VALUES
(4, 10, 5, '2025-04-09 08:00:00', '2025-04-09 09:30:00', FALSE),
(4, 10, 3, '2025-04-20 14:00:00', '2025-04-20 15:30:00', FALSE),
(4, 10, 7, '2025-05-12 11:00:00', '2025-05-12 12:30:00', FALSE),
(4, 10, 1, '2025-05-25 10:00:00', '2025-05-25 11:30:00', FALSE),
(4, 10, 0, '2025-06-18 13:00:00', '2025-06-18 14:30:00', FALSE),
(4, 10, 6, '2025-06-30 15:00:00', '2025-06-30 16:30:00', FALSE);

-- Temeke Hospital (ID: 5)
INSERT INTO slots (hospital_id, max_capacity, current_bookings, start_time, end_time, is_booked) VALUES
(5, 10, 3, '2025-04-14 14:00:00', '2025-04-14 15:30:00', FALSE),
(5, 10, 5, '2025-04-29 16:00:00', '2025-04-29 17:30:00', FALSE),
(5, 10, 4, '2025-05-07 15:00:00', '2025-05-07 16:30:00', FALSE),
(5, 10, 0, '2025-05-20 13:00:00', '2025-05-20 14:30:00', FALSE),
(5, 10, 2, '2025-06-10 12:00:00', '2025-06-10 13:30:00', FALSE),
(5, 10, 3, '2025-06-26 10:00:00', '2025-06-26 11:30:00', FALSE);

-- Amana Regional Hospital (ID: 6)
INSERT INTO slots (hospital_id, max_capacity, current_bookings, start_time, end_time, is_booked) VALUES
(6, 10, 2, '2025-04-05 07:00:00', '2025-04-05 09:00:00', false),
(6, 10, 0, '2025-04-15 12:00:00', '2025-04-15 14:00:00', false),
(6, 10, 5, '2025-05-07 08:00:00', '2025-05-07 10:00:00', false),
(6, 10, 1, '2025-05-18 15:00:00', '2025-05-18 17:00:00', false),
(6, 10, 3, '2025-06-09 11:00:00', '2025-06-09 13:00:00', false),
(6, 10, 0, '2025-06-20 16:00:00', '2025-06-20 18:00:00', false);