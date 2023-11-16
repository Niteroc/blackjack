package gui;

import client.Client;
import table.TableSR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
    private JTextArea betField;
    private JButton dealButton;
    private JButton hitButton;
    private JButton standButton;
    private JPanel cardGroup;
    private JButton resetBet;

    private Client currentClient;
    private int w = 1920;
    private int h = 1080;
    private int bet = 0;

    public GUI(Client c) {
        frame = new JFrame("Blackjack Swing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(w, h);
        currentClient = c;

        mainPane = new JPanel();
        mainPane.setBackground(new Color(53, 101, 77));
        mainPane.setLayout(new BorderLayout());

        JPanel topPane = new JPanel();
        topPane.setBackground(new Color(53, 101, 77));
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
        dealButton.setBounds(20, 700, 80, 40);
        dealButton.setBackground(Color.green);
        dealButton.addActionListener(new Bet());
        centerPane.add(dealButton);
        // Create and add other Swing components (player cards, dealer cards, buttons, etc.) as needed.

        bet1 = new JButton("1");
        bet1.setBounds(900, 650, 80, 40);
        bet1.setBackground(Color.GRAY);
        bet1.addActionListener(new Bet1());
        centerPane.add(bet1);

        bet5 = new JButton("5");
        bet5.setBounds(985, 650, 80, 40);
        bet5.setBackground(Color.GRAY);
        bet5.addActionListener(new Bet5());
        centerPane.add(bet5);

        bet25 = new JButton("25");
        bet25.setBounds(1070, 650, 80, 40);
        bet25.setBackground(Color.GRAY);
        bet25.addActionListener(new Bet25());
        centerPane.add(bet25);

        bet50 = new JButton("50");
        bet50.setBounds(900, 700, 80, 40);
        bet50.setBackground(Color.GRAY);
        bet50.addActionListener(new Bet50());
        centerPane.add(bet50);

        bet100 = new JButton("100");
        bet100.setBounds(985, 700, 80, 40);
        bet100.setBackground(Color.cyan);
        bet100.addActionListener(new Bet100());
        centerPane.add(bet100);

        bet500 = new JButton("500");
        bet500.setBounds(1070, 700, 80, 40);
        bet500.setBackground(Color.red);
        bet500.addActionListener(new Bet500());
        centerPane.add(bet500);

        hitButton = new JButton("Demander");
        hitButton.setBounds(585, 700, 100, 40);
        hitButton.setBackground(Color.WHITE);
        centerPane.add(hitButton);

        standButton = new JButton("Rester");
        standButton.setBounds(500, 700, 80, 40);
        standButton.setBackground(Color.WHITE);
        centerPane.add(standButton);

        playerCardsPane = new JPanel();
        centerPane.add(playerCardsPane);

        centerPane.setBackground(new Color(53, 101, 77));
        mainPane.add(centerPane, BorderLayout.CENTER);

        betField = new JTextArea("Mise :");
        betField.setBounds(20, 666, 200, 24);
        betField.setEditable(false);
        betField.setFont(new Font("Arial", Font.PLAIN, 24));
        centerPane.add(betField);

        resetBet = new JButton("Réinitialiser");
        resetBet.setBounds(105, 700, 115, 40);
        resetBet.setBackground(Color.WHITE);
        resetBet.addActionListener(new Reset());
        centerPane.add(resetBet);


        backToPlayMenu = new JButton("End");
        backToPlayMenu.setBounds(1400, 0, 115, 40);
        backToPlayMenu.setBackground(Color.WHITE);
        topPane.add(backToPlayMenu);

        frame.add(mainPane);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void testText(TableSR tbsr){
        for(int i = 0 ; i < jLabels.length ; i++){
            jLabels[i].setText("");
        }
        for(int i = 0 ; i < tbsr.getClientList().size() ; i++){
            jLabels[i].setText("<html>" + tbsr.getClientList().get(i).getPseudo() + " | " + tbsr.getClientList().get(i).getBalance() + "€" + "</html>");
            //jLabels[i].setText("<html>" + tbsr.getClientList().get(i).getPseudo() + "<br/>" + tbsr.getClientList().get(i).getBalance() + "€</html>");
        }
    }

    public class Bet1 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            bet += 1;
            betField.setText("Mise : $"+(Integer.toString(bet)));
        }
    }

    public class Bet5 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            bet += 5;
            betField.setText("Mise : $"+(Integer.toString(bet)));
        }
    }

    public class Bet25 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            betField.setText("Mise : $"+(Integer.toString(bet)));
        }
    }

    public class Bet50 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            bet += 50;
            betField.setText("Mise : $"+(Integer.toString(bet)));
        }
    }

    public class Bet100 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            bet += 100;
            betField.setText("Mise : $"+(Integer.toString(bet)));
        }
    }

    public class Bet500 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            bet += 500;
            betField.setText("Mise : $"+(Integer.toString(bet)));
        }
    }

    public class Reset implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            bet = 0;
            betField.setText("Mise : $"+(Integer.toString(bet)));
        }
    }

    public class Bet implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                currentClient.setBet(bet);
                currentClient.setHasBet(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}