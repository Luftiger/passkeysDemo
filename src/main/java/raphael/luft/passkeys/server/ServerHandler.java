package raphael.luft.passkeys.server;

import raphael.luft.passkeys.server.helpers.ConnectedClient;
import raphael.luft.passkeys.server.helpers.RegistrationOptions;
import raphael.luft.passkeys.server.helpers.Server;

import java.util.HashMap;

public class ServerHandler extends Server {
    HashMap<String, ConnectedClient> connectedClients = new HashMap<>();
    public ServerHandler() {
        super(5000);
    }

    public void processNewConnection(String ip, int port) {
        ConnectedClient cC = new ConnectedClient(ip, port);
        connectedClients.put((ip + port), cC);
    }

    public void processMessage(String ip, int port, String msg) {
        if (msg.equals("[RA]")){
            RegistrationOptions rO = new RegistrationOptions();
            send(ip, port, rO.toString());

            ConnectedClient cC = connectedClients.get(ip+port);
            cC.setId(rO.getClientId());
            connectedClients.replace((ip+port), cC);
        }
    }

    public void processClosingConnection(String ip, int port){
        connectedClients.remove(ip+port);
    }
}
