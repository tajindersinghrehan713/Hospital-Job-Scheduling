
USE hospital_scheduling;

INSERT INTO departments (department_name, location)
VALUES
    ('Emergency', 'Ground Floor'),
    ('Cardiology', 'First Floor'),
    ('General Medicine', 'Second Floor')
ON DUPLICATE KEY UPDATE
    location = VALUES(location);

INSERT INTO patients (name, age, gender, contact)
VALUES
    ('Aarav Sharma', 35, 'Male', '9000000001'),
    ('Priya Verma', 42, 'Female', '9000000002'),
    ('Rahul Singh', 28, 'Male', '9000000003'),
    ('Ananya Gupta', 50, 'Female', '9000000004');

INSERT INTO staff
    (department_id, name, role, availability_status)
SELECT department_id, 'Dr. Mehta', 'Doctor', 'AVAILABLE'
FROM departments
WHERE department_name = 'Emergency';

INSERT INTO staff
    (department_id, name, role, availability_status)
SELECT department_id, 'Dr. Kapoor', 'Doctor', 'AVAILABLE'
FROM departments
WHERE department_name = 'Cardiology';

INSERT INTO resources
    (department_id, resource_name, resource_type, availability_status)
SELECT department_id, 'ECG Machine', 'Equipment', 'AVAILABLE'
FROM departments
WHERE department_name = 'Cardiology';

INSERT INTO resources
    (department_id, resource_name, resource_type, availability_status)
SELECT department_id, 'Examination Bed', 'Furniture', 'AVAILABLE'
FROM departments
WHERE department_name = 'Emergency';

INSERT INTO jobs
    (patient_id, department_id, job_type, arrival_time,
     burst_time, priority, status)
SELECT p.patient_id, d.department_id, 'Initial Assessment',
       0, 5, 0, 'WAITING'
FROM patients p
JOIN departments d ON d.department_name = 'Emergency'
WHERE p.name = 'Aarav Sharma'
AND NOT EXISTS (
    SELECT 1 FROM jobs j
    WHERE j.patient_id = p.patient_id
      AND j.job_type = 'Initial Assessment'
);

INSERT INTO jobs
    (patient_id, department_id, job_type, arrival_time,
     burst_time, priority, status)
SELECT p.patient_id, d.department_id, 'ECG Test',
       1, 3, 1, 'WAITING'
FROM patients p
JOIN departments d ON d.department_name = 'Cardiology'
WHERE p.name = 'Priya Verma'
AND NOT EXISTS (
    SELECT 1 FROM jobs j
    WHERE j.patient_id = p.patient_id
      AND j.job_type = 'ECG Test'
);

INSERT INTO jobs
    (patient_id, department_id, job_type, arrival_time,
     burst_time, priority, status)
SELECT p.patient_id, d.department_id, 'General Checkup',
       2, 4, 2, 'WAITING'
FROM patients p
JOIN departments d ON d.department_name = 'General Medicine'
WHERE p.name = 'Rahul Singh'
AND NOT EXISTS (
    SELECT 1 FROM jobs j
    WHERE j.patient_id = p.patient_id
      AND j.job_type = 'General Checkup'
);

INSERT INTO jobs
    (patient_id, department_id, job_type, arrival_time,
     burst_time, priority, status)
SELECT p.patient_id, d.department_id, 'Follow-up',
       3, 2, 1, 'WAITING'
FROM patients p
JOIN departments d ON d.department_name = 'Emergency'
WHERE p.name = 'Ananya Gupta'
AND NOT EXISTS (
    SELECT 1 FROM jobs j
    WHERE j.patient_id = p.patient_id
      AND j.job_type = 'Follow-up'
);
