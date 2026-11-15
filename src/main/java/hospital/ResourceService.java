
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResourceService {

    public boolean updateAvailability(int resourceId, String status)
            throws SQLException {

        if (resourceId <= 0) {
            throw new IllegalArgumentException(
                "Invalid resource ID.");
        }

        if (status == null ||
            !(status.equals("AVAILABLE") ||
              status.equals("IN_USE") ||
              status.equals("MAINTENANCE"))) {
            throw new IllegalArgumentException(
                "Status must be AVAILABLE, IN_USE, or MAINTENANCE.");
        }

        String sql = "UPDATE resources SET availability_status = ? " +
                     "WHERE resource_id = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, resourceId);

            return ps.executeUpdate() > 0;
        }
    }
}
