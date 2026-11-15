
const API_URL = "/api/jobs";

const navButtons = document.querySelectorAll(".nav-btn");
const pages = document.querySelectorAll(".page");
const pageTitle = document.getElementById("page-title");

// Keeps Scheduling buttons synchronized with allocation and job status.
const schedulingActionButtons = new Map();

function getSchedulingButtonKey(scheduleId, jobId) {
    return `${scheduleId}-${jobId}`;
}

async function syncSchedulingButtons(allocations) {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) return;

        const jobs = await response.json();
        const activeKeys = new Set(
            allocations.map(allocation =>
                getSchedulingButtonKey(
                    allocation.scheduleId,
                    allocation.jobId
                )
            )
        );
        const completedJobs = new Set(
            jobs
                .filter(job => job.status === "COMPLETED")
                .map(job => String(job.jobId))
        );

        schedulingActionButtons.forEach((button, key) => {
            const jobId = key.substring(key.lastIndexOf("-") + 1);

            if (completedJobs.has(jobId)) {
                button.textContent = "Completed";
                button.disabled = true;
            } else if (activeKeys.has(key)) {
                button.textContent = "Release Resources";
                button.disabled = false;
            } else {
                button.textContent = "Allocate";
                button.disabled = false;
            }
        });
    } catch (error) {
        // Keep the current button states if synchronization fails.
    }
}

navButtons.forEach(button => {
    button.addEventListener("click", () => {
        const pageName = button.dataset.page;

        navButtons.forEach(item => item.classList.remove("active"));
        button.classList.add("active");

        pages.forEach(page => page.classList.add("hidden"));
        document.getElementById(pageName).classList.remove("hidden");

        pageTitle.textContent = button.textContent.trim();
    });
});

function createJobRow(job, includePatient = false) {
    const row = document.createElement("tr");

    const values = includePatient
        ? [job.jobId, job.patientId, job.jobType, job.arrivalTime,
           job.burstTime, job.priority, job.status]
        : [job.jobId, job.jobType, job.arrivalTime,
           job.burstTime, job.priority, job.status];

    values.forEach((value, index) => {
        const cell = document.createElement("td");

        if (value === "WAITING") {
            const badge = document.createElement("span");
            badge.className = "badge";
            badge.textContent = value;
            cell.appendChild(badge);
        } else {
            cell.textContent = value ?? "";
        }

        row.appendChild(cell);
    });

    return row;
}

async function loadJobs() {
    const dashboardTable = document.getElementById("dashboard-jobs");
    const allJobsTable = document.getElementById("all-jobs");

    dashboardTable.replaceChildren();
    allJobsTable.replaceChildren();

    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Unable to retrieve hospital jobs.");
        }

        const jobs = await response.json();

        document.getElementById("total-jobs").textContent = jobs.length;
        document.getElementById("waiting-jobs").textContent =
            jobs.filter(job => job.status === "WAITING").length;

        jobs.forEach(job => {
            dashboardTable.appendChild(createJobRow(job));
            allJobsTable.appendChild(createJobRow(job, true));
        });
    } catch (error) {
        document.getElementById("total-jobs").textContent = "—";
        document.getElementById("waiting-jobs").textContent = "—";

        const message = document.createElement("p");
        message.textContent =
            "Could not load jobs. Check that the Spring Boot API and MySQL are running.";

        dashboardTable.appendChild(document.createElement("tr"))
            .appendChild(document.createElement("td"))
            .appendChild(message);
    }
}

const algorithmSelect = document.getElementById("algorithm");
const quantumField = document.getElementById("quantum-field");

algorithmSelect.addEventListener("change", () => {
    quantumField.classList.toggle(
        "hidden",
        algorithmSelect.value !== "Round Robin"
    );
});


document.getElementById("run-scheduling").addEventListener("click", async () => {
    const algorithm = document.getElementById("algorithm").value;
    const quantum = document.getElementById("quantum").value;
    const message = document.getElementById("schedule-message");
    const button = document.getElementById("run-scheduling");
    const table = document.getElementById("schedule-results");

    table.replaceChildren();
    schedulingActionButtons.clear();
    message.textContent = "Running scheduling algorithm...";
    button.disabled = true;

    try {
        const params = new URLSearchParams({ algorithm });

        if (algorithm === "Round Robin") {
            if (!Number.isInteger(Number(quantum)) || Number(quantum) <= 0) {
                throw new Error("Time quantum must be a positive integer.");
            }

            params.set("quantum", quantum);
        }

        const response = await fetch(
            "/api/scheduling/run?" + params.toString(),
            { method: "POST" }
        );

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "Scheduling failed.");
        }

        const results = await response.json();

        
        results.forEach(result => {
            const row = document.createElement("tr");

            [
                result.jobId,
                result.jobType,
                result.startTime,
                result.completionTime,
                result.turnaroundTime,
                result.waitingTime,
                result.responseTime
            ].forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });

            const actionCell = document.createElement("td");
            const actionButton = document.createElement("button");

            actionButton.textContent = "Allocate";
            schedulingActionButtons.set(
                getSchedulingButtonKey(result.scheduleId, result.jobId),
                actionButton
            );

            
actionButton.addEventListener("click", async () => {
    actionButton.disabled = true;

    const isAllocated =
        actionButton.textContent === "Release Resources";

    actionButton.textContent = isAllocated
        ? "Releasing..."
        : "Allocating...";

    try {
        const params = new URLSearchParams({
            scheduleId: result.scheduleId,
            jobId: result.jobId
        });

        const endpoint = isAllocated
            ? "/api/allocations/release?"
            : "/api/allocations/allocate?";

        const response = await fetch(
            endpoint + params.toString(),
            { method: "POST" }
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || "Operation failed.");
        }

        const isWaiting = data.message.includes("WAITING");

        if (isAllocated) {
            actionButton.textContent = "Completed";
        } else if (isWaiting) {
            actionButton.textContent = "Allocate";
        } else {
            actionButton.textContent = "Release Resources";
        }

        actionButton.disabled = isAllocated ? true : false;

        message.textContent = data.message;

        await loadJobs();
        await loadStaff();
        await loadResources();
        await loadActiveAllocations();

    } catch (error) {
        actionButton.textContent = isAllocated
            ? "Release Resources"
            : "Allocate";

        actionButton.disabled = false;

        message.textContent = "Error: " + error.message;
    }
});

            actionCell.appendChild(actionButton);
            row.appendChild(actionCell);
            table.appendChild(row);
        });

        message.textContent =
            "Scheduling completed. " + results.length + " jobs processed.";
    } catch (error) {
        message.textContent = "Error: " + error.message;
    } finally {
        button.disabled = false;
    }
});


