
# Hospital Job Scheduling System

An academic project that simulates how hospital service requests can be scheduled using Operating System scheduling algorithms. Patient, job, staff, resource, and scheduling data are stored in a MySQL relational database.

> **Scope:** This project is a scheduling simulation for academic purposes. It does not make medical decisions or control real hospital equipment.

## Features

- Register patients and create service jobs.
- Store and manage departments, staff, and hospital resources.
- Schedule waiting jobs using:
  - First Come, First Served (FCFS)
  - Shortest Job First (SJF)
  - Shortest Remaining Time First (SRTF)
  - Priority Scheduling
  - Round Robin
- Check staff and resource availability during allocation.
- Save scheduling runs and display performance reports.
- Track job and resource allocation status.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend development |
| Spring Boot | Backend framework |
| MySQL | Relational database |
| JDBC | Database connectivity |
| HTML | Frontend structure |
| CSS | Frontend styling |
| JavaScript | Frontend functionality |
| Maven | Build and dependency management |

## High-Level Workflow

1. Register a patient.
2. Create a service job for that patient.
3. Store the job and its scheduling attributes in MySQL.
4. Select a scheduling algorithm.
5. Retrieve waiting jobs and calculate their scheduling order.
6. Check staff and resource availability before allocation.
7. Save scheduling results and display performance metrics.
8. Release allocated resources when the job is completed.

## Scheduling Algorithms

| Algorithm | Description |
|---|---|
| FCFS | Executes jobs in their arrival order. |
| SJF | Selects the job with the shortest burst time. |
| SRTF | Selects the job with the shortest remaining execution time. |
| Priority Scheduling | Executes jobs according to their priority. |
| Round Robin | Allocates CPU time to jobs using a fixed time quantum. |

## Scheduling Metrics

- **Waiting Time:** Time a job spends waiting before execution.
- **Turnaround Time:** Total time from arrival until completion.
- **Completion Time:** Time at which a job finishes.
- **Response Time:** Time from arrival until the job first receives service.

The exact metric values depend on the jobs included in a scheduling run.

## Database Design

The application uses a relational MySQL database with the following main tables:

| Table | Purpose |
|---|---|
| `patients` | Stores patient details. |
| `departments` | Stores hospital departments. |
| `staff` | Stores staff information. |
| `resources` | Stores equipment and other allocatable resources. |
| `jobs` | Stores patient service requests and scheduling attributes. |
| `schedules` | Stores scheduling results. |
| `job_resources` | Maps jobs to their required resources. |
| `schedule_resources` | Records resources assigned during scheduling. |
| `schedule_staff` | Records staff assigned to schedules. |
| `scheduling_runs` | Stores scheduling run information. |

The schema uses primary keys, foreign keys, relational relationships, and constraints to maintain data integrity.

## Project Structure

The backend is organized into Java classes for entities, repositories, services, controllers, scheduling logic, and allocation logic.

The frontend is served from Spring Boot's static resources.

```text
HospitalJobScheduling/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── hospital/
│       │       ├── Database.java
│       │       ├── Scheduler.java
│       │       ├── entities/
│       │       ├── repositories/
│       │       ├── services/
│       │       └── controllers/
│       │
│       └── resources/
│           └── static/
│               ├── index.html
│               ├── script.js
│               └── style.css
│
├── pom.xml
├── LLD.md
└── README.md
```

*Note: The structure above is a high-level representation. The actual Java classes are organized directly under the `hospital` package.*

## Prerequisites

Install the following before running the project:

- Java 17
- Maven
- MySQL Server
- MySQL Workbench (optional)

## Setup and Run

### 1. Clone the Repository

```bash
git clone <repository-url>
cd HospitalJobScheduling
```

### 2. Create the Database

Open MySQL Workbench and execute:

```sql
CREATE DATABASE hospital_scheduling;
```

Create the required tables using the project's SQL schema or database setup script.

### 3. Configure Database Connectivity

Update the database connection settings in `Database.java` with your local MySQL details.

Do not commit personal database passwords or other credentials to a public repository.

### 4. Build the Project

Run this command from the project root:

```bash
mvn install
```

### 5. Start the Application

```bash
mvn spring-boot:run
```

### 6. Open the Application

Visit the following URL in your browser:

```text
http://localhost:8080/
```

## Using the Application

1. Open the dashboard to view job and scheduling information.
2. Use **Hospital Jobs** to view service jobs.
3. Open **Scheduling**, choose an algorithm, and run the scheduling simulation.
4. Allocate resources to eligible jobs when staff and equipment are available.
5. Use **Resources** to inspect allocations and release resources after completion.
6. Open **Reports** to compare the latest recorded results for each scheduling algorithm.

## Concepts Demonstrated

### Operating Systems

- Process/job scheduling simulation
- FCFS, SJF, SRTF, Priority Scheduling, and Round Robin
- Waiting time, turnaround time, completion time, and response time
- Resource allocation and availability
- Synchronization and concurrency control

### Database Management Systems

- Relational database design
- Primary keys and foreign keys
- One-to-many and many-to-many relationships
- CRUD operations
- Data integrity constraints
- SQL queries and JDBC connectivity
- Transactional resource allocation and concurrency control

## Project Documentation

For detailed design information, refer to [LLD.md](LLD.md).

## Limitations

- This is an academic simulation, not a production hospital information system.
- Scheduling results depend on the input jobs and selected algorithm.
- The system does not provide clinical recommendations or make treatment decisions.
- Staff and resource mappings are application-level assumptions for demonstrating allocation.

## Contributors

- Yash Diwakar
- Dipesh Joshi
- Shruti Madhu
- Tajinder Singh
