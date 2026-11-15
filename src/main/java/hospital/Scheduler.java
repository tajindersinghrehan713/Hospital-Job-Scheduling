
package hospital;

import java.util.*;

public class Scheduler {

    public static class Result {
        public int jobId;
        public String jobType;
        public int startTime;
        public int completionTime;
        public int waitingTime;
        public int turnaroundTime;
        public int responseTime;

        public Result(int jobId, String jobType, int startTime,
                      int completionTime, int waitingTime,
                      int turnaroundTime, int responseTime) {
            this.jobId = jobId;
            this.jobType = jobType;
            this.startTime = startTime;
            this.completionTime = completionTime;
            this.waitingTime = waitingTime;
            this.turnaroundTime = turnaroundTime;
            this.responseTime = responseTime;
        }
    }

    private void validate(List<Job> jobs) {
        Set<Integer> ids = new HashSet<>();

        for (Job job : jobs) {
            if (job == null)
                throw new IllegalArgumentException("Job cannot be null.");

            if (job.getJobId() <= 0 || !ids.add(job.getJobId()))
                throw new IllegalArgumentException(
                    "Job IDs must be unique and positive.");

            if (job.getArrivalTime() < 0 || job.getBurstTime() <= 0)
                throw new IllegalArgumentException(
                    "Arrival time must be non-negative and burst time positive.");
        }
    }

    private Result createResult(Job job, int start, int finish) {
        int tat = finish - job.getArrivalTime();
        int wt = tat - job.getBurstTime();

        return new Result(job.getJobId(), job.getJobType(),
            start, finish, wt, tat, start - job.getArrivalTime());
    }

    private List<Result> ordered(List<Result> results) {
        results.sort(Comparator.comparingInt((Result r) -> r.startTime)
            .thenComparingInt(r -> r.jobId));
        return results;
    }

    public List<Result> fcfs(List<Job> jobs) {
        validate(jobs);

        List<Job> list = new ArrayList<>(jobs);
        list.sort(Comparator.comparingInt((Job job) -> job.getArrivalTime())
    .thenComparingInt(job -> job.getJobId()));

        List<Result> results = new ArrayList<>();
        int time = 0;

        for (Job job : list) {
            int start = Math.max(time, job.getArrivalTime());
            int finish = start + job.getBurstTime();

            results.add(createResult(job, start, finish));
            time = finish;
        }

        return ordered(results);
    }

    public List<Result> sjf(List<Job> jobs) {
        validate(jobs);

        List<Job> remaining = new ArrayList<>(jobs);
        List<Result> results = new ArrayList<>();
        int time = 0;

        while (!remaining.isEmpty()) {
            Job selected = null;

            for (Job job : remaining) {
                if (job.getArrivalTime() <= time &&
                    (selected == null ||
                     job.getBurstTime() < selected.getBurstTime() ||
                     (job.getBurstTime() == selected.getBurstTime() &&
                      job.getJobId() < selected.getJobId()))) {
                    selected = job;
                }
            }

            if (selected == null) {
                time = remaining.stream()
    .mapToInt(job -> job.getArrivalTime()).min().orElse(time);
                continue;
            }

            int start = time;
            time += selected.getBurstTime();

            results.add(createResult(selected, start, time));
            remaining.remove(selected);
        }

        return ordered(results);
    }

    public List<Result> srtf(List<Job> jobs) {
        validate(jobs);

        List<Result> results = new ArrayList<>();
        Map<Integer, Integer> remaining = new HashMap<>();
        Map<Integer, Integer> firstStart = new HashMap<>();
        Map<Integer, Integer> finishTime = new HashMap<>();

        for (Job job : jobs)
            remaining.put(job.getJobId(), job.getBurstTime());

        int completed = 0;
        int time = jobs.stream()
    .mapToInt(job -> job.getArrivalTime()).min().orElse(0);

        while (completed < jobs.size()) {
            Job selected = null;

            for (Job job : jobs) {
                int left = remaining.get(job.getJobId());

                if (job.getArrivalTime() <= time && left > 0 &&
                    (selected == null ||
                     left < remaining.get(selected.getJobId()) ||
                     (left == remaining.get(selected.getJobId()) &&
                      job.getJobId() < selected.getJobId()))) {
                    selected = job;
                }
            }

            if (selected == null) {
                time++;
                continue;
            }

            firstStart.putIfAbsent(selected.getJobId(), time);
            int id = selected.getJobId();

            remaining.put(id, remaining.get(id) - 1);
            time++;

            if (remaining.get(id) == 0) {
                finishTime.put(id, time);
                completed++;
            }
        }

        for (Job job : jobs) {
            int id = job.getJobId();
            results.add(createResult(job,
                firstStart.get(id), finishTime.get(id)));
        }

        return ordered(results);
    }

