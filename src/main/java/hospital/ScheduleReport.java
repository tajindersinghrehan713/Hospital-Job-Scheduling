
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduleReport {

    public void displayReport() throws SQLException {

        String sql = "SELECT algorithm, " +
            "COUNT(*) AS total_jobs, " +
            "ROUND(AVG(waiting_time), 2) AS avg_waiting, " +
            "ROUND(AVG(turnaround_time), 2) AS avg_turnaround, " +
            "ROUND(AVG(response_time), 2) AS avg_response " +
            "FROM schedules " +
            "WHERE run_id = (SELECT MAX(run_id) FROM scheduling_runs) " +
            "GROUP BY algorithm " +
            "ORDER BY avg_waiting";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n==============================================");
            System.out.println("       SCHEDULING PERFORMANCE REPORT");
            System.out.println("==============================================");

            System.out.printf("%-22s %-8s %-12s %-12s %-12s%n",
                "Algorithm", "Jobs", "Avg WT", "Avg TAT", "Avg RT");

            while (rs.next()) {
                System.out.printf("%-22s %-8d %-12.2f %-12.2f %-12.2f%n",
                    rs.getString("algorithm"),
                    rs.getInt("total_jobs"),
                    rs.getDouble("avg_waiting"),
                    rs.getDouble("avg_turnaround"),
                    rs.getDouble("avg_response"));
            }
        }
    }
}
