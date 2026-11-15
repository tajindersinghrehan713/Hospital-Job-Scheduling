
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ScheduleRepository {

    public int createRun(String description) throws SQLException {
        String sql = "INSERT INTO scheduling_runs (description) VALUES (?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, description);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to create scheduling run.");
    }

    public void saveResults(int runId, String algorithm,
                            List<Scheduler.Result> results)
            throws SQLException {

        String sql = "INSERT INTO schedules " +
            "(job_id, algorithm, start_time, completion_time, " +
            "waiting_time, turnaround_time, response_time, run_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (Scheduler.Result r : results) {
                ps.setInt(1, r.jobId);
                ps.setString(2, algorithm);
                ps.setInt(3, r.startTime);
                ps.setInt(4, r.completionTime);
                ps.setInt(5, r.waitingTime);
                ps.setInt(6, r.turnaroundTime);
                ps.setInt(7, r.responseTime);
                ps.setInt(8, runId);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
    public int getScheduleId(int runId, String algorithm, int jobId)
        throws SQLException {

    String sql = "SELECT schedule_id FROM schedules " +
                 "WHERE run_id = ? AND algorithm = ? AND job_id = ?";

    try (Connection con = Database.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, runId);
        ps.setString(2, algorithm);
        ps.setInt(3, jobId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("schedule_id");
            }
        }
    }

    throw new SQLException("Schedule ID not found.");
}
}
