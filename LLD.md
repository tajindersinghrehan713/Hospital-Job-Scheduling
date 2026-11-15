
# Low-Level Design (LLD)
# Hospital Job Scheduling System

## 1. Project Overview

The Hospital Job Scheduling System is an academic application that simulates how hospitals manage service requests and schedule them using Operating System scheduling algorithms.

The system uses Java and Spring Boot for backend development, MySQL for database management, and HTML, CSS, and JavaScript for the frontend.

It manages patients, departments, service jobs, staff, and hospital resources. It also demonstrates scheduling algorithms, resource allocation, database transactions, and concurrency control.

> **Project Scope:** This system is an academic scheduling simulation. It does not make real medical decisions or control actual hospital equipment.

---

## 2. Technology Stack

| Component | Technology |
|---|---|
| Programming Language | Java 17 |
| Backend Framework | Spring Boot 3.5.6 |
| Database | MySQL |
| Database Connectivity | JDBC |
| Frontend | HTML, CSS, JavaScript |
| Build Tool | Maven |

---

## 3. System Architecture

The application follows a layered architecture that separates the user interface, request handling, business logic, database operations, and data storage.

### 3.1 Architecture Layers

**1. Presentation Layer**
- Developed using HTML, CSS, and JavaScript.
- Displays the dashboard, hospital jobs, scheduling, reports, and resources.
- Sends HTTP requests to backend APIs.
- Displays scheduling results and resource allocation status.

**2. Controller Layer**
- Handles incoming HTTP requests.
- Connects frontend requests with backend services.
- Returns application data in JSON format.

**3. Service Layer**
- Contains the application's business logic.
- Handles job management, staff management, resource management, scheduling, and allocation.
- Coordinates operations between controllers and repositories.

**4. Data Access Layer**
- Uses JDBC and SQL queries to communicate with MySQL.
- Retrieves, inserts, updates, and deletes database records.
- Handles database operations through repository classes.

**5. Database Layer**
- Uses MySQL to store patient, department, staff, resource, job, and scheduling information.
- Maintains relationships using primary keys and foreign keys.

### 3.2 Request Flow

```text
        User
         |
         v
   HTML / CSS / JS
         |
         v
    HTTP Request
         |
         v
   Spring Boot Controller
         |
         v
     Service Layer
         |
         v
   Repository / JDBC
         |
         v
       MySQL
         |
         v
    JSON Response
         |
         v
     Frontend UI
```

---

## 4. Main System Components

### 4.1 Patient

Stores information about patients registered in the hospital.

**Attributes:**
- Patient ID
- Name
- Age
- Contact details

**Responsibilities:**
- Store patient information.
- Maintain patient records.
- Associate patients with their service jobs.

A patient can have multiple service jobs.

### 4.2 Department

Represents different hospital departments.

**Examples:**
- Emergency
- Cardiology
- General Medicine

**Responsibilities:**
- Store department information.
- Associate staff and jobs with departments.
- Support department-based staff eligibility during allocation.

### 4.3 Job

Represents a service request created for a patient.

**Important attributes:**
- Job ID
- Patient ID
- Department ID
- Arrival Time
- Burst Time
- Priority
- Job Type
- Status

**Responsibilities:**
- Store service request details.
- Maintain scheduling attributes.
- Track job status.
- Associate jobs with patients and departments.
- Define the resources required for a job.

**Job Statuses:**

| Status | Meaning |
|---|---|
| WAITING | Job is waiting for scheduling or allocation. |
| SCHEDULED | Job has scheduling information. |
| IN_PROGRESS | Job is currently allocated and being processed. |
| COMPLETED | Job has been completed and its allocation released. |
| CANCELLED | Job has been cancelled, where applicable. |

The actual application workflow should be understood through the implemented status transitions. A scheduling result alone does not mean that a job has received staff or equipment.

### 4.4 Staff

Represents hospital employees who can be assigned to service jobs.

**Important attributes:**
- Staff ID
- Name
- Department
- Role
- Availability Status

**Staff Roles:**
- Doctor
- Nurse

**Availability Statuses:**

| Status | Meaning |
|---|---|
| AVAILABLE | Staff member can be allocated. |
| BUSY | Staff member is currently allocated. |
| UNAVAILABLE | Staff member cannot be allocated. |

**Responsibilities:**
- Store staff information.
- Maintain availability.
- Support department and role-based eligibility.
- Associate staff with allocated schedules.

### 4.5 Resource

Represents hospital equipment and other resources required to process service jobs.

**Important attributes:**
- Resource ID
- Resource Name
- Resource Type
- Availability Status

**Resource Statuses:**

| Status | Meaning |
|---|---|
| AVAILABLE | Resource is available for allocation. |
| IN_USE | Resource is currently allocated. |
| MAINTENANCE | Resource is unavailable due to maintenance. |

