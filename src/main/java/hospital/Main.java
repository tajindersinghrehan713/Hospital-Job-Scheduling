
package hospital;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            JobRepository jobRepository = new JobRepository();
            ScheduleRepository scheduleRepository =
                new ScheduleRepository();
            ScheduleReport report = new ScheduleReport();

            List<Job> jobs = jobRepository.getWaitingJobs();

            if (jobs.isEmpty()) {
                System.out.println("No waiting hospital jobs found.");
                return;
            }

            Scheduler scheduler = new Scheduler();

            int runId = scheduleRepository.createRun(
                "Hospital scheduling comparison");

            System.out.println("==================================");
            System.out.println(" HOSPITAL JOB SCHEDULING SYSTEM");
            System.out.println("==================================");
            System.out.println("Run ID: " + runId);
            System.out.println("Jobs loaded: " + jobs.size());

            runAndSave("FCFS", scheduler.fcfs(jobs),
                scheduler, scheduleRepository, runId);

            runAndSave("SJF", scheduler.sjf(jobs),
                scheduler, scheduleRepository, runId);

            runAndSave("SRTF", scheduler.srtf(jobs),
                scheduler, scheduleRepository, runId);

            runAndSave("Priority Scheduling", scheduler.priority(jobs),
                scheduler, scheduleRepository, runId);

            runAndSave("Round Robin", scheduler.roundRobin(jobs, 2),
                scheduler, scheduleRepository, runId);

            report.displayReport();

            System.out.println("\nScheduling completed successfully.");

        } catch (SQLException e) {
            System.out.println("Database operation failed: "
                + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Scheduling input error: "
                + e.getMessage());
        }
    }

    private static void runAndSave(
            String algorithm,
            List<Scheduler.Result> results,
            Scheduler scheduler,
            ScheduleRepository repository,
            int runId) throws SQLException {

        scheduler.display(algorithm, results);
        repository.saveResults(runId, algorithm, results);
        System.out.println("Results saved for " + algorithm);
    }
}
