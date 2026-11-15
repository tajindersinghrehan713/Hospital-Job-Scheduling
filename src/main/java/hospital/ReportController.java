
package hospital;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @GetMapping
    public List<Map<String, Object>> getReport() throws SQLException {

        String sql = "SELECT s.algorithm, " +
                "COUNT(*) AS totalJobs, " +
                "ROUND(AVG(s.waiting_time), 2) AS avgWT, " +
                "ROUND(AVG(s.turnaround_time), 2) AS avgTAT, " +
                "ROUND(AVG(s.response_time), 2) AS avgRT " +
                "FROM schedules s " +
                "JOIN (" +
                "SELECT algorithm, MAX(run_id) AS latest_run " +
                "FROM schedules GROUP BY algorithm" +
                ") latest ON s.algorithm = latest.algorithm " +
                "AND s.run_id = latest.latest_run " +
                "GROUP BY s.algorithm ORDER BY avgWT";

        List<Map<String, Object>> report = new ArrayList<>();

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                report.add(Map.of(
                    "algorithm", rs.getString("algorithm"),
                    "totalJobs", rs.getInt("totalJobs"),
                    "avgWT", rs.getDouble("avgWT"),
                    "avgTAT", rs.getDouble("avgTAT"),
                    "avgRT", rs.getDouble("avgRT")
                ));
            }
        }

        return report;
    }
}
