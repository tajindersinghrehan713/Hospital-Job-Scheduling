
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceRepository {

    public List<Resource> getAllResources() throws SQLException {
        List<Resource> resources = new ArrayList<>();

        String sql = "SELECT resource_id, department_id, " +
                     "resource_name, resource_type, availability_status " +
                     "FROM resources ORDER BY resource_id";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Resource resource = new Resource(
                    rs.getInt("resource_id"),
                    rs.getInt("department_id"),
                    rs.getString("resource_name"),
                    rs.getString("resource_type"),
                    rs.getString("availability_status")
                );

                resources.add(resource);
            }
        }

        return resources;
    }

    public List<Resource> getAvailableResources() throws SQLException {
        List<Resource> resources = new ArrayList<>();

        String sql = "SELECT resource_id, department_id, " +
                     "resource_name, resource_type, availability_status " +
                     "FROM resources WHERE availability_status = ? " +
                     "ORDER BY resource_id";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "AVAILABLE");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resources.add(new Resource(
                        rs.getInt("resource_id"),
                        rs.getInt("department_id"),
                        rs.getString("resource_name"),
                        rs.getString("resource_type"),
                        rs.getString("availability_status")
                    ));
                }
            }
        }

        return resources;
    }
}
