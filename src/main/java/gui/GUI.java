package gui;

import table.TableSR;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private JFrame frame;
    private JPanel mainPane;
    private JTextArea balanceField;
    private JLabel [] jLabels = new JLabel[3];
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

    private int w = 1280;
    private int h = 800;
    public GUI() {
        frame = new JFrame("Blackjack Swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(w, h);

        mainPane = new JPanel();
        mainPane.setBackground(new Color(81, 80, 77));
        mainPane.setLayout(new BorderLayout());

        JPanel topPane = new JPanel();
        topPane.setSize(w - w/3,h/2);
        topPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        balanceField = new JTextArea("Banque: $0");
        balanceField.setEditable(false);
        balanceField.setFont(new Font("Arial", Font.PLAIN, 24));
        topPane.add(balanceField);

        mainPane.add(topPane, BorderLayout.NORTH);

        JPanel centerPane = new JPanel();
        centerPane.setLayout(null);

        playerNameMiddle = new JLabel("");
        playerNameMiddle.setFont(new Font("HP Simplified", Font.PLAIN, 25));
        playerNameMiddle.setBounds(560, 397, 180, 25);
        centerPane.add(playerNameMiddle);

        playerNameLeft = new JLabel("");
        playerNameLeft.setFont(new Font("HP Simplified", Font.PLAIN, 25));
        playerNameLeft.setBounds(209, 274, 180, 25);
        centerPane.add(playerNameLeft);

        playerNameRight = new JLabel("");
        playerNameRight.setFont(new Font("HP Simplified", Font.PLAIN, 25));
        playerNameRight.setBounds(900, 274, 180, 25);
        centerPane.add(playerNameRight);

        jLabels[0] = playerNameLeft;
        jLabels[1] = playerNameMiddle;
        jLabels[2] = playerNameRight;

        dealButton = new JButton("Miser");
        dealButton.setBounds(20, 650, 80, 40);
        dealButton.setBackground(Color.green);
        centerPane.add(dealButton);
        // Create and add other Swing components (player cards, dealer cards, buttons, etc.) as needed.

        bet1 = new JButton("1");
        bet1.setBounds(900, 600, 80, 40);
        bet1.setBackground(Color.GRAY);
        centerPane.add(bet1);

        bet5 = new JButton("5");
        bet5.setBounds(985, 600, 80, 40);
        bet5.setBackground(Color.GRAY);
        centerPane.add(bet5);

        bet25 = new JButton("25");
        bet25.setBounds(1070, 600, 80, 40);
        bet25.setBackground(Color.GRAY);
        centerPane.add(bet25);

        bet50 = new JButton("50");
        bet50.setBounds(900, 650, 80, 40);
        bet50.setBackground(Color.GRAY);
        centerPane.add(bet50);

        bet100 = new JButton("100");
        bet100.setBounds(985, 650, 80, 40);
        bet100.setBackground(Color.cyan);
        centerPane.add(bet100);

        bet500 = new JButton("500");
        bet500.setBounds(1070, 650, 80, 40);
        bet500.setBackground(Color.red);
        centerPane.add(bet500);

        hitButton = new JButton("Demander");
        hitButton.setBounds(585, 650, 100, 40);
        hitButton.setBackground(Color.WHITE);
        centerPane.add(hitButton);

        bet500 = new JButton("Rester");
        bet500.setBounds(500, 650, 80, 40);
        bet500.setBackground(Color.WHITE);
        centerPane.add(bet500);

        playerCardsPane = new JPanel();
        centerPane.add(playerCardsPane);


        mainPane.add(centerPane, BorderLayout.CENTER);

        frame.add(mainPane);
        frame.setVisible(true);
    }

    public void testText(TableSR tbsr){
        for(int i = 0 ; i < tbsr.getClientList().size() ; i++){
            jLabels[i].setText("<html>" + tbsr.getClientList().get(i).getPseudo() + "</html>");
            //jLabels[i].setText("<html>" + tbsr.getClientList().get(i).getPseudo() + "<br/>" + tbsr.getClientList().get(i).getBalance() + "€</html>");
        }
    }
}