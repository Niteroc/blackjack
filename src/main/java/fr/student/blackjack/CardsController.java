package fr.student.blackjack;

import javafx.fxml.FXML;
import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CardsController {
    @FXML
    private Text playerNameLeft;
    @FXML
    private Text playerNameMiddle;
    @FXML
    private Text playerNameRight;
    private ImageView[] dealercards = new ImageView[4];
    @FXML
    private ImageView dealercard1;
    @FXML
    private ImageView dealercard2;
    @FXML
    private ImageView dealercard3;
    @FXML
    private ImageView dealercard4;


    public CardsController() {
    }

    @FXML
    protected void initialize() throws IOException {
        initLabel();
        initCards();
    }

    @FXML
    protected void initLabel(){
        playerNameLeft.setText("");
        playerNameMiddle.setText("");
        playerNameRight.setText("");
    }

    @FXML
    protected void initCards() throws IOException {
        Image back_cards_img = new Image(Objects.requireNonNull(getClass().getResource("/fr/student/blackjack/cards/back_cards.png")).toExternalForm());
        dealercards[0] = dealercard1;
        dealercards[1] = dealercard2;
        dealercards[2] = dealercard3;
        dealercards[3] = dealercard4;

        for(ImageView img : dealercards){
            img.setImage(back_cards_img);
        }
    }
}