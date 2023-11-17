package gui;

import client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import table.TableSR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Controller {

    private static final Logger logger = Logger.getLogger(JavaFXGUI.class.getName());

    @FXML
    Text playerNameLeft;

    @FXML
    Text playerNameMiddle;

    @FXML
    Text playerNameRight;

    @FXML
    ImageView card1left;
    @FXML
    ImageView card2left;
    @FXML
    ImageView card3left;
    @FXML
    ImageView card4left;
    @FXML
    ImageView card1middle;
    @FXML
    ImageView card2middle;
    @FXML
    ImageView card3middle;
    @FXML
    ImageView card4middle;
    @FXML
    ImageView card1right;
    @FXML
    ImageView card2right;
    @FXML
    ImageView card3right;
    @FXML
    ImageView card4right;
    @FXML
    ImageView dealercard1;
    @FXML
    ImageView dealercard2;
    @FXML
    ImageView dealercard3;
    @FXML
    ImageView dealercard4;

    private ImageView[][] imageViews;

    private int bet = 0;

    @FXML
    TextField currentBet;

    @FXML
    Button dealButton;

    List<Text> textList = new ArrayList<>();

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
    }

    private Client currentClient;

    @FXML
    public void initialize() {
        textList = new ArrayList<>();
        textList.add(playerNameLeft);
        textList.add(playerNameMiddle);
        textList.add(playerNameRight);

        imageViews = new ImageView[4][4];
        imageViews[0][0] = card1left;
        imageViews[0][1] = card2left;
        imageViews[0][2] = card3left;
        imageViews[0][3] = card4left;

        imageViews[1][0] = card1middle;
        imageViews[1][1] = card2middle;
        imageViews[1][2] = card3middle;
        imageViews[1][3] = card4middle;

        imageViews[2][0] = card1right;
        imageViews[2][1] = card2right;
        imageViews[2][2] = card3right;
        imageViews[2][3] = card4right;

        imageViews[3][0] = dealercard1;
        imageViews[3][1] = dealercard2;
        imageViews[3][2] = dealercard3;
        imageViews[3][3] = dealercard4;

        // Mettre à jour les valeurs des Text ici si nécessaire
        clearText();
    }

    public void testText(TableSR tbsr) {
        clearText();
        clearImage();
        for (int i = 0; i < tbsr.getClientList().size(); i++) {
            currentBet.setText("Mise : " + bet + "€");
            textList.get(i).setText(tbsr.getClientList().get(i).getPseudo() + " | " + tbsr.getClientList().get(i).getCurrentBet() + "€");
            if(tbsr.getClientList().get(i).getCurrentHand()!= null){
                for (int k = 0 ; k < tbsr.getClientList().get(i).getCurrentHand().getCardSRList().size() ; k++){
                    imageViews[i][k].setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/all/" + tbsr.getClientList().get(i).getCurrentHand().getCardSRList().get(k).getCardName() + ".png"))));
                    imageViews[i][k].setVisible(true);
                }
            }
        }
    }

    private void clearImage() {
        for (ImageView[] imageViews1 : imageViews) {
            for(ImageView imageView : imageViews1){
                imageView.setImage(null);
            }
        }
    }

    private void clearText() {
        for (Text text : textList) {
            text.setText(""); // Réinitialiser les valeurs des Text à une chaîne vide
        }
    }

    @FXML
    public void Bet1() {

        bet += 1;
        currentBet.setText("Mise : " + bet + "€");

    }

    @FXML
    public void Bet5() {

        bet += 5;
        currentBet.setText("Mise : " + bet + "€");

    }

    @FXML
    public void Bet25() {

        bet += 25;
        currentBet.setText("Mise : " + bet + "€");

    }

    @FXML
    public void Bet50() {

        bet += 50;
        currentBet.setText("Mise : " + bet + "€");

    }

    @FXML
    public void Bet100() {

        bet += 100;
        currentBet.setText("Mise : " + bet + "€");

    }

    @FXML
    public void Bet500() {

        bet += 500;
        currentBet.setText("Mise : " + bet + "€");

    }

    @FXML
    public void Reset() {

        bet = 0;
        currentBet.setText("Mise : " + bet + "€");

    }

    @FXML
    public void Bet() {

        try {
            currentClient.setHasBet(true,bet,true);
            dealButton.setDisable(true);
            bet = 0;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
