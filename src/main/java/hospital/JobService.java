
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JobService {

    public void registerJob(Job job, int departmentId)
            throws SQLException {

        if (job == null) {
            throw new IllegalArgumentException(
                "Job cannot be null.");
        }

        if (job.getPatientId() <= 0 || departmentId <= 0) {
            throw new IllegalArgumentException(
                "Valid patient and department IDs are required.");
        }

        if (job.getJobType() == null ||
            job.getJobType().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Job type is required.");
        }

        if (job.getArrivalTime() < 0) {
            throw new IllegalArgumentException(
                "Arrival time cannot be negative.");
        }

        if (job.getBurstTime() <= 0) {
            throw new IllegalArgumentException(
                "Burst time must be greater than zero.");
        }

        if (job.getPriority() < 0) {
            throw new IllegalArgumentException(
                "Priority cannot be negative.");
        }

        String sql = "INSERT INTO jobs " +
                     "(patient_id, department_id, job_type, " +
                     "arrival_time, burst_time, priority, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, job.getPatientId());
            ps.setInt(2, departmentId);
            ps.setString(3, job.getJobType().trim());
            ps.setInt(4, job.getArrivalTime());
            ps.setInt(5, job.getBurstTime());
            ps.setInt(6, job.getPriority());
            ps.setString(7, "WAITING");

            ps.executeUpdate();

            System.out.println("Hospital job registered successfully.");
        }
    }
}
