module blackjack {
    requires java.logging;
    requires java.desktop;
    requires webcam.capture;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    exports server;
    exports client;
    exports table;
    exports qrcode;
    exports gui;

    opens gui to javafx.fxml;
}