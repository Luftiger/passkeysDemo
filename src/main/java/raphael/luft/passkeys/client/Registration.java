package raphael.luft.passkeys.client;

import raphael.luft.passkeys.client.helpers.KeyGenerator;

import java.security.PublicKey;
import java.security.PrivateKey;
public class Registration {
    private static final String algorithm = "DSA";
    private static final int keySize = 2048;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public Registration() {

    }
}
