package raphael.luft.passkeys.client.helpers;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class RegistrationResponse {
    private final String optionsAsString;
    private String id;
    private final String displayName;
    private String unsignedChallenge;

    private PrivateKey privateKey;

    private String challenge;
    private PublicKey publicKey;
    private String algorithm;

    private KeyGenerator keyGen;

    public RegistrationResponse(String optionsAsString, String displayName){
        this.optionsAsString = optionsAsString;
        convertOptions();

        this.displayName = displayName;
    }

    public String generateResponse() {
        try {
            this.keyGen = new KeyGenerator(this.algorithm);
        } catch (NoSuchAlgorithmException e) {
            try {
                this.keyGen = new KeyGenerator("DSA");
            } catch (NoSuchAlgorithmException ignored) {}
        }
        this.publicKey = this.keyGen.getPublicKey();
        this.algorithm = this.publicKey.getAlgorithm();

        this.privateKey = this.keyGen.getPrivateKey();
        signChallenge();

        //TODO: keys mit Userdaten abspeichern

        return convertResponseToString();
    }

    private void convertOptions() {
        String[] s = this.optionsAsString.split("\\|");
        this.id = s[1];
        this.unsignedChallenge = s[2];
        this.algorithm = s[3];
    }

    private void signChallenge() {
        try{
            Signature signature = Signature.getInstance(this.algorithm);
            signature.initSign(this.privateKey);
            byte[] raw = this.unsignedChallenge.getBytes(StandardCharsets.UTF_8);
            signature.update(raw);
            byte[] signed = signature.sign();
            this.challenge = Base64.getEncoder().encodeToString(signed);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ignored) {}
    }

    private String convertResponseToString() {
        return "[RE]|" + this.id + "|" + this.displayName + "|"
                + this.publicKey.toString() + "|" + this.algorithm + "|" + this.challenge;
    }

    private String getId() {
        return this.id;
    }

    private PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    private String getPublicKeyAsString() {
        return this.publicKey.toString();
    }

    private String getChallenge() {
        return this.challenge;
    }
}
