
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobRepository {

    public List<Job> getWaitingJobs() throws SQLException {
        List<Job> jobs = new ArrayList<>();

        String sql = "SELECT job_id, patient_id, job_type, " +
                     "arrival_time, burst_time, priority, status " +
                     "FROM jobs WHERE status = ? " +
                     "ORDER BY arrival_time, job_id";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "WAITING");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Job job = new Job(
                        rs.getInt("job_id"),
                        rs.getInt("patient_id"),
                        rs.getString("job_type"),
                        rs.getInt("arrival_time"),
                        rs.getInt("burst_time"),
                        rs.getInt("priority"),
                        rs.getString("status")
                    );

                    jobs.add(job);
                }
            }
        }

        return jobs;
    }
    public List<Job> getAllJobs() throws SQLException {
        List<Job> jobs = new ArrayList<>();

        String sql = "SELECT job_id, patient_id, job_type, " +
                    "arrival_time, burst_time, priority, status " +
                    "FROM jobs ORDER BY arrival_time, job_id";

        try (Connection con = Database.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Job job = new Job(
                    rs.getInt("job_id"),
                    rs.getInt("patient_id"),
                    rs.getString("job_type"),
                    rs.getInt("arrival_time"),
                    rs.getInt("burst_time"),
                    rs.getInt("priority"),
                    rs.getString("status")
                );

                jobs.add(job);
            }
        }

        return jobs;
    }
}
