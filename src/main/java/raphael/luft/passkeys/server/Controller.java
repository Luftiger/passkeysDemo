package raphael.luft.passkeys.server;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import raphael.luft.passkeys.server.helpers.*;

import java.util.Objects;

/**
 * Die Controller-Klasse für die PassKeys-Anwendung. Steuert die Benutzeroberfläche und behandelt
 * die Benutzerinteraktionen.
 */
public class Controller {
    private StringBuilder sb;

    @FXML
    private WebView mainOutput;

    @FXML
    private WebView userView;


    /**
     * Initialisiert die Controller-Klasse. Stellt eine Verbindung zur Datenbank her,
     * erstellt gegebenenfalls die erforderlichen Tabellen und startet den Server.
     */
    @FXML
    public void initialize() {
        try {
            this.mainOutput.getEngine().setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("style.css")).toString());
            this.userView.getEngine().setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("style.css")).toString());
        } catch (NullPointerException ignored) {}
        this.sb = new StringBuilder();

        Database database = new Database("data/serverDB.db");
        String s = database.connect();
        if (s.equals("s")) {
            addOutput("</p>Verbindung zur Datenbank hergestellt...</p>");
            s = database.createTable();
            if(s.equals("s")) {
                addOutput("<p>Tabelle wurde angelegt...</p>");
            }
        } else {
            addOutput("<p class='warning'>Fehler bei Verbindung zur Datenbank: " + s + "</p>");
            addOutput("<p class='warning'>Bitte starten Sie das Programm neu.</p>");
        }

        this.addOutput("<p>starte Server...</p>");
        raphael.luft.passkeys.server.helpers.serverHandler serverHandler = new serverHandler(this.userView, this.sb, this.mainOutput, database);

        if (serverHandler.isOpen()) {
            this.addOutput("<p class='important'> Status: aktiv</p>");
        }


    }


    /**
     * Fügt den angegebenen Text zur Ausgabe hinzu und aktualisiert die Anzeige.
     *
     * @param s Der Text, der zur Ausgabe hinzugefügt werden soll.
     */
    private void addOutput(String s) {
        this.sb.append(s);
        this.mainOutput.getEngine().loadContent(this.sb.toString());
        this.mainOutput.getEngine().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }
}