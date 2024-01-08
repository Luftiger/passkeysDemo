package raphael.luft.passkeys.client.helpers;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Abstrakte Klasse, die grundlegende Informationen und Methoden für Nutzerinformationen bereitstellt.
 */
public abstract class Credential {
    protected String id;
    protected String displayName;

    protected PrivateKey privateKey;
    protected PublicKey publicKey;
    protected String algorithm;

    protected String unsignedChallenge;
    protected String challenge;

    /**
     * Signiert die Challenge-Zeichenkette und gibt das Ergebnis als String zurück.
     *
     * @return signierter String oder Fehlermeldung.
     */
    public String signChallenge() {
        if(unsignedChallenge != null) {
            try {
                Signature signature = Signature.getInstance("NONEwith" + this.algorithm);
                signature.initSign(this.privateKey);
                byte[] raw = this.unsignedChallenge.getBytes(StandardCharsets.UTF_8);
                signature.update(raw);
                byte[] signed = signature.sign();
                return Base64.getEncoder().encodeToString(signed);
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
                return "Beim Signieren der Challenge ist ein Fehler aufgetreten: " + e.getMessage();
            }
        }
        else return "Beim Empfangen der Challenge vom Server ist ein Fehler aufgetreten. Versuchen Sie es erneut.";
    }


    /**
     * Konvertiert die Anmeldeinformationen in einen bestimmten String-Typ.
     *
     * @param type Der Zieltyp des Strings ("RR" für Registration Response, "AR" für Authentication Response).
     * @return String repräsentiert die Anmeldeinformationen.
     */
    public String convertToString(String type) {
        if(type.equals("RR")) {
            return "[RR]|" + this.id + "|" + this.displayName + "|"
                    + this.algorithm + "|" + this.challenge + "|" + Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        } else if (type.equals("AR")) {
            return "[AR]|" + this.id + "|" + this.displayName + "|" + this.challenge;
        }
        return "";
    }


    /**
     * Gibt die ID zurück.
     *
     * @return ID
     */
    public String getId() { return this.id; }


    /**
     * Gibt den Anzeigenamen zurück.
     *
     * @return Anzeigename
     */
    public String getDisplayName() { return this.displayName; }

    /**
     * Gibt den für die Schlüssel genutzten Algorithmus zurück.
     * @return Algorithmus
     */
    public String getAlgorithm() { return this.algorithm; }

    /**
     * Gibt den privaten Schlüssel zurück.
     *
     * @return Privater Schlüssel
     */
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    /**
     * Abstrakt zu implementierende Methode.
     * Generiert die Antwort für den Server
     *
     * @return Generierte Antwort
     */
    public abstract String generateResponse();
}