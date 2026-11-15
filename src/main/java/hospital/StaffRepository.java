
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();

        String sql = "SELECT staff_id, department_id, name, role, " +
                     "availability_status FROM staff " +
                     "ORDER BY staff_id";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Staff staff = new Staff(
                    rs.getInt("staff_id"),
                    rs.getInt("department_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("availability_status")
                );

                staffList.add(staff);
            }
        }

        return staffList;
    }

    public List<Staff> getAvailableStaff() throws SQLException {
        List<Staff> staffList = new ArrayList<>();

        String sql = "SELECT staff_id, department_id, name, role, " +
                     "availability_status FROM staff " +
                     "WHERE availability_status = ? " +
                     "ORDER BY staff_id";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "AVAILABLE");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    staffList.add(new Staff(
                        rs.getInt("staff_id"),
                        rs.getInt("department_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("availability_status")
                    ));
                }
            }
        }

        return staffList;
    }
}
