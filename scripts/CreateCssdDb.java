import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CreateCssdDb {
    public static void main(String[] args) throws Exception {
        String[][] creds = {
                {"cssd", "cssd"},
                {"postgres", "postgres"},
                {"postgres", "root"},
                {"postgres", "123456"},
                {"postgres", "admin"}
        };
        for (String[] c : creds) {
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", c[0], c[1])) {
                System.out.println("OK login " + c[0]);
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT 1 FROM pg_database WHERE datname='cssd'")) {
                    if (!rs.next()) {
                        st.execute("CREATE USER cssd WITH PASSWORD 'cssd'");
                    }
                } catch (Exception e) {
                    System.out.println("user note: " + e.getMessage());
                }
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT 1 FROM pg_database WHERE datname='cssd'")) {
                    if (!rs.next()) {
                        st.execute("CREATE DATABASE cssd OWNER cssd");
                        System.out.println("created database cssd");
                    } else {
                        System.out.println("database cssd exists");
                    }
                }
                return;
            } catch (Exception e) {
                System.out.println("fail " + c[0] + ": " + e.getMessage());
            }
        }
        System.exit(1);
    }
}
