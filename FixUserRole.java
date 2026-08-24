import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class FixUserRole {
    public static void main(String[] args) {
        String url = "jdbc:h2:file:./data/evalorithm";
        String user = "sa";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to H2.");
            
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT id FROM users");
                while (rs.next()) {
                    long id = rs.getLong("id");
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO faculty_profiles (user_id, faculty_id, designation) VALUES (?, ?, ?)")) {
                        ps.setLong(1, id);
                        ps.setString(2, "FAC-" + id);
                        ps.setString(3, "Assistant Professor");
                        ps.executeUpdate();
                    } catch (Exception e) {
                        System.out.println("Faculty profile for user " + id + " might already exist: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Done inserting profiles.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
