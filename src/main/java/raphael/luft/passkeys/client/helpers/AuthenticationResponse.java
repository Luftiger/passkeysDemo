package raphael.luft.passkeys.client.helpers;

import java.security.PrivateKey;


/**
 * Von der abstrakten Klasse Credential abgeleitete Klasse,
 * die eine Antwort auf eine Authentifizierungsherausforderung des Servers repräsentiert.
 */
public class AuthenticationResponse extends Credential {

    /**
     * Konstruktor für die AuthenticationResponse-Klasse.
     *
     * @param id          ID
     * @param displayName Anzeigename
     * @param privateKey  Privater Schlüssel
     * @param algorithm   Algorithmus
     */
    public AuthenticationResponse(String id, String displayName, PrivateKey privateKey, String algorithm) {
        this.id = id;
        this.displayName = displayName;
        this.privateKey = privateKey;
        this.algorithm = algorithm;
    }


    /**
     * Generiert die Antwort für den Server und gibt diese als String zurück.
     *
     * @return Generierte Antwort auf die Herausforderung.
     */
    public String generateResponse() {
        String resp = this.signChallenge();
        if (resp.startsWith("Beim")) {
            return "[E]" + resp;
        } else this.challenge = resp;

        return this.convertToString("AR");
    }


    /**
     * Setzt die Optionen für die Authentifizierungsantwort.
     *
     * @param options Optionen für die Authentifizierungsantwort als String.
     */
    public void setOptions(String options) {
        // 0:[O]|1:Id|2:challenge|3:alg
        String[] o = options.split("\\|");
        this.unsignedChallenge = o[2];
    }

}
