package fr.student.blackjack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client extends Application {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private static String pseudo;

    public static void main(String[] args) {
        final String SERVER_IP = "localhost";
        final int SERVER_PORT = 12345;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            logger.info("Connecté au serveur. Vous pouvez commencer à envoyer des messages.");

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            logger.info("Saisissez votre pseudo : ");
            pseudo = consoleReader.readLine();

            writer.println(pseudo);

            int responseCode = Integer.parseInt(reader.readLine());

            if(responseCode == 0){
                launch(args);
            }else{
                logger.info("Votre pseudo a été mal saisi ou incorrect, veuillez réessayer.");
            }

            logger.info("Fermeture du programme");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur de communication avec le serveur", e);
        }

    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CardsController.class.getResource("cards.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        stage.setResizable(false);
        stage.setTitle("BlackJack (" + pseudo + ")");
        stage.setScene(scene);
        stage.show();
    }
}
