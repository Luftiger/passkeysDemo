package raphael.luft.passkeys.server.helpers;

import javafx.application.Platform;
import javafx.scene.web.WebView;

import java.util.HashMap;


/**
 * Diese Klasse `ServerHandler` erweitert die Klasse `Server` und behandelt die Verarbeitung von
 * Verbindungen, Nachrichten und Anfragen im Zusammenhang mit der Server-Funktionalität.
 */
public class ServerHandler extends Server {
    private final HashMap<String, ConnectedClient> connectedClients;

    private final StringBuilder mainOutputSb;
    private final WebView userView;
    private final WebView mainOutput;

    private final Database database;

    private ConnectedClient currentClient;


    /**
     * Konstruktor für `ServerHandler`.
     *
     * @param userView   Die `WebView` für die Benutzeransicht.
     * @param mainOutputSb         Der `StringBuilder` für die Ausgabemeldungen.
     * @param mainOutput Die `WebView` für die Hauptausgabe.
     * @param database   Die `Database` für den Zugriff auf Benutzerinformationen.
     */
    public ServerHandler(WebView userView, StringBuilder mainOutputSb, WebView mainOutput, Database database) {
        super(5000);
        this.connectedClients = new HashMap<>();
        this.userView = userView;
        this.mainOutputSb = mainOutputSb;
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
        this.currentClient = new ConnectedClient(ip, port);
        this.connectedClients.put((ip + port), this.currentClient);
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
        } else if (msg.startsWith("[E]")) {
            addOutput("<p class='warning'>Clientseitiger Fehler bei der Antwort durch " + ip + ":" + port +
                    ": '"+ msg + "' Vorgang abgebrochen</p>");
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

        if (this.database.idExists(options.getClientId())) {
            send(ip, port, options.toString());
            addOutput("<p>Challenge an " + ip + ":" + port + " gesendet</p>");

            this.currentClient = this.connectedClients.get(ip + port);
            this.currentClient.setId(options.getClientId());
            this.currentClient.setOptions(options);
            this.connectedClients.replace((ip + port), this.currentClient);
        } else {
            send(ip, port, "[E]");
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
        this.currentClient = this.connectedClients.get(ip + port);

        // 0:[AR]|1:id|2:displayName|3:challenge
        String[] r = msg.split("\\|");

        User currentUser = this.database.getCredential(r[1]);
        currentUser.setRecentSignedChallenge(r[3]);
        currentUser.setRecentChallenge(this.currentClient.getOptions().getChallenge());

        if (currentUser.challengeIsValid()) {
            addOutput("<p class='important'>" + currentUser.getDisplayName() + "|" + ip + ":" + port + " erfolgreich verifiziert</p>");
            this.currentClient.setVerified(true);
            this.connectedClients.replace((ip + port), this.currentClient);
            this.currentClient.setUserData(currentUser);
            this.updateUserView();
            send(ip, port, "[V]");
        } else {
            send(ip, port, "[E]");
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

        this.currentClient = this.connectedClients.get(ip + port);
        this.currentClient.setId(options.getClientId());
        this.currentClient.setOptions(options);
        this.connectedClients.replace((ip + port), this.currentClient);
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
        this.currentClient = this.connectedClients.get(ip + port);
        User currentUser = new User(msg);
        currentUser.setRecentChallenge(this.currentClient.getOptions().getChallenge());
        if(currentUser.challengeIsValid()) {
            addOutput("<p class='important'>" + currentUser.getDisplayName() + "|" + ip + ":" + port + " erfolgreich verifiziert</p>");
            this.currentClient.setVerified(true);
            this.connectedClients.replace((ip + port), this.currentClient);
            this.database.addCredential(currentUser);
            this.currentClient.setUserData(currentUser);
            this.updateUserView();
            send(ip, port, "[A]");
        } else {
            addOutput("<p>" + ip + ":" + port + " abgelehnt: signierte Challenge ungültig</p>");
            this.connectedClients.remove((ip + port));
            send(ip, port, "[E]");
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
        this.connectedClients.remove(ip + port);
        this.updateUserView();
    }


    /**
     * Aktualisiert die Benutzeroberfläche mit den verbundenen Clients.
     */
    public void updateUserView() {
        Platform.runLater(() -> {
            StringBuilder userSb = new StringBuilder();
            this.connectedClients.forEach((key, connectedClient) -> userSb.append(connectedClient.getInfoAsString()));
            this.userView.getEngine().loadContent(userSb.toString());
        });
    }


    /**
     * Fügt eine Ausgabemeldung hinzu und aktualisiert die Hauptausgabe in der Benutzeroberfläche.
     *
     * @param s Die hinzuzufügende Ausgabemeldung.
     */
    public void addOutput(String s) {
        Platform.runLater(() -> {
            mainOutputSb.append(s);
            mainOutput.getEngine().loadContent(mainOutputSb.toString());
        });

    }
}
