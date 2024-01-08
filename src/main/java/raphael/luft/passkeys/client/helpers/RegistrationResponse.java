package raphael.luft.passkeys.client.helpers;


/**
 * Von der abstrakten Klasse Credential abgeleitete Klasse,
 * die eine Antwort auf eine Registrierungsherausforderung repräsentiert.
 */
public class RegistrationResponse extends Credential{

    /**
     * Konstruktor für die RegistrationResponse-Klasse.
     *
     * @param optionsAsString Vom Server übermittelte Optionen als String.
     * @param displayName     Anzeigename
     */
    public RegistrationResponse(String optionsAsString, String displayName) {
        this.splitStringOptions(optionsAsString);
        this.displayName = displayName;
    }


    /**
     * Generiert die Antwort für den Server und gibt diese als String zurück.
     *
     * @return Generierte Antwort auf die Herausforderung.
     */
    public String generateResponse() {
        KeyGenerator keyGen = new KeyGenerator(this.algorithm);
        this.publicKey = keyGen.getPublicKey();
        this.algorithm = this.publicKey.getAlgorithm();

        this.privateKey = keyGen.getPrivateKey();
        String resp = this.signChallenge();
        if (resp.startsWith("Beim")) {
            return "[E]" + resp;
        } else this.challenge = resp;

        return this.convertToString("RR");
    }


    /**
     * Teilt die vom Server gegebenen Optionen aus einem String auf und setzt die entsprechenden Werte.
     *
     * @param optionsAsString Optionen als String.
     */
    private void splitStringOptions(String optionsAsString) {
        String[] s = optionsAsString.split("\\|");
        this.id = s[1];
        this.unsignedChallenge = s[2];
        this.algorithm = s[3];
    }


}
