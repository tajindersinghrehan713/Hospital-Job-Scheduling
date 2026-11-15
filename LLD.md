# Hospital Job Scheduling System

## 1. Project Overview

The Hospital Job Scheduling System simulates how hospitals manage service requests and schedule them using Operating System scheduling algorithms. It uses MySQL to store patient, job, staff, resource, and scheduling information.

## 2. Main Components

### Patient
Stores patient information such as patient ID, name, age, and contact details.

### Job
Represents a service request created for a patient. A patient can have multiple jobs. Each job has an arrival time, burst time, priority, and status.

### Resource
Represents hospital resources such as staff members and equipment. Resources can be allocated to jobs when available.

### Scheduler
Selects and orders jobs using scheduling algorithms:
- FCFS (First Come, First Served)
- SJF (Shortest Job First)
- Priority Scheduling
- Round Robin

### Database
Stores and retrieves application data using MySQL.

## 3. Database Tables

- **Patient:** Stores patient details.
- **Department:** Stores hospital departments.
- **Staff:** Stores staff information.
- **Resource:** Stores equipment and other resources.
- **Job:** Stores service requests and scheduling attributes.
- **Schedule:** Stores scheduling results.
- **Job_Resource:** Connects jobs with required resources.
- **Schedule_Resource:** Records resources assigned during a schedule.

## 4. Basic Workflow

1. Register a patient.
2. Create a service job for the patient.
3. Store the job in the database.
4. Select a scheduling algorithm.
5. Arrange jobs according to the selected algorithm.
6. Check resource availability.
7. Save scheduling results and display performance metrics.

## 5. Operating System Concepts

- Process scheduling simulation
- FCFS, SJF, Priority, and Round Robin
- Waiting time
- Turnaround time
- Completion time
- Resource allocation

## 6. DBMS Concepts

- Primary keys and foreign keys
- One-to-many relationships
- Many-to-many relationships
- CRUD operations
- Data integrity constraints
- Relational database design

## 7. Project Scope

This project is an academic simulation of hospital service scheduling. It does not make real medical decisions or control actual hospital equipment.