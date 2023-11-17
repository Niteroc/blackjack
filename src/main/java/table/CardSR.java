package table;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class CardSR implements Serializable {

    public String getCardName() {
        return cardName;
    }

    private String cardName;

    private int color; // 1Pique - 2Coeur - 3Carreau - 4Trèfle

    private int value; // between [1-10 - 11Valet - 12Dame - 13Roi]

    public CardSR(int color, int value) {
        this.color = color;
        this.value = value;
        this.cardName = "" + value + color;
    }

    @Override
    public String toString() {
        return "CardSR{" +
                "carte=" + cardName +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardSR cardSR = (CardSR) o;
        return Objects.equals(cardName, cardSR.cardName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName);
    }
}
