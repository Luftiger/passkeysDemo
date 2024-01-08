package raphael.luft.passkeys.server.helpers;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;


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
        this.url = "jdbc:sqlite:"+ dbName;
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
        String sql = """
                CREATE TABLE credentials (
                     id TEXT NOT NULL,\s
                     displayName TEXT NOT NULL,\s
                     publicKey BYTES NOT NULL,\s
                     algorithm TEXT NOT NULL\s
                );""";
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
     * Fügt eine Benutzeranmeldeinformation zur Datenbank hinzu.
     *
     * @param user Der Benutzer, dessen Informationen hinzugefügt werden sollen.
     * @return "s" bei erfolgreicher Hinzufügung, andernfalls die Fehlermeldung.
     */
    public String addCredential(User user) {
        String sql = "INSERT INTO credentials(id,displayName,publicKey,algorithm) VALUES (?,?,?,?)";
        try {
            PreparedStatement stmt =  this.conn.prepareStatement(sql);
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getDisplayName());
            stmt.setBytes(3, user.getPublicKey().getEncoded());
            stmt.setString(4, user.getAlgorithm());
            stmt.executeUpdate();
            return "s";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }


    /**
     * Entfernt eine Benutzeranmeldeinformation aus der Datenbank.
     *
     * @param id Die ID des Benutzers, dessen Informationen entfernt werden sollen.
     * @return "s" bei erfolgreicher Entfernung, andernfalls die Fehlermeldung.
     */
    public String removeCredential(String id){
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
     * Ruft die Benutzeranmeldeinformationen für eine bestimmte ID aus der Datenbank ab.
     *
     * @param id Die ID des Benutzers, dessen Informationen abgerufen werden sollen.
     * @return Ein Benutzerobjekt mit den abgerufenen Informationen oder null bei einem Fehler.
     */
    public User getCredential(String id) {
        String sql = "SELECT id,displayName,publicKey,algorithm FROM credentials WHERE id = ?";
        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs  = stmt.executeQuery();

            byte[]encodedPublicKey = rs.getBytes("publicKey");
            KeyFactory kf = KeyFactory.getInstance(rs.getString("algorithm"));

            return new User(
                    rs.getString("id"),
                    rs.getString("displayName"),
                    kf.generatePublic(new X509EncodedKeySpec(encodedPublicKey)),
                    rs.getString("algorithm")
            );

        } catch (InvalidKeySpecException | SQLException | NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    /**
     * Überprüft, ob eine bestimmte ID in der Datenbank existiert.
     *
     * @param id Die zu überprüfende ID.
     * @return true, wenn die ID existiert, false sonst.
     */
    public boolean idExists(String id) {
        String sql = "SELECT COUNT(*) FROM credentials WHERE id = ?";

        try {
            PreparedStatement stmt = this.conn.prepareStatement(sql);
            stmt.setString(1, id);

            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
