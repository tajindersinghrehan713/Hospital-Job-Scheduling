
# High-Level Design (HLD)
# Hospital Job Scheduling System

## 1. Introduction

The Hospital Job Scheduling System is an academic application designed to simulate the management and scheduling of hospital service requests.

The system manages patients, departments, staff, jobs, and hospital resources. It uses Operating System scheduling algorithms to determine the execution order of waiting jobs and demonstrates resource allocation using database availability checks.

The application uses Java and Spring Boot for backend processing, MySQL for data storage, and HTML, CSS, and JavaScript for the frontend.

> **Scope:** This project is an academic simulation. It does not make real medical decisions or control actual hospital equipment.

## 2. Objectives

The main objectives of the system are:

- Manage patient and hospital service-job information.
- Demonstrate Operating System scheduling algorithms.
- Calculate and compare scheduling performance metrics.
- Manage staff and resource availability.
- Allocate available staff and resources to eligible jobs.
- Store scheduling and allocation information in a relational database.
- Demonstrate database transactions and concurrency control.

## 3. Technology Stack

| Layer | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java 17 |
| Framework | Spring Boot 3.5.6 |
| Database | MySQL |
| Database Connectivity | JDBC |
| Build Tool | Maven |

## 4. System Architecture

The application follows a layered architecture. Each layer has a specific responsibility, making the system easier to maintain and extend.

### 4.1 Architecture Diagram

```text
                  +----------------------+
                  |         USER         |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  |   PRESENTATION LAYER |
                  |    HTML / CSS / JS   |
                  +----------+-----------+
                             |
                        HTTP / JSON
                             |
                             v
                  +----------------------+
                  |    CONTROLLER LAYER  |
                  |   Spring Boot APIs  |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  |     SERVICE LAYER    |
                  |                      |
                  | Job Management       |
                  | Scheduling           |
                  | Resource Allocation  |
                  | Staff Management     |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  |   DATA ACCESS LAYER  |
                  |      JDBC / SQL      |
                  +----------+-----------+
                             |
                             v
                  +----------------------+
                  |     DATABASE LAYER   |
                  |        MySQL         |
                  +----------------------+
```

### 4.2 Layer Responsibilities

**Presentation Layer**
- Provides the user interface.
- Displays jobs, scheduling results, reports, and resource information.
- Communicates with backend APIs.

**Controller Layer**
- Receives HTTP requests.
- Invokes appropriate backend operations.
- Returns responses in JSON format.

**Service Layer**
- Implements application business logic.
- Manages jobs, staff, resources, scheduling, and allocation.
- Coordinates database operations.

**Data Access Layer**
- Executes SQL queries through JDBC.
- Performs database CRUD operations.
- Retrieves and updates application records.

**Database Layer**
- Stores application data in MySQL.
- Maintains relationships and data integrity.
- Stores scheduling results and allocation records.

## 5. Major System Modules

### 5.1 Patient Management

- Registers and stores patient information.
- Maintains patient records.
- Associates patients with service jobs.

### 5.2 Job Management

- Creates and manages hospital service requests.
- Stores arrival time, burst time, priority, and job status.
- Associates jobs with patients and departments.
- Maintains job lifecycle information.

### 5.3 Scheduling Module

The scheduling module implements five Operating System scheduling algorithms:

1. First Come, First Served (FCFS)
2. Shortest Job First (SJF)
3. Shortest Remaining Time First (SRTF)
4. Priority Scheduling
5. Round Robin (RR)

It retrieves waiting jobs, applies the selected algorithm, calculates performance metrics, and stores scheduling results.

### 5.4 Resource Allocation Module

- Checks staff eligibility based on department and role.
- Identifies resources required by a job.
- Checks the availability of staff and resources.
- Allocates them when all requirements are satisfied.
- Updates their availability status.
- Releases allocations after job completion.

### 5.5 Reports Module

- Retrieves stored scheduling results.
- Compares scheduling algorithms.
- Displays average waiting time.
- Displays average turnaround time.
- Displays average response time.

### 5.6 Resource Management Module

- Maintains hospital resource information.
- Tracks available, in-use, and maintenance resources.
- Displays active allocations.
- Supports resource release.

## 6. High-Level System Workflow

The application follows this workflow:

