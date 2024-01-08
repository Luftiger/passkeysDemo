package raphael.luft.passkeys.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Die Hauptklasse für die PasskeyDemo-Server-Anwendung.
 */
public class App extends Application {

    /**
     * Die Hauptmethode, die das Starten der Anwendung ermöglicht.
     *
     * @param args Die Befehlszeilenargumente, die an die Anwendung übergeben werden (keine).
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Die Startmethode für die JavaFX-Anwendung. Lädt die FXML-Datei, erstellt eine
     * Szene und zeigt das Hauptfenster an.
     *
     * @param stage Die Hauptbühne (Stage) der Anwendung.
     * @throws IOException wenn ein Fehler beim Laden der FXML-Datei auftritt.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PasskeyDemo Server");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
