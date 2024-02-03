package raphael.luft.passkeys.server.helpers;

import javafx.application.Platform;
import javafx.scene.web.WebView;

import java.util.HashMap;


/**
 * Diese Klasse `serverHandler` erweitert die Klasse `Server` und behandelt die Verarbeitung von
 * Verbindungen, Nachrichten und Anfragen im Zusammenhang mit der Server-Funktionalität.
 */
public class serverHandler extends Server {
    private final HashMap<String, ConnectedClient> connectedClients;

    private final StringBuilder sb;
    private final WebView userView;
    private final WebView mainOutput;

    private final Database database;


    /**
     * Konstruktor für `serverHandler`.
     *
     * @param userView   Die `WebView` für die Benutzeransicht.
     * @param sb         Der `StringBuilder` für die Ausgabemeldungen.
     * @param mainOutput Die `WebView` für die Hauptausgabe.
     * @param database   Die `Database` für den Zugriff auf Benutzerinformationen.
     */
    public serverHandler(WebView userView, StringBuilder sb, WebView mainOutput, Database database) {
        super(5000);
        this.connectedClients = new HashMap<>();
        this.userView = userView;
        this.sb = sb;
        this.mainOutput = mainOutput;

        this.database = database;
    }


    /**
     * Verarbeitet eine neue Client-Verbindung und aktualisiert die Benutzeroberfläche.
     *
     * @param ip   Die IP-Adresse des Clients.
     * @param port Der Port des Clients.
     */
    public void processNewConnection(String ip, int port) {
        addOutput("<p>Neue Verbindung zu Client: " + ip + ":" + port + "</p>");
        ConnectedClient cC = new ConnectedClient(ip, port);
        connectedClients.put((ip + port), cC);
        updateUserView();
    }


    /**
     * Verarbeitet eingehende Nachrichten basierend auf ihrem Inhalt.
     *
     * @param ip  Die IP-Adresse des sendenden Clients.
     * @param port Der Port des sendenden Clients.
     * @param msg Die empfangene Nachricht.
     */
    public void processMessage(String ip, int port, String msg) {
        if (msg.equals("[RA]")) {
            this.handleRegistrationAttempt(ip, port);
        } else if (msg.startsWith("[RR]")) {
            this.handleRegistrationRequest(ip, port, msg);
        } else if (msg.startsWith("[AA]")) {
            this.handleAuthenticationAttempt(ip, port, msg);
        } else if (msg.startsWith("[AR]")) {
            this.handleAuthenticationResponse(ip, port, msg);
        }
    }

    /**
     * Verarbeitet einen Authentifizierungsversuch.
     *
     * @param ip   Die IP-Adresse des Clients.
     * @param port Der Port des Clients.
     * @param msg  Die empfangene Authentifizierungsnachricht.
     */
    private void handleAuthenticationAttempt(String ip, int port, String msg) {
        addOutput("<p>Anmeldeversuch durch " + ip + ":" + port + "</p>");
        Options options = new Options(msg);

        if (database.idExists(options.getClientId())) {
            send(ip, port, options.toString());
            addOutput("<p>Challenge an " + ip + ":" + port + " gesendet</p>");

            ConnectedClient cC = connectedClients.get(ip + port);
            cC.setId(options.getClientId());
            cC.setOptions(options);
            connectedClients.replace((ip + port), cC);
        } else {
            send(ip, port, "failed");
            addOutput("<p class='warning'>Anmeldeversuch durch " + ip + ":" + port + " fehlgeschlagen: ungültige Id erhalten</p>");
        }
    }


