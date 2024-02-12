package raphael.luft.passkeys.server.helpers;


/**
 * Die Klasse ConnectedClient repräsentiert einen verbundenen Client.
 * Sie speichert Informationen wie IP-Adresse, Port, Benutzer-ID, Anzeigename, Optionen und den Verifizierungsstatus.
 */
public class ConnectedClient {
    private final String ip;
    private final int port;
    private String id;
    private Options options;
    private User userData;
    private boolean verified;


    /**
     * Konstruktor für einen ConnectedClient.
     *
     * @param ip Die IP-Adresse des Clients.
     * @param port Der Port, über den der Client verbunden ist.
     */
    public ConnectedClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.verified = false;
    }


    /**
     * Gibt eine formatierte Zeichenkette mit den Informationen des Clients zurück.
     *
     * @return Die formatierte Zeichenkette mit Anzeigename, IP-Adresse und Port.
     */
    public String getInfoAsString() {
        try {
            return "<p>" + this.userData.getDisplayName() + " (" + this.ip + ":" + this.port + ")</p>";
        } catch (NullPointerException e) {
            return "<p>Unbekannt (" + this.ip + ":" + this.port + ")</p>";
        }
    }


    /**
     * Gibt die ID des Clients zurück.
     *
     * @return Die ID des Clients.
     */
    public String getId() {
        return this.id;
    }


    /**
     * Setzt die ID des Clients.
     *
     * @param id Die zu setzende ID.
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Gibt die Optionsinstanz des Clients zurück.
     *
     * @return Die Optionsinstanz des Clients.
     */
    public Options getOptions() {
        return this.options;
    }


    /**
     * Setzt die Optionsinstanz für den Client.
     *
     * @param options Die zu setzenden Optionen.
     */
    public void setOptions(Options options) {
        this.options = options;
    }


    /**
     * Gibt die zum Client gehörige Instanz der Klasse User zurück.
     *
     * @return Instanz der Klasse User
     */
    public User getUserData() {return this.userData;}


    /**
     * Setzt die zum Client gehörige Instanz der Klasse User.
     *
     * @param userData Die zu speichernde Instanz.
     */
    public void setUserData(User userData) {
        this.userData = userData;
        this.id = userData.getId();
    }


    /**
     * Überprüft, ob der Client verifiziert ist.
     *
     * @return True, wenn der Client verifiziert ist; andernfalls false.
     */
    public boolean isVerified() {
        return this.verified;
    }


    /**
     * Setzt den Verifizierungsstatus des Clients.
     *
     * @param verified Der zu setzende Verifizierungsstatus.
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

}