**Responsibilities:**
- Store resource information.
- Track resource availability.
- Associate resources with job requirements.
- Prevent simultaneous allocation of the same resource.

### 4.6 Scheduler

The Scheduler component selects and orders waiting jobs using Operating System scheduling algorithms.

**Supported Algorithms:**

1. First Come, First Served (FCFS)
2. Shortest Job First (SJF)
3. Shortest Remaining Time First (SRTF)
4. Priority Scheduling
5. Round Robin (RR)

**Responsibilities:**
- Retrieve eligible waiting jobs.
- Apply the selected scheduling algorithm.
- Calculate scheduling metrics.
- Generate scheduling results.
- Store results in the database.

The scheduling algorithms are used to demonstrate CPU/process scheduling concepts. Their output is separate from the actual staff and equipment allocation process.

### 4.7 Allocation Service

The Allocation Service handles the operational allocation of staff and resources to jobs.

**Responsibilities:**
- Check job eligibility.
- Identify suitable staff based on department and role.
- Identify required resources based on job type.
- Verify staff and resource availability.
- Allocate staff and resources together.
- Update job, staff, and resource statuses.
- Release allocations after completion.

**Allocation Rule:**

An allocation is successful only when all required staff and resources are available.

If any required staff member or resource is unavailable, the allocation is not partially completed. The job remains waiting for allocation.

---

## 5. Scheduling Algorithms

The system implements five Operating System scheduling algorithms.

### 5.1 First Come, First Served (FCFS)

- Jobs are executed in their arrival order.
- It is a non-preemptive scheduling algorithm.
- A job continues until its burst time is completed.

**Advantages:**
- Simple to implement.
- Easy to understand.
- Maintains arrival order.

**Disadvantage:**
- Long jobs can delay shorter jobs.

### 5.2 Shortest Job First (SJF)

- Selects the job with the shortest burst time.
- It is non-preemptive.
- Once selected, a job completes before another job is selected.

**Advantages:**
- Can reduce average waiting time.

**Disadvantage:**
- Longer jobs may experience starvation.

### 5.3 Shortest Remaining Time First (SRTF)

- Preemptive version of SJF.
- Selects the job with the shortest remaining execution time.
- A running job can be interrupted when a shorter job becomes available.

**Advantages:**
- Responds to newly arriving shorter jobs.
- Can improve average waiting time.

**Disadvantage:**
- Frequent preemption can increase scheduling overhead in real systems.

### 5.4 Priority Scheduling

- Jobs are selected according to their assigned priority.
- The priority value determines scheduling order.
- The implemented priority convention determines whether a smaller or larger value represents higher priority.

**Advantages:**
- Allows priority-based job ordering.

**Disadvantage:**
- Low-priority jobs may experience starvation.

### 5.5 Round Robin (RR)

- Each job receives a fixed time quantum.
- If a job is not completed within its time quantum, it is placed back in the ready queue.
- Scheduling continues until all jobs are completed.

**Advantages:**
- Provides fair CPU time distribution.
- Useful for demonstrating time-sharing.

**Disadvantage:**
- Performance depends on the selected time quantum.

---

## 6. Scheduling Performance Metrics

The system compares algorithms using the following metrics.

### 6.1 Completion Time (CT)

The time at which a job finishes execution.

### 6.2 Turnaround Time (TAT)

Total time taken by a job from arrival until completion.

```text
Turnaround Time = Completion Time - Arrival Time
```

### 6.3 Waiting Time (WT)

Total time a job spends waiting in the ready queue.

```text
Waiting Time = Turnaround Time - Burst Time
```

### 6.4 Response Time (RT)

Time between job arrival and its first execution.

```text
Response Time = First Start Time - Arrival Time
```

### 6.5 Average Metrics

```text
Average Waiting Time =
    Total Waiting Time / Number of Jobs

Average Turnaround Time =
    Total Turnaround Time / Number of Jobs

Average Response Time =
    Total Response Time / Number of Jobs
```

The Reports module displays the average scheduling metrics for the latest recorded run of each algorithm.

---

## 7. Database Design

The system uses a MySQL relational database named:

```text
hospital_scheduling
```

### 7.1 Database Tables

| Table | Purpose |
|---|---|
| patients | Stores patient information. |
| departments | Stores hospital departments. |
| staff | Stores hospital staff information. |
| resources | Stores equipment and other resources. |
| jobs | Stores patient service requests and scheduling attributes. |
| job_resources | Maps jobs to their required resources. |
| schedules | Stores scheduling results. |
| schedule_resources | Records resources assigned to schedules. |
| schedule_staff | Records staff assigned to schedules. |
| scheduling_runs | Stores scheduling run information. |

### 7.2 Entity Relationships

