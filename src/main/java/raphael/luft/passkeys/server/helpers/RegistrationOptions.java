package raphael.luft.passkeys.server.helpers;

import java.io.Serializable;
import java.util.UUID;

import java.security.SecureRandom;
import java.math.BigInteger;

public class RegistrationOptions implements Serializable {
    private String clientId;
    private String challenge;

    public RegistrationOptions(){
        generateRegistrationOptions();
    }

    public void generateRegistrationOptions(){
        UUID randomUUID = UUID.randomUUID();
        this.clientId = randomUUID.toString();

        SecureRandom random = new SecureRandom();
        this.challenge = new BigInteger(130, random).toString(32);
    }

    public String toString(){
        String alg = "DSA";
        return "[RO]|" + this.clientId + "|" + this.challenge + "|" + alg;
    }

    public String getClientId(){return this.clientId;}
}
