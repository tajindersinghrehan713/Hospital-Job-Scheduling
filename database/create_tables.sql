
USE hospital_scheduling;

CREATE TABLE IF NOT EXISTS patients (
    patient_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT,
    gender VARCHAR(20),
    contact VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS staff (
    staff_id INT PRIMARY KEY AUTO_INCREMENT,
    department_id INT,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    availability_status VARCHAR(20) DEFAULT 'AVAILABLE',
    FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);

CREATE TABLE IF NOT EXISTS resources (
    resource_id INT PRIMARY KEY AUTO_INCREMENT,
    department_id INT,
    resource_name VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50),
    availability_status VARCHAR(20) DEFAULT 'AVAILABLE',
    FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);

CREATE TABLE IF NOT EXISTS jobs (
    job_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    department_id INT NOT NULL,
    job_type VARCHAR(100) NOT NULL,
    arrival_time INT NOT NULL,
    burst_time INT NOT NULL,
    priority INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    FOREIGN KEY (patient_id)
        REFERENCES patients(patient_id),
    FOREIGN KEY (department_id)
        REFERENCES departments(department_id),
    CHECK (arrival_time >= 0),
    CHECK (burst_time > 0),
    CHECK (priority >= 0)
);

CREATE TABLE IF NOT EXISTS job_resources (
    job_id INT,
    resource_id INT,
    quantity_required INT NOT NULL DEFAULT 1,
    PRIMARY KEY (job_id, resource_id),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id),
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id),
    CHECK (quantity_required > 0)
);

CREATE TABLE IF NOT EXISTS schedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    job_id INT NOT NULL,
    algorithm VARCHAR(50) NOT NULL,
    start_time INT NOT NULL,
    completion_time INT NOT NULL,
    waiting_time INT NOT NULL,
    turnaround_time INT NOT NULL,
    response_time INT NOT NULL,
    FOREIGN KEY (job_id) REFERENCES jobs(job_id),
    CHECK (start_time >= 0),
    CHECK (completion_time >= start_time),
    CHECK (waiting_time >= 0),
    CHECK (turnaround_time >= 0),
    CHECK (response_time >= 0)
);

CREATE TABLE IF NOT EXISTS schedule_resources (
    schedule_id INT,
    resource_id INT,
    quantity_allocated INT NOT NULL DEFAULT 1,
    PRIMARY KEY (schedule_id, resource_id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(schedule_id),
    FOREIGN KEY (resource_id) REFERENCES resources(resource_id),
    CHECK (quantity_allocated > 0)
);