The database uses primary keys and foreign keys to maintain relationships between entities.

**Patient and Job**
- One patient can have multiple jobs.
- Each job belongs to a patient.
- Relationship: One-to-Many.

**Department and Staff**
- One department can have multiple staff members.
- Each staff member belongs to a department.
- Relationship: One-to-Many.

**Department and Job**
- One department can have multiple jobs.
- Each job is associated with a department.
- Relationship: One-to-Many.

**Job and Resource**
- A job can require multiple resources.
- A resource can be associated with multiple job requirements.
- Relationship: Many-to-Many through `job_resources`.

**Schedule and Resource**
- A schedule can have multiple allocated resources.
- A resource can appear in multiple schedules over time.
- Relationship: Many-to-Many through `schedule_resources`.

**Schedule and Staff**
- A schedule can have multiple assigned staff members.
- A staff member can appear in multiple schedules over time.
- Relationship: Many-to-Many through `schedule_staff`.

### 7.3 Database Integrity

The database uses:
- Primary keys for unique record identification.
- Foreign keys for maintaining relationships.
- Data types and constraints for valid values.
- Relational tables to reduce unnecessary duplication.
- SQL queries for CRUD operations.

---

## 8. Java Class Design

The backend is organized under the `hospital` package.

The following classes are responsible for the major application operations.

### 8.1 Database.java

**Purpose:**
- Establishes database connectivity.
- Provides connections to MySQL.
- Centralizes database connection configuration.

### 8.2 Scheduler.java

**Purpose:**
- Implements scheduling algorithms.
- Orders jobs based on the selected algorithm.
- Calculates scheduling metrics.
- Produces scheduling results.

### 8.3 Repository Classes

Repository classes handle database operations.

| Class | Responsibility |
|---|---|
| Patient-related repository | Retrieves and manages patient records. |
| DepartmentRepository | Handles department data. |
| JobRepository | Handles job data and waiting-job retrieval. |
| StaffRepository | Handles staff data. |
| ResourceRepository | Handles resource data. |
| ScheduleRepository | Stores and retrieves scheduling results. |

### 8.4 Service Classes

Service classes contain application-level business logic.

| Class | Responsibility |
|---|---|
| DepartmentService | Department-related operations. |
| JobService | Job management operations. |
| JobStatusService | Job status management. |
| StaffService | Staff management operations. |
| ResourceService | Resource management operations. |
| AllocationService | Staff and resource allocation and release. |

### 8.5 Controller Classes

Controllers expose backend operations through HTTP APIs.

| Class | Responsibility |
|---|---|
| HomeController | Handles the application home page. |
| JobController | Handles job-related API requests. |
| SchedulingController | Handles scheduling operations. |
| ReportController | Provides scheduling performance reports. |
| AllocationController | Handles resource allocation and release. |

The exact responsibilities should follow the corresponding controller and service implementations.

---

## 9. Resource Allocation Design

Scheduling and resource allocation are two separate operations.

The Scheduler determines the execution order of jobs. The Allocation Service checks whether the required staff and resources are actually available.

### 9.1 Allocation Workflow

```text
       Select Job
           |
           v
   Check Job Eligibility
           |
           v
   Find Suitable Staff
           |
           v
   Find Required Resources
           |
           v
   Check Availability
           |
           v
    Are All Available?
        /       \
      Yes        No
       |          |
       v          v
  Allocate     Keep Job
  Staff and    Waiting
  Resources
       |
       v
  Update Status
       |
       v
  Save Allocation
```

### 9.2 Allocation Conditions

A job can be allocated only when:

1. The job is eligible for allocation.
2. Suitable staff are available.
3. All required resources are available.
4. The required records can be locked and updated successfully.

### 9.3 Allocation Status Updates

After successful allocation:

- Staff status changes to `BUSY`.
- Resource status changes to `IN_USE`.
- The job status is updated to reflect its active allocation.
- Allocation records are stored in the database.

### 9.4 Resource Release

When an allocation is released after job completion:

- Staff status changes back to `AVAILABLE`, where applicable.
- Resource status changes back to `AVAILABLE`, where applicable.
- Allocation records are updated.
- The job status changes to `COMPLETED`.

---

## 10. Transaction Management and Concurrency Control

The application uses database transactions and row-level locking to protect allocation operations.

### 10.1 Database Transactions

Allocation operations are performed transactionally.

This means:
- Related database updates are treated as one logical operation.
- If an operation fails, the transaction can be rolled back.
- Partial allocation updates are avoided.

### 10.2 Row-Level Locking

The application uses SQL row locking through:

```sql
SELECT ... FOR UPDATE;
```

This locks selected database rows during a transaction so that competing transactions cannot modify the same locked records simultaneously.

The application uses locking for job, staff, and resource records during allocation.

