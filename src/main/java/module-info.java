module raphael.luft.passkeys {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.sql;

    exports raphael.luft.passkeys.client;
    opens raphael.luft.passkeys.client to javafx.fxml;

    exports raphael.luft.passkeys.server;
    opens raphael.luft.passkeys.server to javafx.fxml;

    exports raphael.luft.passkeys.client.helpers;

}