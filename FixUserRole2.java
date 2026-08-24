import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class FixUserRole2 {
    public static void main(String[] args) {
        String url = "jdbc:h2:file:./data/evalorithm";
        String user = "sa";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to H2.");
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE users SET role = 1");
                stmt.executeUpdate("DELETE FROM student_profiles");
            }
            System.out.println("Updated roles.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