    /**
     * Verarbeitet die Antwort auf eine Authentifizierungsanfrage.
     *
     * @param ip  Die IP-Adresse des Clients.
     * @param port Der Port des Clients.
     * @param msg Die empfangene Authentifizierungsantwort.
     */
    private void handleAuthenticationResponse(String ip, int port, String msg) {
        addOutput("<p>Antwort von " + ip + ":" + port + " erhalten</p>");
        ConnectedClient cC = connectedClients.get(ip + port);

        // 0:[AR]|1:id|2:displayName|3:challenge
        String[] r = msg.split("\\|");

        User currentUser = database.getCredential(r[1]);
        currentUser.setRecentSignedChallenge(r[3]);
        currentUser.setRecentChallenge(cC.getOptions().getChallenge());

        if (currentUser.challengeIsValid()) {
            addOutput("<p class='important'>" + ip + ":" + port + " erfolgreich verifiziert</p>");
            cC.setVerified(true);
            cC.setDisplayName(currentUser.getDisplayName());
            connectedClients.replace((ip + port), cC);
            this.updateUserView();
            send(ip, port, "verified");
        } else {
            send(ip, port, "failed");
            addOutput("<p class='warning'>Anmeldeversuch durch " + ip + ":" + port + " fehlgeschlagen: ungültige Challenge erhalten</p>");
        }
    }


    /**
     * Verarbeitet einen Registrierungsversuch.
     *
     * @param ip   Die IP-Adresse des Clients.
     * @param port Der Port des Clients.
     */
    private void handleRegistrationAttempt(String ip, int port) {
        addOutput("<p>Registrierungsanfrage von " + ip + ":" + port + "</p>");
        Options options = new Options(this.database);
        send(ip, port, options.toString());
        addOutput("<p>ID und Challenge an " + ip + ":" + port + " gesendet</p>");

        ConnectedClient cC = connectedClients.get(ip + port);
        cC.setId(options.getClientId());
        cC.setOptions(options);
        connectedClients.replace((ip + port), cC);
    }


    /**
     * Verarbeitet eine Registrierungsanfrage.
     *
     * @param ip   Die IP-Adresse des Clients.
     * @param port Der Port des Clients.
     * @param msg  Die empfangene Registrierungsnachricht.
     */
    private void handleRegistrationRequest(String ip, int port, String msg) {
        addOutput("<p>Antwort von " + ip + ":" + port + " erhalten</p>");
        ConnectedClient cC = connectedClients.get(ip + port);
        User currentUser = new User(msg);
        currentUser.setRecentChallenge(cC.getOptions().getChallenge());
        if(currentUser.challengeIsValid()) {
            addOutput("<p class='important'>" + ip + ":" + port + " erfolgreich verifiziert</p>");
            cC.setVerified(true);
            cC.setDisplayName(currentUser.getDisplayName());
            connectedClients.replace((ip + port), cC);
            this.updateUserView();
            this.database.addCredential(currentUser);
            send(ip, port, "accepted");
        } else {
            addOutput("<p>" + ip + ":" + port + " abgelehnt: signierte Challenge ungültig</p>");
            connectedClients.remove((ip + port));
            send(ip, port, "failed");
        }
    }


    /**
     * Verarbeitet das Schließen einer Client-Verbindung.
     *
     * @param ip   Die IP-Adresse des Clients.
     * @param port Der Port des Clients.
     */
    public void processClosingConnection(String ip, int port) {
        addOutput("<p>Verbindung zu Client " + ip + ":" + port + " beendet</p>");
        connectedClients.remove(ip + port);
        this.updateUserView();
    }


    /**
     * Aktualisiert die Benutzeroberfläche mit den verbundenen Clients.
     */
    public void updateUserView() {
        Platform.runLater(() -> {
            StringBuilder users = new StringBuilder();
            connectedClients.forEach((key, connectedClient) -> users.append(connectedClient.getInfoAsString()));
            userView.getEngine().loadContent(users.toString());
        });
    }


    /**
     * Fügt eine Ausgabemeldung hinzu und aktualisiert die Hauptausgabe in der Benutzeroberfläche.
     *
     * @param s Die hinzuzufügende Ausgabemeldung.
     */
    public void addOutput(String s) {
        Platform.runLater(() -> {
            sb.append(s);
            mainOutput.getEngine().loadContent(sb.toString());
        });

    }
}
