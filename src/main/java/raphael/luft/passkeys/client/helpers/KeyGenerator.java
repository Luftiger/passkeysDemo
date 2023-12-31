package raphael.luft.passkeys.client.helpers;

import java.security.*;


public class KeyGenerator {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public KeyGenerator(String alg) throws NoSuchAlgorithmException {
        // neuen Generator erstellen
        KeyPairGenerator generator = KeyPairGenerator.getInstance(alg);
        generator.initialize(2048);

        // Keys generieren
        KeyPair pair = generator.generateKeyPair();
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
