package table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Cette classe représente une main de cartes dans un jeu.
 */
public class HandSR implements Serializable, Cloneable {

    private final String id = UUID.randomUUID().toString();

    private List<CardSR> cardSRList = new ArrayList<>();

    /**
     * Renvoie la liste des cartes dans la main.
     *
     * @return La liste des cartes dans la main.
     */
    public List<CardSR> getCardSRList() {
        return cardSRList;
    }

    private int value = 0;

    public HandSR() {
    }

    /**
     * Ajoute une carte à la main.
     *
     * @param card La carte à ajouter à la main.
     */
    public void addCardToList(CardSR card) {
        cardSRList.add(card);
    }

    /**
     * Efface la liste des cartes dans la main.
     */
    public void clearCardList() {
        cardSRList.clear();
    }

    /**
     * Calcule et renvoie la valeur totale de la main.
     *
     * @return La valeur totale de la main.
     */
    public int getValue() {
        value = 0;
        for (CardSR cardSR : getCardSRList()) {
            value += cardSR.getValue();
        }
        return value;
    }

    @Override
    public HandSR clone() {
        try {
            HandSR clonedHand = (HandSR) super.clone();
            clonedHand.cardSRList = new ArrayList<>(this.cardSRList); // Copie profonde de la liste
            return clonedHand;
        } catch (CloneNotSupportedException e) {
            // Gestion de l'exception si le clonage n'est pas supporté
            return null;
        }
    }

    @Override
    public String toString() {
        return "HandSR{" + "cardSRList=" + cardSRList + ", value=" + getValue() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandSR handSR = (HandSR) o;
        return Objects.equals(cardSRList, handSR.cardSRList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean containsAce() {
        for (CardSR cardSR : getCardSRList()) {
            if (cardSR.isAnAce()) return true;
        }
        return false;
    }

    public boolean isABlackJack() {
        return cardSRList.size() == 2 && getValue() == 21 && (cardSRList.get(0).isAnAce() || cardSRList.get(1).isAnAce());
    }

    public void tryToUpValue() {
        for (CardSR cardSR : cardSRList) {
            if (cardSR.isAnAce()) {
                if (value + 10 <= 21) value += 10;
            } else {
                return;
            }
        }
    }
}
