
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository {

    public List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();

        String sql = "SELECT department_id, department_name, location " +
                     "FROM departments ORDER BY department_id";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Department department = new Department(
                    rs.getInt("department_id"),
                    rs.getString("department_name"),
                    rs.getString("location")
                );

                departments.add(department);
            }
        }

        return departments;
    }
}
