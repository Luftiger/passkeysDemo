package raphael.luft.passkeys;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.control.TextField;

public class controller {
    private StringBuilder sb;

    @FXML
    private TextField usernameField;

    @FXML
    private WebView outputArea;

    @FXML
    public void initialize() {
        sb = new StringBuilder();
        sb.append("""
                <style>*{
                font-size: 14px;
                font-family: Arial, Helvetica, sans-serif;
        }</style>""");
    }


    @FXML
    protected void RegistrationHandler() {
        addOutput("<b>Registrierung läuft...</b>");
    }

    private void addOutput(String s) {
        sb.append(s).append("<br/>");
        outputArea.getEngine().loadContent(sb.toString());
    }
}