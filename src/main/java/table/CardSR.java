package table;

import java.io.Serializable;
import java.util.Objects;

/**
 * Cette classe représente une carte de jeu.
 */
public class CardSR implements Serializable {

    private String cardName;

    private boolean hide = false;

    private int value;

    private boolean isAnAce = false;

    /**
     * Constructeur de la classe CardSR.
     *
     * @param color La couleur de la carte (1 pour Pique, 2 pour Cœur, 3 pour Carreau, 4 pour Trèfle)
     * @param value La valeur de la carte (de 1 à 10 pour les nombres, 11 pour Valet, 12 pour Dame, 13 pour Roi)
     */
    public CardSR(int color, int value) { // 1Pique - 2Coeur - 3Carreau - 4Trèfle // between [1-10 - 11Valet - 12Dame - 13Roi]
        if (value == 1) isAnAce = true;
        this.cardName = "" + value + color;
        if (value > 10)value = 10;
        if(isAnAce) value = 11;
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

    /**
     * Renvoie vrai si la carte est un As.
     *
     * @return Vrai si la carte est un As, sinon faux.
     */
    public boolean isAnAce() {
        return isAnAce;
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
        return Objects.equals(cardName, cardSR.cardName) && Objects.equals(hide, cardSR.hide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardName);
    }
}
