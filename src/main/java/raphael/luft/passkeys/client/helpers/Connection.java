package raphael.luft.passkeys.client.helpers;

import java.io.*;
import java.net.*;


public class Connection {
    private Socket socket;
    private BufferedReader fromServer;
    private PrintWriter toServer;

    private final String ip;
    private final int port;

    public Connection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean connect(){
        try {
            this.socket = new Socket(ip, port);
            this.toServer = new PrintWriter(this.socket.getOutputStream(), true);
            this.fromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            return true;
        } catch (IOException e){
            this.socket = null;
            this.toServer = null;
            this.fromServer = null;
            return false;
        }
    }

    public String receive() {
        if (this.fromServer != null) {
            try {
                return this.fromServer.readLine();
            } catch (IOException ignored) {
            }
        }

        return (null);
    }

    public void send(String msg) {
        if (this.toServer != null) {
            this.toServer.println(msg);
        }
    }


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

    public boolean isConnected() {
        return this.socket.isConnected();
    }
}
