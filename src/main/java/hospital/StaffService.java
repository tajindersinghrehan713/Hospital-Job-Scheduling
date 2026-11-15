
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StaffService {

    public boolean updateAvailability(int staffId, String status)
            throws SQLException {

        if (staffId <= 0) {
            throw new IllegalArgumentException(
                "Invalid staff ID.");
        }

        if (status == null ||
            !(status.equals("AVAILABLE") ||
              status.equals("BUSY") ||
              status.equals("UNAVAILABLE"))) {
            throw new IllegalArgumentException(
                "Status must be AVAILABLE, BUSY, or UNAVAILABLE.");
        }

        String sql = "UPDATE staff SET availability_status = ? " +
                     "WHERE staff_id = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, staffId);

            return ps.executeUpdate() > 0;
        }
    }
}
