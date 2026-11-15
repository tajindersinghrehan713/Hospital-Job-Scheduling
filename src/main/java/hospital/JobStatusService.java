
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JobStatusService {

    public boolean updateStatus(int jobId, String status)
            throws SQLException {

        if (jobId <= 0) {
            throw new IllegalArgumentException("Invalid job ID.");
        }

        if (status == null ||
            !(status.equals("WAITING") ||
              status.equals("IN_PROGRESS") ||
              status.equals("COMPLETED") ||
              status.equals("CANCELLED"))) {
            throw new IllegalArgumentException(
                "Invalid job status.");
        }

        String sql = "UPDATE jobs SET status = ? WHERE job_id = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, jobId);

            return ps.executeUpdate() > 0;
        }
    }
}
