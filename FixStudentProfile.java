import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FixStudentProfile {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:file:C:/Projects/college/college/college/evalorithm/data/evalorithm";
        Connection conn = DriverManager.getConnection(url, "sa", "");
        
        // Find if user 65 exists
        PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM student_profiles WHERE user_id = ?");
        checkStmt.setLong(1, 65);
        ResultSet rs = checkStmt.executeQuery();
        if (!rs.next()) {
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO student_profiles (user_id, register_number, department_id, semester_id, current_year, section) " +
                "VALUES (?, ?, NULL, NULL, 1, 'A')"
            );
            insertStmt.setLong(1, 65);
            insertStmt.setString(2, "REG-65");
            insertStmt.executeUpdate();
            System.out.println("Inserted profile for user 65");
        } else {
            System.out.println("Profile already exists");
        }
        conn.close();
    }
}
