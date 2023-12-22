module raphael.luft.passkeys {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens raphael.luft.passkeys to javafx.fxml;
    exports raphael.luft.passkeys;
}