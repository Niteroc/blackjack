module blackjack {
    requires java.logging;
    requires java.desktop;
    requires webcam.capture;
    requires com.google.zxing;
    requires com.google.zxing.javase;

    exports server;
    exports client;
    exports table;
    exports qrcode;
}