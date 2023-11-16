package table;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Objects;

public class CardSR implements Serializable {

    private byte[] imageData;

    private String cardName;

    private int color; // 1Pique - 2Coeur - 3Carreau - 4Trèfle

    private int value; // between [1-10 - 11Valet - 12Dame - 13Roi]

    public CardSR(int color, int value) {
        this.color = color;
        this.value = value;
        this.cardName = "" + color + value;

        try {
            InputStream inputStream = getClass().getResourceAsStream("/fr/student/blackjack/cards/all/" + color + value + ".png");

            if (inputStream != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];

                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                imageData = buffer.toByteArray();
                inputStream.close();
            }
        } catch (IOException e) {
            System.out.println("Impossible de charger l'image \n" + e);
        }
    }

    public byte[] getImageData() {
        return imageData;
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
