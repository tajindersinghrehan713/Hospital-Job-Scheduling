
package hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatientService {

    public void registerPatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new IllegalArgumentException(
                "Patient cannot be null.");
        }

        if (patient.getName() == null ||
            patient.getName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Patient name is required.");
        }

        if (patient.getAge() < 0 || patient.getAge() > 130) {
            throw new IllegalArgumentException(
                "Enter a valid patient age.");
        }

        String sql = "INSERT INTO patients " +
                     "(name, age, gender, contact) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, patient.getName().trim());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getGender());
            ps.setString(4, patient.getContact());

            ps.executeUpdate();

            System.out.println("Patient registered successfully.");
        }
    }
}
