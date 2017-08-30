import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Game extends JFrame implements ActionListener {

    private Token player1, player2;
    private JButton diceButton;
    private JLabel diceValue;
    private JLabel playerTurn;

    protected Game() {

        setSize(880, 715);
        setTitle("Snakes And Ladders");
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(this);
        menu.add(newGame);

        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.addActionListener(this);
        menu.add(exitMenu);

        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        ImagePanel imagePanel = new ImagePanel();
        imagePanel.setLayout(null);

        diceButton = new JButton("Roll Dice");
        diceButton.setToolTipText("Click or press Spacebar to roll dice");
        diceButton.setFocusPainted(false);
        diceButton.setBackground(Color.PINK);
        diceButton.setOpaque(true);
        diceButton.setBounds(710, 100, 100, 60);
        diceButton.addActionListener(this);
        imagePanel.add(diceButton);

        playerTurn = new JLabel("Player 1 Turn");
        playerTurn.setFont(playerTurn.getFont().deriveFont(18.0f));
        playerTurn.setForeground(new Color(255, 0, 0));
        playerTurn.setBounds(700, 200, 150, 25);
        add(playerTurn);

        JLabel diceValueLabel = new JLabel("Dice Value");
        diceValueLabel.setFont(playerTurn.getFont().deriveFont(18.0f));
        diceValueLabel.setForeground(Color.BLACK);
        diceValueLabel.setBounds(710, 300, 150, 25);
        add(diceValueLabel);

        diceValue = new JLabel("-");
        diceValue.setFont(playerTurn.getFont().deriveFont(35.0f));
        diceValue.setForeground(Color.BLACK);
        diceValue.setBounds(750, 340, 40, 40);
        add(diceValue);

        player1 = new Token();
        player1.turn = 1;
        player2 = new Token();

        add(imagePanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String action = e.getActionCommand();
        switch (action) {
            case "Exit":

                this.dispose();
                break;
            case "New Game":

                repaint();
                player1.xPosition = player2.xPosition = 65;
                player1.yPosition = player2.yPosition = 630;
                player1.turn = 1;
                player1.entry = player2.entry = 0;
                player1.reverseMove = player2.reverseMove = 0;
                player2.turn = 0;
                diceButton.setEnabled(true);
                diceButton.requestFocusInWindow();
                playerTurn.setForeground(new Color(255,0,0));
                playerTurn.setText("Player 1 Turn");
                diceValue.setText("-");
                break;
            default:

                rollDice();
        }
    }

    private void setPlayerPosition(int diceNumber) {

        Graphics graphics = getGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;
        int repeat = 0;

        if (diceNumber == 1 || diceNumber == 6) {
            repeat = 1;
        }

        if (player1.turn == 1) {

            if (player1.entry == 0 && diceNumber == 1) {

                graphics2D.setPaint(Color.red);
                graphics2D.fillOval(player1.xPosition, player1.yPosition, 20, 20);
                player1.entry = 1;
                player1.turn = 0;
                player2.turn = 1;
            }

            if (player1.entry == 1 && player1.turn == 1) {

                update(graphics);
                if (player1.reverseMove == 0) {

                    player1.xPosition += 60 * diceNumber;
                    if (player1.xPosition > 605) {

                        player1.yPosition -= 60;
                        player1.xPosition = 605 - 60 * (((player1.xPosition - 605) / 60) - 1);
                        player1.reverseMove = 1;
                    }
                } else {

                    player1.xPosition -= 60 * diceNumber;

                    if (player1.yPosition != 90) {

                        if (player1.xPosition < 65) {

                            player1.yPosition -= 60;
                            if (player1.xPosition > 0)
                                player1.xPosition = 65 + 60 * (((65 - player1.xPosition) / 60) - 1);
                            else
                                player1.xPosition = 65 + 60 * ((((-1 * player1.xPosition) + 65) / 60) - 1);
                            player1.reverseMove = 0;
                        }
                    } else {

                        if (player1.xPosition < 65) {

                            player1.xPosition += 60 * diceNumber;
                        } else if (player1.xPosition == 65) {

                            JOptionPane.showMessageDialog(null, "Player 1 Wins", "Winner", JOptionPane.INFORMATION_MESSAGE);
                            diceButton.setEnabled(false);
                            return;
                        }
                    }
                }

                graphics2D.setPaint(Color.red);
                checkPosition(player1);
                graphics2D.fillOval(player1.xPosition, player1.yPosition, 20, 20);

                if (player2.entry == 1) {

                    graphics2D.setPaint(Color.blue);
                    graphics2D.fillOval(player2.xPosition, player2.yPosition + 20, 20, 20);
                }
            }

            if (repeat == 1) {

                JOptionPane.showMessageDialog(null, "Roll Again");
                player1.turn = 1;
                player2.turn = 0;
                playerTurn.setForeground(new Color(255, 0, 0));
                playerTurn.setText("Player 1 Turn");
            } else {
                player2.turn = 1;
                player1.turn = 0;
                playerTurn.setForeground(new Color(0, 0, 255));
                playerTurn.setText("Player 2 Turn");
            }

        } else {

            if (player2.entry == 0 && diceNumber == 1) {

                graphics2D.setPaint(Color.blue);
                graphics2D.fillOval(player2.xPosition, player2.yPosition + 20, 20, 20);
                player2.entry = 1;
                player1.turn = 1;
                player2.turn = 0;
            }

            if (player2.entry == 1 && player2.turn == 1) {
                update(graphics);

                if (player2.reverseMove == 0) {

                    player2.xPosition += 60 * diceNumber;
                    if (player2.xPosition > 605) {
                        player2.yPosition -= 60;
                        player2.xPosition = 605 - 60 * (((player2.xPosition - 605) / 60) - 1);
                        player2.reverseMove = 1;
                    }
                } else {
                    player2.xPosition -= 60 * diceNumber;
                    if (player2.yPosition != 90) {
                        if (player2.xPosition < 65) {

                            player2.yPosition -= 60;
                            if (player2.xPosition > 0)
                                player2.xPosition = 65 + 60 * (((65 - player2.xPosition) / 60) - 1);
                            else
                                player2.xPosition = 65 + 60 * ((((-1 * player2.xPosition) + 65) / 60) - 1);
                            player2.reverseMove = 0;
                        }
                    } else {
                        if (player2.xPosition < 65)
                            player2.xPosition += 60 * diceNumber;
                        else if (player2.xPosition == 65) {
                            JOptionPane.showMessageDialog(null, "Player 2 Wins", "Winner", JOptionPane.INFORMATION_MESSAGE);
                            diceButton.setEnabled(false);
                            return;
                        }
                    }
                }
                graphics2D.setPaint(Color.blue);
                checkPosition(player2);
                graphics2D.fillOval(player2.xPosition, player2.yPosition + 20, 20, 20);
                if (player1.entry == 1) {
                    graphics2D.setPaint(Color.red);
                    graphics2D.fillOval(player1.xPosition, player1.yPosition, 20, 20);
                }
            }
            if (repeat == 1) {
                JOptionPane.showMessageDialog(null, "Roll Again");
                player2.turn = 1;
                player1.turn = 0;
                playerTurn.setForeground(new Color(0, 0, 255));
                playerTurn.setText("Player 2 Turn");
            } else {
                player1.turn = 1;
                player2.turn = 0;
                playerTurn.setForeground(new Color(255, 0, 0));
                playerTurn.setText("Player 1 Turn");
            }

        }
        diceButton.setEnabled(true);
        diceButton.requestFocus();
    }

    private void checkPosition(Token p) {
        if (p.xPosition == 485 && p.yPosition == 630) //8
        {
            p.xPosition = 605;
            p.yPosition = 450;
            p.reverseMove = 1;
        } else if (p.xPosition == 365 && p.yPosition == 570) //15
        {
            p.xPosition = 245;
            p.yPosition = 90;
            p.reverseMove = 1;
        } else if (p.xPosition == 245 && p.yPosition == 510) //24
        {
            p.xPosition = 65;
            p.yPosition = 630;
            p.reverseMove = 0;
        } else if (p.xPosition == 125 && p.yPosition == 390) //42
        {
            p.xPosition = 65;
            p.yPosition = 150;
            p.reverseMove = 0;
        } else if (p.xPosition == 365 && p.yPosition == 330) //55
        {
            p.xPosition = 485;
            p.yPosition = 570;
            p.reverseMove = 1;
        } else if (p.xPosition == 365 && p.yPosition == 270) //66
        {
            p.xPosition = 425;
            p.yPosition = 150;
            p.reverseMove = 0;
        } else if (p.xPosition == 605 && p.yPosition == 210) //71
        {
            p.xPosition = 545;
            p.yPosition = 510;
            p.reverseMove = 0;
        } else if (p.xPosition == 485 && p.yPosition == 150) //88
        {
            p.xPosition = 425;
            p.yPosition = 270;
            p.reverseMove = 0;
        } else if (p.xPosition == 125 && p.yPosition == 90) //99
        {
            p.xPosition = 365;
            p.yPosition = 630;
            p.reverseMove = 0;
        }
    }

    private void rollDice() {

        diceButton.setEnabled(false);
        Random rand = new Random();
        int roll = rand.nextInt(6) + 1;
        diceValue.setText(String.valueOf(roll));

        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        final int[] secondsToWait = {20};
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Random rand = new Random();
                int roll = rand.nextInt(6) + 1;
                diceValue.setText(String.valueOf(roll));
                secondsToWait[0]--;
                if (secondsToWait[0] == 0) {
                    exec.shutdown();
                    setPlayerPosition(roll);
                }
            }
        };

        exec.scheduleAtFixedRate(task, 100, 100, TimeUnit.MILLISECONDS);

    }

    protected class Token {

        int xPosition, yPosition, turn, entry, reverseMove;

        private Token() {

            xPosition = 65;
            yPosition = 630;
            turn = entry = reverseMove = 0;
        }
    }

    protected class ImagePanel extends JPanel {

        private Image image;

        private ImagePanel() {

            try {
                URL url = this.getClass().getResource("image.jpg");
                image = ImageIO.read(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void paintComponent(Graphics graphics) {
            if (image == null)
                return;
            graphics.drawImage(image, 50, 30, 600, 600, null);
        }
    }
}

public class SnakesAndLadders {

    public static void main(String[] args) {

        Game game = new Game();
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setVisible(true);

    }
}
