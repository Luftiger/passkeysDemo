package raphael.luft.passkeys.client.helpers;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;


public class KeyGenerator {
    private KeyPairGenerator generator;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private KeyPair pair;

    public KeyGenerator(String algorithm, int keySize) throws Exception {
        //TODO: check type

        // neuen Generator erstellen
        this.generator = KeyPairGenerator.getInstance("DSA");
        this.generator.initialize(keySize);

        // Keys generieren
        this.pair = generator.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

}
