package qrcode;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class QrCode {

    private String pseudo;

    public String getPseudo() {
        return pseudo;
    }

    public QrCode(){
        Webcam webcam = Webcam.getDefault();   //Generate Webcam Object
        webcam.setViewSize(new Dimension(320,240));
        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(false);
        JFrame jFrame = new JFrame();
        jFrame.add(webcamPanel);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        do {
            try {
                BufferedImage image = webcam.getImage();
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result = new MultiFormatReader().decode(bitmap);
                if(result.getText() != null) {
                    pseudo = result.getText();
                    jFrame.setVisible(false);
                    jFrame.dispose();
                    webcam.close();
                    break;
                }

            }catch (NotFoundException e ) {
                //pass
            }

        } while(true);
    }
}