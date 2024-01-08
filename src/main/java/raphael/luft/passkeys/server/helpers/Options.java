package raphael.luft.passkeys.server.helpers;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;


/**
 * Die Klasse Options stellt die nötigen Informationen zur Registrierung und Anmeldung den jeweiligen Clients bereit.
 */
public class Options {
    private String clientId;
    private String challenge;

    private Database database;

    /**
     * Konstruktor für Options, der für die Registrierung verwendet wird.
     *
     * @param database Die Datenbank, in der die Informationen gespeichert werden sollen.
     */
    public Options(Database database) {
        this.database = database;
        this.generateId();
        this.generateChallenge();
    }

    /**
     * Konstruktor für Options, der für die Anmeldung verwendet wird.
     *
     * @param request Die Anmeldungsanforderung, die Informationen über den Client enthält.
     */
    public Options(String request) {
        String[] r = request.split("\\|");
        this.clientId = r[1];
        this.generateChallenge();
    }


    /**
     * Gibt eine Zeichenfolgendarstellung der Options-Instanz zurück.
     *
     * @return Eine Zeichenfolge im Format "[O]|clientId|challenge|Algorithmus",
     * wobei clientId und challenge spezifische Informationen sind.
     */
    public String toString() {
        String alg = "RSA";
        return "[O]|" + this.clientId + "|" + this.challenge + "|" + alg;
    }


    /**
     * Generiert eine zufällige Client-ID und weist sie der Instanz zu.
     * Stellt sicher, dass die generierte ID eindeutig ist, indem sie rekursiv generiert wird, falls bereits vorhanden.
     */
    public void generateId() {
        String randomUUID = UUID.randomUUID().toString();

        if (!(this.database.idExists(randomUUID))) {
            this.clientId = randomUUID;
        } else generateId();

    }


    /**
     * Generiert eine zufällige Challenge für die Options-Instanz und weist sie der Instanz zu.
     */
    public void generateChallenge() {
        SecureRandom random = new SecureRandom();
        this.challenge = new BigInteger(130, random).toString(32);
    }


    /**
     * Gibt die Client-ID der Options-Instanz zurück.
     *
     * @return Die Client-ID als Zeichenfolge.
     */
    public String getClientId() {
        return this.clientId;
    }


    /**
     * Gibt die Challenge der Options-Instanz zurück.
     *
     * @return Die Challenge als Zeichenfolge.
     */
    public String getChallenge() {
        return this.challenge;
    }
}
