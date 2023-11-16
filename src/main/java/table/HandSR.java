package table;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HandSR implements Serializable {

    private final String id = UUID.randomUUID().toString();

    private List<CardSR> cardSRList = new ArrayList<>();

    public List<CardSR> getCardSRList() {
        return cardSRList;
    }

    public HandSR() {}

    public void addCardToList(CardSR card){
        cardSRList.add(card);
    }

    public void clearCardList(){
        cardSRList.clear();
    }

    @Override
    public String toString() {
        return "HandSR{" +
                "cardSRList=" + cardSRList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandSR handSR = (HandSR) o;
        return Objects.equals(id, handSR.id) && Objects.equals(cardSRList, handSR.cardSRList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
