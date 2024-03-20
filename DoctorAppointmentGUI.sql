create database DoctorAppointmentGUI;
use DoctorAppointmentGUI;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);
INSERT INTO users (username, password) VALUES('sethu', '0365');
CREATE TABLE appointments (
    id INT PRIMARY KEY,
    user_id INT NOT NULL,
    appointment_date varchar(100) NOT NULL,
    appointment_time varchar(100) NOT NULL,
    doctor_name VARCHAR(100) NOT NULL
);
select * from appointments;


