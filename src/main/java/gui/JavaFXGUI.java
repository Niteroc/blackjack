package gui;

import client.Client;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JavaFXGUI extends Application {

    private Client client;

    private static ThreadPoolExecutor tpe = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());;

    public JavaFXGUI() {
    }

    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cards.fxml"));
        Parent root = loader.load();

        // Récupération du contrôleur si nécessaire
        Controller controller = loader.getController();

        // Initialisation de votre Client avec le socket, etc.
        this.client = new Client(controller);
        tpe.execute(client);

        while(client.getPseudo().isEmpty()){ // on attend que le client est bien chargé
            // on attend
            Thread.sleep(100);
        }

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture du client et du GUI");
            client.shutdown();
        });

        controller.setCurrentClient(client);

        primaryStage.setTitle("BlackJack -- " + client.getPseudo());
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}