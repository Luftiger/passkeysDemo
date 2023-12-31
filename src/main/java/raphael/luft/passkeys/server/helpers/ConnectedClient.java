package raphael.luft.passkeys.server.helpers;

public class ConnectedClient {
    private final String ip;
    private final int port;
    private String id;
    private String displayName;


    public ConnectedClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getInfoAsString() {
        return this.displayName + " (" + this.ip + ":" + this.port + ")";
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
