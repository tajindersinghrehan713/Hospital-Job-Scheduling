
package hospital;

public class Job {

    private int jobId;
    private int patientId;
    private String jobType;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private String status;

    public Job(int jobId, int patientId, String jobType,
               int arrivalTime, int burstTime,
               int priority, String status) {
        this.jobId = jobId;
        this.patientId = patientId;
        this.jobType = jobType;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.status = status;
    }

    public int getJobId() {
        return jobId;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getJobType() {
        return jobType;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
