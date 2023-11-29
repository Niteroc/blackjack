package gui;

import client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import table.TableSR;

import java.io.IOException;
import java.util.*;

/**
 * Contrôleur pour l'interface graphique de l'application.
 */
public class Controller {

    @FXML
    Text playerNameLeft;
    @FXML
    Text playerNameMiddle;
    @FXML
    Text playerNameRight;
    @FXML
    TextArea chatTextArea;
    @FXML
    ImageView card1left;
    @FXML
    ImageView card2left;
    @FXML
    ImageView card3left;
    @FXML
    ImageView card4left;
    @FXML
    ImageView card5left;
    @FXML
    ImageView card6left;
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
    private int bank;
    @FXML
    Text bankrupt;

    @FXML
    TextField currentBet;
    @FXML
    TextField currentBalance;
    @FXML
    TextField textStatus;
    @FXML
    TextField textPlayerCount;
    @FXML
    TextField chatField;

    @FXML
    Button dealButton;
    @FXML
    Button hitButton;
    @FXML
    Button standButton;
    @FXML
    Button doubleDown;

    Timer timer;
    TimerTask task;
    List<Text> textList = new ArrayList<>();

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
    }

    private Client currentClient;

    /**
     * Initialise le contrôleur.
     */
    @FXML
    public void initialize() {
        textList = new ArrayList<>();
        textList.add(playerNameLeft);
        textList.add(playerNameMiddle);
        textList.add(playerNameRight);

        textStatus.setStyle("-fx-control-inner-background: green;");
        dealButton.setDisable(true);

        imageViews = new ImageView[4][6];
        imageViews[0][0] = card1left;
        imageViews[0][1] = card2left;
        imageViews[0][2] = card3left;
        imageViews[0][3] = card4left;
        imageViews[0][4] = card5left;
        imageViews[0][5] = card6left;

        imageViews[1][0] = card1middle;
        imageViews[1][1] = card2middle;
        imageViews[1][2] = card3middle;
        imageViews[1][3] = card4middle;
        imageViews[1][4] = card4middle;
        imageViews[1][5] = card4middle;

        imageViews[2][0] = card1right;
        imageViews[2][1] = card2right;
        imageViews[2][2] = card3right;
        imageViews[2][3] = card4right;
        imageViews[2][4] = card4right;
        imageViews[2][5] = card4right;

        imageViews[3][0] = dealercard1;
        imageViews[3][1] = dealercard2;
        imageViews[3][2] = dealercard3;
        imageViews[3][3] = dealercard4;
        imageViews[3][4] = dealercard4;
        imageViews[3][5] = dealercard4;

        // Mettre à jour les valeurs des Text ici si nécessaire
        clearText();

        chatField.setOnAction(e -> {
            String inputText = chatField.getText();
            try {
                currentClient.sendMessage(inputText);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            chatField.clear();
        });
    }

    /**
     * Met à jour l'interface graphique avec les informations de la table de jeu.
     *
     * @param tbsr L'état de la table de jeu.
     */
    public void testText(TableSR tbsr) {
        if (tbsr.isGameInProgress()) {
            textStatus.setStyle("-fx-control-inner-background: red;");
            textStatus.setText("Partie en cours");
        } else {
            textStatus.setStyle("-fx-control-inner-background: green;");
            textStatus.setText("En attente des mises");
        }

        if (tbsr.isGameFinished()) {
            bankrupt.setText("Gain : " + currentClient.getGain() + "€");
            bankrupt.setVisible(true);
        }

        clearText();
        clearImage();
        textPlayerCount.setText("Joueurs connectés : " + tbsr.getClientList().size() + "/3");

        hitButton.setDisable(!currentClient.isMyTurn());
        standButton.setDisable(!currentClient.isMyTurn());
        if (currentClient.isMyTurn()) doubleDown.setDisable(currentClient.getCurrentHand().getCardSRList().size() != 2);

        if (currentClient.isMyTurn()) {
            setTimer(30);
        }

        dealButton.setDisable(currentClient.hasBet());

        for (int i = 0; i < tbsr.getClientList().size(); i++) {
            textList.get(i).setUnderline(tbsr.getClientList().get(i).isMyTurn());
            textList.get(i).setText(tbsr.getClientList().get(i).getPseudo() + " | " + tbsr.getClientList().get(i).getCurrentBet() + "€");
            bank = currentClient.getBalance();
            currentBalance.setText("Banque : " + currentClient.getBalance() + "€");
            if (tbsr.getClientList().get(i).getCurrentHand() != null) {
                for (int k = 0; k < tbsr.getClientList().get(i).getCurrentHand().getCardSRList().size(); k++) {
                    imageViews[i][k].setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/all/" + tbsr.getClientList().get(i).getCurrentHand().getCardSRList().get(k).getCardName() + ".png"))));
                    imageViews[i][k].setVisible(true);
                }
            }
        }
        if (tbsr.getHandDealer().getCardSRList() != null) {
            for (int i = 0; i < tbsr.getHandDealer().getCardSRList().size(); i++) {
                if (tbsr.getHandDealer().getCardSRList().get(i).getHide()) {
                    imageViews[3][i].setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/back_cards.png"))));
                } else {
                    imageViews[3][i].setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/all/" + tbsr.getHandDealer().getCardSRList().get(i).getCardName() + ".png"))));
                }
                imageViews[3][i].setVisible(true);
            }
        }
    }

    public void cancelTimer() {
        if (timer != null && task != null) {
            timer.cancel();
            task.cancel();
        }
    }

    public void setTimer(int secondes) {
        timer = new Timer();
        final int[] tempsRestant = {secondes};

        // Tâche pour mettre à jour l'affichage du temps restant
        task = new TimerTask() {
            @Override
            public void run() {
                if (tempsRestant[0] > 0) {
                    bankrupt.setText("Temps restant : " + tempsRestant[0]);
                    bankrupt.setVisible(true);
                    tempsRestant[0]--;
                } else {
                    bankrupt.setText("");
                    bankrupt.setVisible(false);
                    cancelTimer();
                    try {
                        currentClient.setWantToStay(true, true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    blockAction();
                }
            }
        };

        // Tâche pour le délai de 30 secondes
        timer.schedule(task, 0, 1000); // Mettre à jour toutes les secondes
    }

    @FXML
    private void hitACard() throws IOException {
        currentClient.setWantACard(true, true);
        doubleDown.setDisable(true);
        blockAction();
        cancelTimer();
    }

    @FXML
    private void stand() throws IOException {
        currentClient.setWantToStay(true, true);
        doubleDown.setDisable(true);
        blockAction();
        cancelTimer();
    }

    private void clearImage() {
        for (ImageView[] imageViews1 : imageViews) {
            for (ImageView imageView : imageViews1) {
                imageView.setImage(null);
            }
        }
    }

    private void clearText() {
        for (Text text : textList) {
            text.setText(""); // Réinitialiser les valeurs des Text à une chaîne vide
        }
    }

    public void resetBankrupt() {
        bankrupt.setVisible(false);
        bankrupt.setText("Banque insuffisante");
    }

    @FXML
    public void Bet1() {
        resetBankrupt();

        if ((currentClient.canBet(bet + 1))) {
            bet += 1;
            bank -= 1;
            currentBet.setText("Mise : " + bet + "€");
            currentBalance.setText("Banque : " + bank + "€");
        } else {
            bankrupt.setVisible(true);
        }

    }

    @FXML
    public void Bet5() {
        resetBankrupt();

        if ((currentClient.canBet(bet + 5))) {
            bet += 5;
            bank -= 5;
            currentBet.setText("Mise : " + bet + "€");
            currentBalance.setText("Banque : " + bank + "€");
        } else {
            bankrupt.setVisible(true);
        }

    }

    @FXML
    public void Bet25() {
        resetBankrupt();

        if ((currentClient.canBet(bet + 25))) {
            bet += 25;
            bank -= 25;
            currentBet.setText("Mise : " + bet + "€");
            currentBalance.setText("Banque : " + bank + "€");
        } else {
            bankrupt.setVisible(true);
        }

    }

    @FXML
    public void Bet50() {
        resetBankrupt();

        if ((currentClient.canBet(bet + 50))) {
            bet += 50;
            bank -= 50;
            currentBet.setText("Mise : " + bet + "€");
            currentBalance.setText("Banque : " + bank + "€");
        } else {
            bankrupt.setVisible(true);
        }

    }

    @FXML
    public void Bet100() {
        resetBankrupt();

        if ((currentClient.canBet(bet + 100))) {
            bet += 100;
            bank -= 100;
            currentBet.setText("Mise : " + bet + "€");
            currentBalance.setText("Banque : " + bank + "€");
        } else {
            bankrupt.setVisible(true);
        }

    }

    @FXML
    public void Bet500() {
        resetBankrupt();

        if ((currentClient.canBet(bet + 500))) {
            bet += 500;
            bank -= 500;
            currentBet.setText("Mise : " + bet + "€");
            currentBalance.setText("Banque : " + bank + "€");
        } else {
            bankrupt.setVisible(true);
        }

    }

    @FXML
    public void Reset() {

        bet = 0;
        bank = currentClient.getBalance();
        currentBet.setText("Mise : " + bet + "€");
        currentBalance.setText("Banque : " + bank + "€");
        resetBankrupt();
    }

    @FXML
    public void Bet() {

        try {
            currentClient.setBalance(bank, false);
            currentClient.setHasBet(true, bet, true);
            blockAction();
            dealButton.setDisable(true);
            bet = 0;
            currentBet.setText("Mise : " + bet + "€");
            currentBalance.setText("Banque : " + currentClient.getBalance() + "€");
            resetBankrupt();
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }

    }

    @FXML
    public void DoubleDown() {

        try {
            if(currentClient.canBet(bet * 2)){
                currentClient.setBalance(currentClient.getBalance() - currentClient.getCurrentBet(), false);
                currentClient.setCurrentBet(currentClient.getCurrentBet() * 2);
                currentClient.setWantACard(true, false);
                currentClient.setWantToStay(true, true);
                doubleDown.setDisable(true);
                cancelTimer();
            } else {
                bankrupt.setVisible(true);
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }

    }

    public void blockAction() {
        dealButton.setDisable(true);
        standButton.setDisable(true);
        hitButton.setDisable(true);
        doubleDown.setDisable(true);
    }

    public void setChat(String s) {
        chatTextArea.appendText(s + "\n");
    }
}
