package raphael.luft.passkeys.client.helpers;

import java.security.*;


/**
 * Die Klasse KeyGenerator ist für die Generierung von Schlüsselpaaren
 * basierend auf dem angegebenen Algorithmus verantwortlich und ermöglicht
 * den Zugriff auf die generierten öffentlichen und privaten Schlüssel.
 */
public class KeyGenerator {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Konstruiert ein neues KeyGenerator-Objekt unter Verwendung des angegebenen Algorithmus.
     *
     * @param alg Der Algorithmus, der für die Generierung des Schlüsselpaars verwendet werden soll.
     */
    public KeyGenerator(String alg) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(alg);
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException n) {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                KeyPair pair = generator.generateKeyPair();
                this.privateKey = pair.getPrivate();
                this.publicKey = pair.getPublic();
            } catch (NoSuchAlgorithmException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    /**
     * Ruft den von dieser KeyGenerator-Instanz generierten öffentlichen Schlüssel ab.
     *
     * @return Der generierte öffentliche Schlüssel.
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }


    /**
     * Ruft den von dieser KeyGenerator-Instanz generierten privaten Schlüssel ab.
     *
     * @return Der generierte private Schlüssel.
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

}
