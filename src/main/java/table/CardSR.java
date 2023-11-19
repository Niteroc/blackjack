package table;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class CardSR implements Serializable {

    private String cardName;

    private boolean hide = false;

    private int value;

    private boolean isAnAce = false;

    public CardSR(int color, int value) { // 1Pique - 2Coeur - 3Carreau - 4Trèfle // between [1-10 - 11Valet - 12Dame - 13Roi]
        if (value == 1) isAnAce = true;
        this.cardName = "" + value + color;
        if (value > 10)value = 10;
        this.value = value;
    }

    public String getCardName() {
        return cardName;
    }

    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isAnAce() {
        return isAnAce;
    }

    public void setAnAce(boolean anAce) {
        isAnAce = anAce;
    }

    @Override
    public String toString() {
        return "CardSR{" + "cardName='" + cardName + '\'' + ", hide=" + hide + '}';
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
