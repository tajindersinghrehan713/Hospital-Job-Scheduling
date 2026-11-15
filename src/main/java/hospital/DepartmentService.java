
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DepartmentService {

    public boolean addDepartment(String name, String location)
            throws SQLException {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Department name is required.");
        }

        String sql = "INSERT INTO departments " +
                     "(department_name, location) VALUES (?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setString(2, location);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateLocation(int departmentId, String location)
            throws SQLException {

        if (departmentId <= 0) {
            throw new IllegalArgumentException(
                "Invalid department ID.");
        }

        String sql = "UPDATE departments SET location = ? " +
                     "WHERE department_id = ?";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, location);
            ps.setInt(2, departmentId);

            return ps.executeUpdate() > 0;
        }
    }
}