```text
        +----------------------+
        |   Patient Register   |
        +----------+-----------+
                   |
                   v
        +----------------------+
        |    Create Job        |
        +----------+-----------+
                   |
                   v
        +----------------------+
        |   Store Job in DB    |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Retrieve Waiting Jobs|
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Select Scheduling    |
        | Algorithm            |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Run Scheduler        |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Calculate Scheduling |
        | Metrics              |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Save Scheduling      |
        | Results              |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Check Staff and      |
        | Resource Availability|
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Allocate Resources   |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Complete Job and     |
        | Release Resources    |
        +----------+-----------+
                   |
                   v
        +----------------------+
        | Generate Reports     |
        +----------------------+
```

## 7. Scheduling Design

The system uses Operating System scheduling algorithms to simulate the execution order of hospital service jobs.

| Algorithm | Scheduling Approach |
|---|---|
| FCFS | Jobs execute in arrival order. |
| SJF | Job with the shortest burst time is selected. |
| SRTF | Job with the shortest remaining time is selected. |
| Priority Scheduling | Jobs are selected according to priority. |
| Round Robin | Jobs receive CPU time in fixed time slices. |

### Scheduling Metrics

The system calculates:

- Waiting Time
- Turnaround Time
- Completion Time
- Response Time

The Reports module uses these metrics to compare algorithm performance.

**Important:** Scheduling determines job execution order. It does not automatically guarantee that staff and equipment are available.

## 8. Database Architecture

The application uses a MySQL relational database named `hospital_scheduling`.

### 8.1 Main Database Entities

| Entity | Description |
|---|---|
| Patient | Stores patient details. |
| Department | Stores hospital departments. |
| Job | Represents a patient service request. |
| Staff | Stores hospital staff information. |
| Resource | Stores equipment and other resources. |
| Schedule | Stores scheduling results. |
| Scheduling Run | Stores scheduling execution information. |

### 8.2 Database Relationships

- One patient can have multiple jobs.
- One department can have multiple staff members.
- One department can have multiple jobs.
- A job can require multiple resources.
- A resource can be associated with multiple job requirements.
- A schedule can have multiple staff and resources assigned over time.

Many-to-many relationships are maintained using junction tables.

### 8.3 Database Design Principles

- Primary keys provide unique identification.
- Foreign keys maintain relationships.
- Relational tables maintain data consistency.
- CRUD operations manage application records.
- Transactions protect related allocation updates.

For detailed table structures and relationships, refer to `LLD.md`.

## 9. Resource Allocation and Concurrency

The system separates scheduling from actual resource allocation.

The scheduling module determines the job order, while the allocation module checks staff and resource availability.

### 9.1 Allocation Conditions

A job is allocated only when:

- Suitable staff are available.
- All required resources are available.
- The database allocation transaction succeeds.

If any required staff member or resource is unavailable, the allocation is not partially completed.

### 9.2 Concurrency Control

The system uses:

- Database transactions.
- SQL row-level locking using `SELECT ... FOR UPDATE`.
- Consistent resource locking order.
- Handling of MySQL deadlock and lock-wait timeout errors.

These mechanisms reduce concurrency problems and help maintain data consistency.

They are implementation safeguards, not a formal guarantee that deadlocks cannot occur.

## 10. System Scope and Limitations

### Scope

- Academic simulation of hospital service-job scheduling.
- Implementation of five OS scheduling algorithms.
- Patient, job, department, staff, and resource management.
- Resource allocation and release.
- Scheduling performance comparison.
- Database transaction and concurrency concepts.

### Limitations

- The system is not a production hospital management system.
- It does not make clinical decisions.
- It does not control real medical equipment.
- Scheduling algorithms demonstrate OS concepts rather than real clinical decision-making.
- Staff and resource mappings are application-level assumptions.

## 11. Future Enhancements

Possible future improvements include:

- User authentication and role-based access.
- Advanced reporting and visual analytics.
- Configurable scheduling priorities.
- Improved allocation monitoring.
- Automated concurrency testing.
- Detailed audit logs and job history.

## 12. Conclusion

The Hospital Job Scheduling System combines Operating System scheduling concepts with Database Management System principles.

It demonstrates how service jobs can be ordered using scheduling algorithms, how staff and resources can be allocated based on availability, and how database transactions and concurrency safeguards help maintain consistent application data.

For implementation details, refer to [LLD.md](LLD.md).
