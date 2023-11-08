package gui;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private JFrame frame;
    private JPanel mainPane;
    private JTextField balanceField;
    private JLabel playerNameMiddle;
    private JLabel playerNameLeft;
    private JLabel playerNameRight;
    private JLabel dealerTotal;
    private JPanel playerCardsPane;
    private JPanel dealerCardsPane;
    private JPanel chipsPanel;
    private JButton bet1, bet5, bet25;
    private JPanel chips1Panel;
    private JButton bet50, bet100, bet500;
    private JButton backToPlayMenu;
    private JTextField betField;
    private JButton dealButton;
    private JButton hitButton;
    private JButton standButton;
    private JPanel cardGroup;
    private JButton resetBet;

    public GUI() {
        frame = new JFrame("Blackjack Swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 800);

        mainPane = new JPanel();
        mainPane.setBackground(new Color(0, 81, 44));
        mainPane.setLayout(new BorderLayout());

        JPanel topPane = new JPanel();
        topPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        balanceField = new JTextField("Banque: $0");
        balanceField.setEditable(false);
        balanceField.setFont(new Font("Arial", Font.PLAIN, 24));
        topPane.add(balanceField);

        mainPane.add(topPane, BorderLayout.NORTH);

        JPanel centerPane = new JPanel();
        centerPane.setLayout(null);

        playerNameMiddle = new JLabel("Votre main");
        playerNameMiddle.setFont(new Font("HP Simplified", Font.PLAIN, 25));
        playerNameMiddle.setBounds(560, 397, 180, 25);
        centerPane.add(playerNameMiddle);

        playerNameLeft = new JLabel("Votre main");
        playerNameLeft.setFont(new Font("HP Simplified", Font.PLAIN, 25));
        playerNameLeft.setBounds(209, 274, 180, 25);
        centerPane.add(playerNameLeft);

        playerNameRight = new JLabel("Votre main");
        playerNameRight.setFont(new Font("HP Simplified", Font.PLAIN, 25));
        playerNameRight.setBounds(900, 274, 180, 25);
        centerPane.add(playerNameRight);

        // Create and add other Swing components (player cards, dealer cards, buttons, etc.) as needed.

        mainPane.add(centerPane, BorderLayout.CENTER);

        frame.add(mainPane);
        frame.setVisible(true);
    }
}




