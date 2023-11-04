module fr.student.blackjack {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;


    opens fr.student.blackjack to javafx.fxml;
    exports fr.student.blackjack;
}