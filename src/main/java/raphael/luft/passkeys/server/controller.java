package raphael.luft.passkeys.server;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;

public class controller {
    private StringBuilder sb;

    @FXML
    private WebView mainOutput;
    @FXML
    private WebView statistics;
    @FXML
    private WebView user;

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
    protected void startHandler() {
        addOutput("<b>Starten...</b>");
    }

    private void addOutput(String s) {
        sb.append(s).append("<br/>");
        mainOutput.getEngine().loadContent(sb.toString());
    }
}