### 10.3 Consistent Resource Locking Order

Resource records are locked in ascending `resource_id` order.

This reduces the possibility of circular waiting between transactions.

### 10.4 Deadlock Error Handling

The application handles MySQL errors:

- `1213` - Deadlock found when trying to get lock.
- `1205` - Lock wait timeout exceeded.

These errors are handled so that the application can return an appropriate retry-friendly error instead of proceeding with an incomplete allocation.

### 10.5 Concurrency Control Limitations

The implementation includes:
- Transactional allocation.
- Database row locking.
- Consistent resource locking order.
- Handling of deadlock and lock-timeout errors.

These are safeguards against concurrency problems. They do not constitute a formal implementation of every deadlock prevention or detection algorithm, and they do not guarantee that deadlocks can never occur.

---

## 11. API Design

The frontend communicates with the backend through REST-style HTTP endpoints.

### 11.1 Main API Endpoints

| Endpoint | Purpose |
|---|---|
| `/api/jobs` | Retrieves hospital job information. |
| `/api/scheduling/run` | Runs the selected scheduling algorithm. |
| `/api/scheduling/compare` | Compares scheduling algorithms. |
| `/api/reports` | Retrieves scheduling performance reports. |
| `/api/allocations/...` | Handles allocation-related operations. |

The allocation endpoint includes additional operations for allocation and release.

> The exact HTTP methods, request parameters, response structures, and complete allocation paths should be documented from the corresponding controller mappings.

### 11.2 API Request Flow

1. Frontend sends an HTTP request.
2. The corresponding controller receives the request.
3. The service layer performs the required operation.
4. Repository classes communicate with MySQL.
5. The controller returns the response.
6. JavaScript updates the frontend.

---

## 12. Application Workflow

The complete application workflow is:

```text
       Patient Registration
               |
               v
        Create Service Job
               |
               v
       Store Job in MySQL
               |
               v
       Retrieve Waiting Jobs
               |
               v
       Select Scheduling Algorithm
               |
               v
        Run Scheduler
               |
               v
      Calculate Scheduling Metrics
               |
               v
       Save Scheduling Results
               |
               v
       Check Staff and Resource
             Availability
               |
               v
         Allocate Resources
               |
               v
       Update Job and Resource
              Status
               |
               v
        Complete the Job
               |
               v
        Release Resources
               |
               v
       Generate Performance
              Reports
```

---

## 13. Error Handling

The application handles common operational failures, including:

- Invalid or unavailable job records.
- Staff members not being available.
- Required resources not being available.
- Database connection or query failures.
- Transaction failures during allocation.
- MySQL deadlock and lock-wait timeout errors.

The application avoids treating an unsuccessful allocation as a successful one.

---

## 14. Testing and Validation

The following areas should be tested before final project submission.

| Test Area | Validation |
|---|---|
| Patient Management | Patient records can be created and retrieved. |
| Job Management | Jobs are stored and displayed correctly. |
| Scheduling | All five algorithms produce scheduling results. |
| Scheduling Metrics | Waiting, turnaround, response, and completion times are calculated. |
| Resource Allocation | Available staff and resources can be allocated. |
| Resource Availability | Busy or unavailable resources are not allocated again. |
| Resource Release | Released resources become available again. |
| Reports | Latest recorded results for each algorithm are displayed. |
| Database Integrity | Foreign-key relationships remain valid. |
| Concurrency | Allocation operations use transactions and row locking. |

---

## 15. Project Scope and Limitations

### Scope

- Demonstrates Operating System scheduling algorithms.
- Manages hospital patients and service jobs.
- Maintains staff and resource availability.
- Demonstrates relational database design and CRUD operations.
- Demonstrates transactional resource allocation and concurrency control.
- Compares scheduling performance using calculated metrics.

### Limitations

- The system is an academic simulation, not a production hospital management system.
- It does not make clinical decisions or recommend treatments.
- It does not control actual hospital equipment.
- Scheduling algorithms demonstrate OS concepts rather than real clinical decision-making.
- Staff and resource eligibility mappings are application-level assumptions.
- The system does not implement a formal Banker's Algorithm or a complete deadlock detection and recovery algorithm.

---

## 16. Future Enhancements

Possible future improvements include:

- Authentication and role-based access control.
- More detailed job lifecycle management.
- Advanced reporting and visualization.
- Configurable scheduling priorities.
- Improved allocation monitoring.
- Automated testing for concurrent allocation scenarios.
- Audit logs for job and resource status changes.

---

## 17. Conclusion

The Hospital Job Scheduling System demonstrates the application of Operating System scheduling concepts and Database Management System principles in a hospital service-request scenario.

It combines job scheduling, performance measurement, relational database management, resource allocation, transactions, and concurrency safeguards in a single academic application.