    public List<Result> priority(List<Job> jobs) {
        validate(jobs);

        List<Job> remaining = new ArrayList<>(jobs);
        List<Result> results = new ArrayList<>();
        int time = 0;

        while (!remaining.isEmpty()) {
            Job selected = null;

            for (Job job : remaining) {
                if (job.getArrivalTime() <= time &&
                    (selected == null ||
                     job.getPriority() < selected.getPriority() ||
                     (job.getPriority() == selected.getPriority() &&
                      job.getJobId() < selected.getJobId()))) {
                    selected = job;
                }
            }

            if (selected == null) {
                time = remaining.stream()
    .mapToInt(job -> job.getArrivalTime()).min().orElse(time);
                continue;
            }

            int start = time;
            time += selected.getBurstTime();

            results.add(createResult(selected, start, time));
            remaining.remove(selected);
        }

        return ordered(results);
    }

    public List<Result> roundRobin(List<Job> jobs, int quantum) {
        validate(jobs);

        if (quantum <= 0)
            throw new IllegalArgumentException(
                "Time quantum must be greater than zero.");

        List<Job> list = new ArrayList<>(jobs);
        list.sort(Comparator.comparingInt((Job job) -> job.getArrivalTime())
    .thenComparingInt(job -> job.getJobId()));

        Map<Integer, Integer> remaining = new HashMap<>();
        Map<Integer, Integer> firstStart = new HashMap<>();
        Map<Integer, Integer> finishTime = new HashMap<>();
        Queue<Job> queue = new LinkedList<>();

        for (Job job : list)
            remaining.put(job.getJobId(), job.getBurstTime());

        List<Result> results = new ArrayList<>();
        int time = 0, index = 0, completed = 0;

        while (completed < list.size()) {
            while (index < list.size() &&
                   list.get(index).getArrivalTime() <= time) {
                queue.add(list.get(index++));
            }

            if (queue.isEmpty()) {
                time = list.get(index).getArrivalTime();
                continue;
            }

            Job job = queue.poll();
            int id = job.getJobId();

            firstStart.putIfAbsent(id, time);

            int run = Math.min(quantum, remaining.get(id));
            time += run;
            remaining.put(id, remaining.get(id) - run);

            while (index < list.size() &&
                   list.get(index).getArrivalTime() <= time) {
                queue.add(list.get(index++));
            }

            if (remaining.get(id) > 0) {
                queue.add(job);
            } else {
                finishTime.put(id, time);
                completed++;
            }
        }

        for (Job job : list) {
            int id = job.getJobId();
            results.add(createResult(job,
                firstStart.get(id), finishTime.get(id)));
        }

        return ordered(results);
    }

    public void display(String algorithm, List<Result> results) {
        System.out.println("\n--- " + algorithm + " ---");
        System.out.printf(
            "%-6s %-20s %-8s %-8s %-8s %-8s %-8s%n",
            "Job", "Type", "Start", "CT", "TAT", "WT", "RT");

        double totalWT = 0, totalTAT = 0, totalRT = 0;

        for (Result r : results) {
            System.out.printf(
                "%-6d %-20s %-8d %-8d %-8d %-8d %-8d%n",
                r.jobId, r.jobType, r.startTime,
                r.completionTime, r.turnaroundTime,
                r.waitingTime, r.responseTime);

            totalWT += r.waitingTime;
            totalTAT += r.turnaroundTime;
            totalRT += r.responseTime;
        }

        if (!results.isEmpty()) {
            int size = results.size();
            System.out.printf("Average WT: %.2f%n", totalWT / size);
            System.out.printf("Average TAT: %.2f%n", totalTAT / size);
            System.out.printf("Average RT: %.2f%n", totalRT / size);
        }
    }
}
