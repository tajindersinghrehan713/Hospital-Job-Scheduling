
package hospital;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scheduling")
public class SchedulingController {

    private final JobRepository jobRepository = new JobRepository();
    private final ScheduleRepository scheduleRepository =
        new ScheduleRepository();
    private final Scheduler scheduler = new Scheduler();

    @PostMapping("/run")
public ResponseEntity<?> runScheduling(
        @RequestParam("algorithm") String algorithm,
        @RequestParam(name = "quantum", defaultValue = "2") int quantum) {

        try {
            List<Job> jobs = jobRepository.getWaitingJobs();

            if (jobs.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No waiting jobs found."));
            }

            List<Scheduler.Result> results;

            switch (algorithm) {
                case "FCFS":
                    results = scheduler.fcfs(jobs);
                    break;
                case "SJF":
                    results = scheduler.sjf(jobs);
                    break;
                case "SRTF":
                    results = scheduler.srtf(jobs);
                    break;
                case "Priority Scheduling":
                    results = scheduler.priority(jobs);
                    break;
                case "Round Robin":
                    results = scheduler.roundRobin(jobs, quantum);
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Unsupported algorithm."));
            }

            int runId = scheduleRepository.createRun("Web scheduling: " + algorithm);
scheduleRepository.saveResults(runId, algorithm, results);

List<Map<String, Object>> response = new java.util.ArrayList<>();

for (Scheduler.Result result : results) {
    Map<String, Object> item = new java.util.HashMap<>();

    item.put("scheduleId",
        scheduleRepository.getScheduleId(runId, algorithm, result.jobId));
    item.put("jobId", result.jobId);
    item.put("jobType", result.jobType);
    item.put("startTime", result.startTime);
    item.put("completionTime", result.completionTime);
    item.put("waitingTime", result.waitingTime);
    item.put("turnaroundTime", result.turnaroundTime);
    item.put("responseTime", result.responseTime);

    response.add(item);
}

return ResponseEntity.ok(response);

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage() == null
                    ? "Scheduling failed." : e.getMessage()));
        }
    }
    
@PostMapping("/compare")
public ResponseEntity<?> compareAlgorithms() {
    try {
        List<Job> jobs = jobRepository.getWaitingJobs();

        if (jobs.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "No waiting jobs found."));
        }

        int runId = scheduleRepository.createRun(
            "Comparison of all scheduling algorithms");

        scheduleRepository.saveResults(
            runId, "FCFS", scheduler.fcfs(jobs));

        scheduleRepository.saveResults(
            runId, "SJF", scheduler.sjf(jobs));

        scheduleRepository.saveResults(
            runId, "SRTF", scheduler.srtf(jobs));

        scheduleRepository.saveResults(
            runId, "Priority Scheduling", scheduler.priority(jobs));

        scheduleRepository.saveResults(
            runId, "Round Robin", scheduler.roundRobin(jobs, 2));

        return ResponseEntity.ok(
            Map.of("message", "All five algorithms compared.",
                   "runId", runId));

    } catch (SQLException | IllegalArgumentException e) {
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", e.getMessage() == null
                ? "Comparison failed." : e.getMessage()));
    }
}

}
