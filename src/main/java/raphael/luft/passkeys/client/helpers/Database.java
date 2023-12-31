package raphael.luft.passkeys.client.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection conn;

    public String connect() {
        conn = null;
        try {
            String url = "jdbc:sqlite:data.db";
            conn = DriverManager.getConnection(url);
            return "s";
        } catch (SQLException e) {
            conn = null;
            return e.getMessage();
        }
    }

    public void disconnect() {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {}
        }
    }
}
