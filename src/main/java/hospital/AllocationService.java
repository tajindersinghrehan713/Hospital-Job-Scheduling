
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllocationService {

    public String allocate(int scheduleId, int jobId)
            throws SQLException {

        if (scheduleId <= 0 || jobId <= 0) {
            throw new IllegalArgumentException(
                "Invalid schedule ID or job ID.");
        }

        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);

            try {
                String jobStatus = null;

                String jobSql =
                    "SELECT job_type, status FROM jobs " +
                    "WHERE job_id = ? FOR UPDATE";

                try (PreparedStatement ps = con.prepareStatement(jobSql)) {
                    ps.setInt(1, jobId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Job not found.");
                        }

                        jobStatus = rs.getString("status");
                    }
                }

                if ("COMPLETED".equals(jobStatus) ||
                    "CANCELLED".equals(jobStatus)) {
                    throw new SQLException(
                        "Completed or cancelled jobs cannot be allocated.");
                }

                String scheduleSql =
                    "SELECT schedule_id FROM schedules " +
                    "WHERE schedule_id = ? AND job_id = ? FOR UPDATE";

                try (PreparedStatement ps =
                         con.prepareStatement(scheduleSql)) {

                    ps.setInt(1, scheduleId);
                    ps.setInt(2, jobId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                "Schedule not found for this job.");
                        }
                    }
                }

                String existingSql =
                    "SELECT schedule_id FROM schedule_staff " +
                    "WHERE schedule_id = ? " +
                    "UNION " +
                    "SELECT schedule_id FROM schedule_resources " +
                    "WHERE schedule_id = ?";

                try (PreparedStatement ps =
                         con.prepareStatement(existingSql)) {

                    ps.setInt(1, scheduleId);
                    ps.setInt(2, scheduleId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException(
                                "This schedule already has an active allocation.");
                        }
                    }
                }

                // Match staff role with the job type.
                String staffSql =
                    "SELECT s.staff_id FROM staff s " +
                    "JOIN jobs j ON s.department_id = j.department_id " +
                    "WHERE j.job_id = ? " +
                    "AND s.availability_status = 'AVAILABLE' " +
                    "AND (" +
                    " (j.job_type = 'Initial Assessment' " +
                    "AND s.role IN ('Doctor', 'Nurse')) " +
                    " OR (j.job_type = 'ECG Test' " +
                    "AND s.role = 'Doctor') " +
                    " OR (j.job_type = 'General Checkup' " +
                    "AND s.role = 'Doctor') " +
                    " OR (j.job_type = 'Follow-up' " +
                    "AND s.role IN ('Doctor', 'Nurse'))" +
                    ") " +
                    "ORDER BY s.staff_id LIMIT 1 FOR UPDATE";

                int staffId = -1;

                try (PreparedStatement ps =
                         con.prepareStatement(staffSql)) {

                    ps.setInt(1, jobId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            staffId = rs.getInt("staff_id");
                        }
                    }
                }

                // Lock every required resource in ascending ID order.
                String resourceSql =
                    "SELECT r.resource_id, r.availability_status " +
                    "FROM job_resources jr " +
                    "JOIN resources r ON jr.resource_id = r.resource_id " +
                    "WHERE jr.job_id = ? " +
                    "ORDER BY r.resource_id FOR UPDATE";

                List<Integer> resourceIds = new ArrayList<>();
                boolean resourcesAvailable = true;

                try (PreparedStatement ps =
                         con.prepareStatement(resourceSql)) {

                    ps.setInt(1, jobId);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            resourceIds.add(
                                rs.getInt("resource_id"));

                            if (!"AVAILABLE".equals(
                                    rs.getString("availability_status"))) {
                                resourcesAvailable = false;
                            }
                        }
                    }
                }

                if (staffId == -1 ||
                    resourceIds.isEmpty() ||
                    !resourcesAvailable) {

                    String waitingSql =
                        "UPDATE jobs SET status = 'WAITING' " +
                        "WHERE job_id = ?";

                    try (PreparedStatement ps =
                             con.prepareStatement(waitingSql)) {

                        ps.setInt(1, jobId);
                        ps.executeUpdate();
                    }

                    con.commit();

                    return "Job remains WAITING: eligible staff or required resources are unavailable.";
                }

                // Assign the selected staff member.
                String assignStaff =
                    "INSERT INTO schedule_staff " +
                    "(schedule_id, staff_id) VALUES (?, ?)";

                try (PreparedStatement ps =
                         con.prepareStatement(assignStaff)) {

                    ps.setInt(1, scheduleId);
                    ps.setInt(2, staffId);
                    ps.executeUpdate();
                }

                String updateStaff =
                    "UPDATE staff SET availability_status = 'BUSY' " +
                    "WHERE staff_id = ? " +
                    "AND availability_status = 'AVAILABLE'";

                try (PreparedStatement ps =
                         con.prepareStatement(updateStaff)) {

                    ps.setInt(1, staffId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                            "Staff availability changed. Please retry.");
                    }
                }

                // Assign all required resources.
                String assignResource =
                    "INSERT INTO schedule_resources " +
                    "(schedule_id, resource_id) VALUES (?, ?)";

                String updateResource =
                    "UPDATE resources SET availability_status = 'IN_USE' " +
                    "WHERE resource_id = ? " +
                    "AND availability_status = 'AVAILABLE'";

                try (PreparedStatement insert =
                         con.prepareStatement(assignResource);
                     PreparedStatement update =
                         con.prepareStatement(updateResource)) {

                    for (int resourceId : resourceIds) {
                        insert.setInt(1, scheduleId);
                        insert.setInt(2, resourceId);
                        insert.addBatch();

                        update.setInt(1, resourceId);
                        update.addBatch();
                    }

                    insert.executeBatch();

                    int[] updateCounts = update.executeBatch();

                    for (int count : updateCounts) {
                        if (count == 0) {
                            throw new SQLException(
                                "Resource availability changed. Please retry.");
                        }
                    }
                }

                // Update the job in the same transaction.
                String updateJob =
                    "UPDATE jobs SET status = 'IN_PROGRESS' " +
                    "WHERE job_id = ?";

                try (PreparedStatement ps =
                         con.prepareStatement(updateJob)) {

                    ps.setInt(1, jobId);
                    ps.executeUpdate();
                }

                con.commit();

                return "Staff and all required resources allocated successfully. Job is now IN_PROGRESS.";

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw handleDatabaseError(e);

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public String release(int scheduleId, int jobId)
            throws SQLException {

        if (scheduleId <= 0 || jobId <= 0) {
            throw new IllegalArgumentException(
                "Invalid schedule ID or job ID.");
        }

        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);

            try {
                String checkSql =
                    "SELECT schedule_id FROM schedules " +
                    "WHERE schedule_id = ? AND job_id = ? FOR UPDATE";

                try (PreparedStatement ps =
                         con.prepareStatement(checkSql)) {

                    ps.setInt(1, scheduleId);
                    ps.setInt(2, jobId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                "Schedule not found for this job.");
                        }
                    }
                }

                List<Integer> staffIds = new ArrayList<>();
                List<Integer> resourceIds = new ArrayList<>();

                String staffSql =
                    "SELECT staff_id FROM schedule_staff " +
                    "WHERE schedule_id = ? ORDER BY staff_id FOR UPDATE";

                try (PreparedStatement ps =
                         con.prepareStatement(staffSql)) {

                    ps.setInt(1, scheduleId);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            staffIds.add(rs.getInt("staff_id"));
                        }
                    }
                }

                String resourceSql =
                    "SELECT resource_id FROM schedule_resources " +
                    "WHERE schedule_id = ? ORDER BY resource_id FOR UPDATE";

                try (PreparedStatement ps =
                         con.prepareStatement(resourceSql)) {

                    ps.setInt(1, scheduleId);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            resourceIds.add(rs.getInt("resource_id"));
                        }
                    }
                }

                if (staffIds.isEmpty() && resourceIds.isEmpty()) {
                    throw new SQLException(
                        "No active allocation found for this job.");
                }

                // Remove this schedule's allocation records.
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM schedule_staff WHERE schedule_id = ?")) {

                    ps.setInt(1, scheduleId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM schedule_resources WHERE schedule_id = ?")) {

                    ps.setInt(1, scheduleId);
                    ps.executeUpdate();
                }

                // Release staff only when no other schedule uses them.
                String updateStaff =
                    "UPDATE staff SET availability_status = 'AVAILABLE' " +
                    "WHERE staff_id = ? " +
                    "AND availability_status = 'BUSY' " +
                    "AND NOT EXISTS (" +
                    "SELECT 1 FROM schedule_staff " +
                    "WHERE staff_id = ?)";

                try (PreparedStatement ps =
                         con.prepareStatement(updateStaff)) {

                    for (int staffId : staffIds) {
                        ps.setInt(1, staffId);
                        ps.setInt(2, staffId);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                // Release resources only if they are still IN_USE.
                // MAINTENANCE resources remain unchanged.
                String updateResource =
                    "UPDATE resources SET availability_status = 'AVAILABLE' " +
                    "WHERE resource_id = ? " +
                    "AND availability_status = 'IN_USE' " +
                    "AND NOT EXISTS (" +
                    "SELECT 1 FROM schedule_resources " +
                    "WHERE resource_id = ?)";

                try (PreparedStatement ps =
                         con.prepareStatement(updateResource)) {

                    for (int resourceId : resourceIds) {
                        ps.setInt(1, resourceId);
                        ps.setInt(2, resourceId);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                // Do not change a cancelled job back to COMPLETED.
                String updateJob =
                    "UPDATE jobs SET status = " +
                    "CASE WHEN status = 'CANCELLED' " +
                    "THEN 'CANCELLED' ELSE 'COMPLETED' END " +
                    "WHERE job_id = ?";

                try (PreparedStatement ps =
                         con.prepareStatement(updateJob)) {

                    ps.setInt(1, jobId);
                    ps.executeUpdate();
                }

                con.commit();

                return "Job released successfully. Staff and available resources have been freed.";

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw handleDatabaseError(e);

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Map<String, Object>> getActiveAllocations()
            throws SQLException {

        List<Map<String, Object>> allocations = new ArrayList<>();

        String sql =
            "SELECT s.schedule_id, s.job_id, s.algorithm, " +
            "st.staff_id, st.name AS staff_name, " +
            "r.resource_id, r.resource_name " +
            "FROM schedules s " +
            "JOIN schedule_staff ss ON s.schedule_id = ss.schedule_id " +
            "JOIN staff st ON ss.staff_id = st.staff_id " +
            "JOIN schedule_resources sr ON s.schedule_id = sr.schedule_id " +
            "JOIN resources r ON sr.resource_id = r.resource_id " +
            "ORDER BY s.schedule_id, r.resource_id";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();

                item.put("scheduleId", rs.getInt("schedule_id"));
                item.put("jobId", rs.getInt("job_id"));
                item.put("algorithm", rs.getString("algorithm"));
                item.put("staffId", rs.getInt("staff_id"));
                item.put("staffName", rs.getString("staff_name"));
                item.put("resourceId", rs.getInt("resource_id"));
                item.put("resourceName", rs.getString("resource_name"));

                allocations.add(item);
            }
        }

        return allocations;
    }

    private SQLException handleDatabaseError(Exception e) {

        if (e instanceof SQLException) {
            SQLException sqlException = (SQLException) e;

            if (sqlException.getErrorCode() == 1213 ||
                sqlException.getErrorCode() == 1205) {

                return new SQLException(
                    "Database concurrency conflict. Please retry the allocation.",
                    sqlException);
            }

            return sqlException;
        }

        return new SQLException(
            "Allocation operation failed.", e);
    }
}
