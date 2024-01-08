package raphael.luft.passkeys.client.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Die Klasse Connection repräsentiert eine Client-Verbindung zu einem Server über Socket.
 */
public class Connection {
    private final String ip;
    private final int port;
    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;


    /**
     * Konstruktor für die Verbindungsklasse.
     *
     * @param ip   Die IP-Adresse des Servers.
     * @param port Der Port, über den die Verbindung hergestellt wird.
     */
    public Connection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    /**
     * Versucht, eine Verbindung zum Server herzustellen.
     *
     * @return True, wenn die Verbindung erfolgreich hergestellt wurde, ansonsten false.
     */
    public boolean connect() {
        try {
            this.socket = new Socket(ip, port);
            this.toServer = new PrintWriter(this.socket.getOutputStream(), true);
            this.fromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            this.socket = null;
            this.toServer = null;
            this.fromServer = null;
            return false;
        }
    }


    /**
     * Empfängt eine Nachricht vom Server.
     *
     * @return Die empfangene Nachricht oder null, wenn ein Fehler aufgetreten ist.
     */
    public String receive() {
        if (this.fromServer != null) {
            try {
                return this.fromServer.readLine();
            } catch (IOException ignored) {
            }
        }

        return (null);
    }


    /**
     * Sendet eine Nachricht an den Server.
     *
     * @param msg Die zu sendende Nachricht.
     */
    public void send(String msg) {
        if (this.toServer != null) {
            this.toServer.println(msg);
        }
    }


    /**
     * Schließt die Verbindung zum Server.
     *
     * @return True, wenn die Verbindung erfolgreich geschlossen wurde, ansonsten false.
     */
    public boolean close() {
        if ((this.socket != null) && !this.socket.isClosed()) {
            try {
                this.toServer.close();
                this.fromServer.close();
                this.socket.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }


    /**
     * Überprüft, ob die Verbindung zum Server hergestellt ist.
     *
     * @return True, wenn die Verbindung hergestellt ist, ansonsten false.
     */
    public boolean isConnected() {
        return this.socket.isConnected();
    }
}
