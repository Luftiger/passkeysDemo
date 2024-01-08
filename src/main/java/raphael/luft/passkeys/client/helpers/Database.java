package raphael.luft.passkeys.client.helpers;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;


/**
 * Die Klasse Database stellt Methoden für die Datenbankverbindung und -operationen bereit.
 */
public class Database {
    private final String url;
    private Connection conn;


    /**
     * Konstruktor für die Database-Klasse.
     *
     * @param dbName Der Name der Datenbank.
     */
    public Database(String dbName) {
        this.url = "jdbc:sqlite:" + dbName;
    }


    /**
     * Stellt eine Verbindung zur Datenbank her.
     *
     * @return "s" bei erfolgreicher Verbindung, andernfalls die Fehlermeldung.
     */
    public String connect() {
        try {
            this.conn = DriverManager.getConnection(url);
            return "s";
        } catch (SQLException e) {
            this.conn = null;
            return e.getMessage();
        }
    }


    /**
     * Erstellt die notwendige Tabelle in der Datenbank.
     *
     * @return "s" bei erfolgreicher Erstellung, andernfalls die Fehlermeldung.
     */
    public String createTable() {
        String sql = "CREATE TABLE credentials ("
                + "id TEXT NOT NULL,"
                + "displayName TEXT NOT NULL,"
                + "privateKey BYTES NOT NULL,"
                + "algorithm TEXT NOT NULL "
                + ");";
        try {
            Statement stmt = this.conn.createStatement();
            stmt.execute(sql);
            return "s";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }


    /**
     * Trennt die Verbindung zur Datenbank.
     *
     * @return "s" bei erfolgreicher Trennung, "NPE" bei NullpointerException, andernfalls die Fehlermeldung.
     */
    public String disconnect() {
        try {
            this.conn.close();
            this.conn = null;
            return "s";
        } catch (SQLException e) {
            this.conn = null;
            return e.getMessage();
        } catch (NullPointerException n) {
            return "NPE";
        }
    }


    /**
     * Fügt eine neue Anmeldeinformation zur Datenbank hinzu.
     *
     * @param response Die Antwort vom Anmeldeprozess.
     * @return "s" bei erfolgreicher Hinzufügung, andernfalls die Fehlermeldung.
     */
    public String addCredential(RegistrationResponse response) {
        String sql = "INSERT INTO credentials(id,displayName,privateKey,algorithm) VALUES (?,?,?,?)";
        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, response.getId());
            stmt.setString(2, response.getDisplayName());
            stmt.setBytes(3, response.getPrivateKey().getEncoded());
            stmt.setString(4, response.getAlgorithm());
            stmt.executeUpdate();
            return "s";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }


    /**
     * Entfernt eine Anmeldeinformation aus der Datenbank.
     *
     * @param id Die eindeutige ID der Anmeldeinformation.
     * @return "s" bei erfolgreicher Entfernung, andernfalls die Fehlermeldung.
     */
    public String removeCredential(String id) {
        String sql = "DELETE FROM credentials WHERE id = ?";
        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.executeUpdate();
            return "s";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }


    /**
     * Ruft Anmeldeinformationen anhand des Anzeigenamens ab.
     *
     * @param displayName Der Anzeigename der Anmeldeinformation.
     * @return Eine Instanz von AuthenticationResponse bei erfolgreicher Abfrage, andernfalls null.
     */
    public AuthenticationResponse getCredentialByName(String displayName) {
        String sql = "SELECT id,displayName,privateKey,algorithm FROM credentials WHERE displayName = ?";
        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, displayName);
            ResultSet rs = stmt.executeQuery();

            byte[] encodedPrivateKey = rs.getBytes("privateKey");
            KeyFactory kf = KeyFactory.getInstance(rs.getString("algorithm"));

            return new AuthenticationResponse(
                    rs.getString("id"),
                    rs.getString("displayName"),
                    kf.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey)),
                    rs.getString("algorithm")
            );

        } catch (InvalidKeySpecException | SQLException | NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    /**
     * Ruft alle Anzeigenamen aus der Datenbank ab.
     *
     * @return Ein Array von Anzeigenamen.
     */
    public String[] getAllDisplayNames() {
        ArrayList<String> names = new ArrayList<>();
        String sql = "SELECT displayName FROM credentials";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String[] arr = new String[names.size()];
        arr = names.toArray(arr);

        return arr;
    }
}