loadJobs();

async function loadReports() {
    const table = document.getElementById("report-results");
    const message = document.getElementById("report-message");

    table.replaceChildren();
    message.textContent = "Loading report...";

    try {
        const response = await fetch("/api/reports");

        if (!response.ok) {
            throw new Error("Could not load the report.");
        }

        const reports = await response.json();

        reports.forEach(report => {
            const row = document.createElement("tr");

            [
                report.algorithm,
                report.totalJobs,
                report.avgWT,
                report.avgTAT,
                report.avgRT
            ].forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });

            table.appendChild(row);
        });

        message.textContent = reports.length
            ? "Report loaded successfully."
            : "No report found. Run the algorithm comparison first.";

    } catch (error) {
        message.textContent = error.message;
    }
}


async function loadStaff() {
    const table = document.getElementById("staff-results");
    table.replaceChildren();

    try {
        const response = await fetch("/api/allocations/staff");
        if (!response.ok) throw new Error("Unable to load staff.");

        const staff = await response.json();

        staff.forEach(member => {
            const row = document.createElement("tr");

            [
                member.staffId,
                member.name,
                member.role,
                member.departmentId,
                member.availabilityStatus
            ].forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            table.appendChild(row);
        });
    } catch (error) {
        table.innerHTML =
            "<tr><td colspan='5'>Unable to load staff.</td></tr>";
    }
}

async function loadResources() {
    const table = document.getElementById("resource-results");
    table.replaceChildren();

    try {
        const response = await fetch("/api/allocations/resources");
        if (!response.ok) throw new Error("Unable to load resources.");

        const resources = await response.json();

        resources.forEach(resource => {
            const row = document.createElement("tr");

            [
                resource.resourceId,
                resource.resourceName,
                resource.resourceType,
                resource.departmentId,
                resource.availabilityStatus
            ].forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            table.appendChild(row);
        });
    } catch (error) {
        table.innerHTML =
            "<tr><td colspan='5'>Unable to load resources.</td></tr>";
    }
}


async function loadActiveAllocations() {
    const table = document.getElementById("active-allocations");
    const message = document.getElementById("allocation-message");

    table.replaceChildren();
    message.textContent = "Loading allocations...";

    try {
        const response = await fetch("/api/allocations/active");

        if (!response.ok) {
            throw new Error("Unable to load active allocations.");
        }

        const allocations = await response.json();
        await syncSchedulingButtons(allocations);

        if (allocations.length === 0) {
            message.textContent = "No active allocations.";
            return;
        }

        allocations.forEach(allocation => {
            const row = document.createElement("tr");

            [
                allocation.jobId,
                allocation.scheduleId,
                allocation.staffName,
                allocation.resourceName,
                allocation.algorithm
            ].forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            const actionCell = document.createElement("td");
            const releaseButton = document.createElement("button");

            releaseButton.textContent = "Release";

            releaseButton.addEventListener("click", async () => {
                releaseButton.disabled = true;
                releaseButton.textContent = "Releasing...";

                try {
                    const params = new URLSearchParams({
                        scheduleId: allocation.scheduleId,
                        jobId: allocation.jobId
                    });

                    const releaseResponse = await fetch(
                        "/api/allocations/release?" + params.toString(),
                        { method: "POST" }
                    );

                    const data = await releaseResponse.json();

                    if (!releaseResponse.ok) {
                        throw new Error(data.error || "Release failed.");
                    }

                    message.textContent = data.message;

                    await loadJobs();
                    await loadStaff();
                    await loadResources();
                    await loadActiveAllocations();

                } catch (error) {
                    message.textContent = "Error: " + error.message;
                    releaseButton.disabled = false;
                    releaseButton.textContent = "Release";
                }
            });

            actionCell.appendChild(releaseButton);
            row.appendChild(actionCell);
            table.appendChild(row);
        });

        message.textContent =
            allocations.length + " active allocation(s).";

    } catch (error) {
        message.textContent = error.message;
    }
}


document.querySelector('[data-page="resources"]')
    .addEventListener("click", () => {
        loadStaff();
        loadResources();
        loadActiveAllocations();
    });


document.querySelector('[data-page="reports"]')
    .addEventListener("click", loadReports);
