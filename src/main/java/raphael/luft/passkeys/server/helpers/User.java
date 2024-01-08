package raphael.luft.passkeys.server.helpers;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * Die Klasse repräsentiert einen Benutzer mit einer eindeutigen ID, Anzeigenamen,
 * einem öffentlichen Schlüssel, einem Algorithmus und zugehörigen Herausforderungen
 * und signierten Herausforderungen.
 */
public class User {
    private String strResp;

    private String id;
    private String displayName;
    private String publicKeyAsString;
    private PublicKey publicKey;
    private String algorithm;
    private String recentChallenge;
    private String recentSignedChallenge;


    /**
     * Konstruktor für die Erstellung eines Benutzers mit spezifischen Attributen.
     *
     * @param id          Die eindeutige ID des Benutzers.
     * @param displayName Der Anzeigename des Benutzers.
     * @param publicKey   Der öffentliche Schlüssel des Benutzers.
     * @param algorithm   Der Verschlüsselungsalgorithmus, der für den Schlüssel verwendet wird.
     */
    public User(
            String id,
            String displayName,
            PublicKey publicKey,
            String algorithm) {
        this.id = id;
        this.displayName = displayName;
        this.publicKey = publicKey;
        this.algorithm = algorithm;
    }


    /**
     * Konstruktor für die Erstellung eines Benutzers aus einem Zeichenkettenformat.
     *
     * @param strResp Die Zeichenkette, die die Benutzerinformationen enthält.
     */
    public User(String strResp) {
        this.strResp = strResp;
        this.splitStrResp();
        this.convertPublicKey();
    }


    /**
     * Private Methode zum Aufteilen des StrResp-Strings und Zuweisen der Werte zu den entsprechenden Attributen.
     */
    private void splitStrResp() {
        String[] s = this.strResp.split("\\|");
        this.id = s[1];
        this.displayName = s[2];
        this.algorithm = s[3];
        this.recentSignedChallenge = s[4];
        this.publicKeyAsString = s[5];
    }


    /**
     * Private Methode zum Konvertieren des öffentlichen Schlüssels von einem String zu einem PublicKey-Objekt.
     */
    private void convertPublicKey() {
        try {
            System.out.println(this.publicKeyAsString);
            byte[] publicBytes = Base64.getDecoder().decode(this.publicKeyAsString);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm);
            this.publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {}

    }


    /**
     * Überprüft die Gültigkeit einer Herausforderung durch Signaturverifikation.
     *
     * @return true, wenn die Herausforderung gültig ist, andernfalls false.
     */
    public boolean challengeIsValid() {
        try {
            Signature signature = Signature.getInstance("NONEwith" + this.algorithm);
            signature.initVerify(this.publicKey);
            signature.update(this.recentChallenge.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(this.recentSignedChallenge);
            return signature.verify(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    /**
     * Setzt die zuletzt signierte Herausforderung für den Benutzer.
     *
     * @param recentSignedChallenge Die zuletzt signierte Herausforderung.
     */
    public void setRecentSignedChallenge(String recentSignedChallenge) {
        this.recentSignedChallenge = recentSignedChallenge;
    }


    /**
     * Setzt die zuletzt erhaltene Herausforderung für den Benutzer.
     *
     * @param recentChallenge Die zuletzt erhaltene Herausforderung.
     */
    public void setRecentChallenge(String recentChallenge) {
        this.recentChallenge = recentChallenge;
    }


    /**
     * Gibt die eindeutige ID des Benutzers zurück.
     *
     * @return Die eindeutige ID des Benutzers.
     */
    public String getId() {
        return this.id;
    }


    /**
     * Gibt den Anzeigenamen des Benutzers zurück.
     *
     * @return Der Anzeigename des Benutzers.
     */
    public String getDisplayName() {
        return this.displayName;
    }


    /**
     * Gibt den öffentlichen Schlüssel des Benutzers zurück.
     *
     * @return Der öffentliche Schlüssel des Benutzers.
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }


    /**
     * Gibt den Verschlüsselungsalgorithmus des Benutzers zurück.
     *
     * @return Der Verschlüsselungsalgorithmus des Benutzers.
     */
    public String getAlgorithm() {
        return this.algorithm;
    }
}

