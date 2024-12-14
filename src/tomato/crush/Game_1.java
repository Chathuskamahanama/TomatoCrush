/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tomato.crush;

//import tomato.crush.DatabaseConnector;
//import java.awt.BorderLayout;
//import java.awt.Image;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.image.BufferedImage;
//import javax.swing.ImageIcon;
//import javax.swing.JButton;
//import javax.swing.JLabel;
import javax.swing.JOptionPane;
import GameEngine.GameEngine1;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import tomato.crush.Levels;

/**
 *
 * @author Chathuska Mahanama
 */
/**
 * Creates new form Game
 */
public class Game_1 extends javax.swing.JFrame implements ActionListener {

    private GameEngine1 gameEngine;
    private BufferedImage currentGame;
    private String username;
    private int totalImagesInLevel;

    private Timer gameTimer;
    private int remainingTimeInSeconds;

    private int answeredQuestions = 0;
    private int totalAttempts = 0;
    private int correctAnswers = 0;

    private LevelList[] gameLevels;
    private LevelList currentLevel;
    private LevelList selectedLevelList;

    public Game_1(GameEngine1 gameEngine, String username) {
        initComponents();
        this.gameEngine = gameEngine;
        this.username = username;
        initGame();
        initTimer();
        updateLevelLabel();

        String displayName = fetchDisplayName(username);
        jLabel2.setText(displayName);

        int score = fetchScore(username);
        jLabel5.setText("Score: " + String.valueOf(score));
    }

    private void initializeGameLevels() {

        gameLevels = gameEngine.getGameLevels();
        initTimer();
    }

    public Game_1(GameEngine1 gameEngine, LevelList selectedLevelList, String username) {
        initComponents();
        this.gameEngine = gameEngine;
        this.selectedLevelList = selectedLevelList;
        this.username = username;
        initGame();
        initTimer();
        updateLevelLabel();

        String displayName = fetchDisplayName(username);
        jLabel2.setText(displayName);

        int score = fetchScore(username);
        jLabel5.setText("Score: " + String.valueOf(score));
    }

    private void initGame() {

        currentGame = gameEngine.nextGame();

        jPanel4.setLayout(new BorderLayout());
// Get the image from engine and set it to the panel
        JLabel imageLabel = new JLabel();
        ImageIcon initialImageIcon = new ImageIcon(currentGame);

        jPanel4.add(imageLabel);

        Image originalImage = initialImageIcon.getImage();

        int panelWidth = jPanel4.getWidth();
        int panelHeight = jPanel4.getHeight();
        Image scaledImage = originalImage.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);

        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        imageLabel.setIcon(scaledImageIcon);

        JButton[] answerButtons = {
            jButton2, jButton3, jButton4, jButton5, jButton6,
            jButton7, jButton8, jButton9, jButton10, jButton1
        };

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(String.valueOf(i));
            answerButtons[i].addActionListener(this);
        }

    }

    private void updateLevelLabel() {
        jLabel3.setText("Level: " + gameEngine.getCurrentLevelNumber());
    }

    // Load the game from 2nd image
    // Check answer
    public void actionPerformed(ActionEvent e) {
        int solution = Integer.parseInt(e.getActionCommand());
        boolean correct = gameEngine.checkSolution(solution);

        System.out.println("ActionPerformed - Correct: " + correct);

        if (correct) {
            System.out.println("Correct solution entered!");

            currentGame = gameEngine.nextGame();
            checkGameOver();
            jPanel4.removeAll();
            jPanel4.setLayout(new BorderLayout());

            JLabel imageLabel = new JLabel();
            jPanel4.add(imageLabel);

            ImageIcon ii = new ImageIcon(currentGame);

            int panelWidth = jPanel4.getWidth();
            int panelHeight = jPanel4.getHeight();

            ImageIcon initialImageIcon = new ImageIcon(currentGame);
            Image originalImage = initialImageIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(scaledImageIcon);

            jLabel6.setText("Good!");

        } else {
            System.out.println("Not Correct");
            jLabel6.setText("Oops. Try again!");

            currentGame = gameEngine.nextGame();
            checkGameOver(); // Check game over condition before updating the image
            jPanel4.removeAll();
            jPanel4.setLayout(new BorderLayout());

            JLabel imageLabel = new JLabel();
            jPanel4.add(imageLabel);

            // Set the new image
            ImageIcon ii = new ImageIcon(currentGame);

            int panelWidth = jPanel4.getWidth();
            int panelHeight = jPanel4.getHeight();

            ImageIcon initialImageIcon = new ImageIcon(currentGame);
            Image originalImage = initialImageIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(scaledImageIcon);

        }
    }
    //...........................................DATABASE........................................................................................................................

    private String fetchDisplayName(String username) {
        String userName = null;

        try (Connection connection = DatabaseConnector.connect(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT user_name FROM players WHERE user_name = ?")) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userName = resultSet.getString("user_name");
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return userName;
    }

    private int fetchAndUpdateScore(String username, int additionalScore) {
        int currentScore = fetchScore(username);

        int newScore = currentScore + additionalScore;

        try (Connection connection = DatabaseConnector.connect(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET score = ? WHERE user_name = ?")) {

            preparedStatement.setInt(1, newScore);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return newScore;
    }

    private int fetchScore(String username) {
        int score = 0;

        try (Connection connection = DatabaseConnector.connect(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT score FROM players WHERE user_name = ?")) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    score = resultSet.getInt("score");
                }
            }

        } catch (SQLException e) {

        }

        return score;
    }

    private void handleSQLException(SQLException e) {
        e.printStackTrace();
    }
// Update playerprogress table on the database player_id, level_id, completed, correct_answers, wrong_answers, time_to_complete 

    private void storeLevelProgressToDatabase() {
        try (Connection connection = DatabaseConnector.connect()) {
            int playerId = getPlayerIdByUsername(username);
            int levelId = gameEngine.getCurrentLevelNumber();
            int correctAnswers = gameEngine.getCorrectAnswers();
            int currentScoreOnDatabase = getPlayerScoreFromDatabase(playerId);  // New line to get the current score

            // Calculate the new score by adding the current score from the database and the new score
            int newScore = currentScoreOnDatabase + (correctAnswers * 10);

            int wrongAnswers = gameEngine.getTotalAttempts() - correctAnswers;

            int timeToCompleteInSeconds = gameEngine.getCurrentLevel().getTimeLimitInSeconds() - remainingTimeInSeconds;
            int minutes = timeToCompleteInSeconds / 60;
            int seconds = timeToCompleteInSeconds % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);

            String insertQuery = "INSERT INTO playerprogress (player_id, level_id, completed, correct_answers, wrong_answers, time_to_complete) VALUES (?, ?, 'yes', ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setInt(1, playerId);
                preparedStatement.setInt(2, levelId);
                preparedStatement.setInt(3, correctAnswers);
                preparedStatement.setInt(4, wrongAnswers);
                preparedStatement.setString(5, formattedTime);
                preparedStatement.executeUpdate();

                // Update the player's score in the 'players' table with the new score
                String updateScoreQuery = "UPDATE players SET score = ? WHERE id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateScoreQuery)) {
                    updateStatement.setInt(1, newScore);  // Use the new score here
                    updateStatement.setInt(2, playerId);
                    updateStatement.executeUpdate();
                }

                System.out.println("Level progress and score stored to the database.");

            } catch (SQLException e) {
                handleSQLException(e);
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

// Helper method to get the current score from the database
    private int getPlayerScoreFromDatabase(int playerId) throws SQLException {
        String query = "SELECT score FROM players WHERE id = ?";
        try (Connection connection = DatabaseConnector.connect(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, playerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("score");
                }
            }
        }
        return 0; // Return 0 if no score is found (you can adjust this based on your logic)
    }

    private int getPlayerIdByUsername(String username) {
        int playerId = -1;

        try (Connection connection = DatabaseConnector.connect(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM players WHERE user_name = ?")) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    playerId = resultSet.getInt("id");
                }
            }

        } catch (SQLException e) {
            handleSQLException(e);
        }

        return playerId;
    }

    //...........................................TIMER........................................................................................................................
    private void initTimer() { //set timer Initialize Timer
        remainingTimeInSeconds = gameEngine.getCurrentLevel().getTimeLimitInSeconds();
        System.out.println("Initial Time Limit: " + remainingTimeInSeconds);
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTimerTick();
            }
        });
        gameTimer.start();
    }

    private void handleTimerTick() { // Timer Countdown
        remainingTimeInSeconds--;

        if (remainingTimeInSeconds >= 0) {

            int minutes = remainingTimeInSeconds / 60;
            int seconds = remainingTimeInSeconds % 60;
            String formattedTime = String.format("%02d:%02d", minutes, seconds);
            txtTimer.setText(formattedTime);
        } else {

            gameTimer.stop();
            handleGameTimer();
        }
    }

    private void handleGameTimer() { //Game over after timer end

        JOptionPane.showMessageDialog(
                this,
                "Game Over! Time Ran Out\nTotal Attempts: " + gameEngine.getTotalAttempts()
                + "\nCorrect Answers: " + gameEngine.getCorrectAnswers()
        );

        stopTimer();
        gameTimer = null;

        Levels form = new Levels(username);
        form.setVisible(true);
        form.pack();
        form.setLocationRelativeTo(null);
        this.dispose();
    }

    private void stopTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    //......................................................Game Over............................................................................................................
    private void checkGameOver() {

//        System.out.println("Total Attempts: " + gameEngine.getTotalAttempts());
//        System.out.println("Correct Answers: " + gameEngine.getCorrectAnswers());
//        System.out.println("Correct Answers for the level: " + gameEngine.getCurrentLevel().getCorrectAnswersToPass());
//        System.out.println("   ");
//        System.out.println("............................................................................................................");
//        System.out.println("   ");
// 5 >= 5
        if (gameEngine.getTotalAttempts() >= gameEngine.getCurrentLevel().getAnswerCount()) { // check Game over - imagecount

            // 3 >= 3
            if (gameEngine.getCorrectAnswers() >= gameEngine.getCurrentLevel().getCorrectAnswersToPass()) { //

                JOptionPane.showMessageDialog(
                        this,
                        "Congratulations! You passed the level!\nTotal Attempts: " + gameEngine.getTotalAttempts()
                        + "\nCorrect Answers: " + gameEngine.getCorrectAnswers()
                );
                storeLevelProgressToDatabase();
                stopTimer();
                gameTimer = null;

                Levels form = new Levels(username);
                form.setVisible(true);
                form.pack();
                form.setLocationRelativeTo(null);
                this.dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Game Over!\nTotal Attempts: " + gameEngine.getTotalAttempts()
                        + "\nCorrect Answers: " + gameEngine.getCorrectAnswers()
                );

                stopTimer();
                gameTimer = null;
                initTimer();

                Levels form = new Levels(username);
                form.setVisible(true);
                form.pack();
                form.setLocationRelativeTo(null);
                this.dispose();
            }
        } else {

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtTimer = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel2.setText("Chathuska");

        jLabel3.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel3.setText("Level 01");

        txtTimer.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        txtTimer.setText("02 : 34");

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel5.setText("Score :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel2)
                .addGap(105, 105, 105)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addComponent(txtTimer)
                .addGap(73, 73, 73)
                .addComponent(jLabel5)
                .addGap(44, 44, 44))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(txtTimer)
                    .addComponent(jLabel5))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 600, 50));

        jLabel6.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 0, 51));
        jLabel6.setText("Hints :  Please Try  againg....... ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel6)
                .addContainerGap(347, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 330, 600, 20));

        jButton1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 0, 0));
        jButton1.setText("9");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 0, 0));
        jButton2.setText("0");

        jButton3.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 0, 0));
        jButton3.setText("1");

        jButton4.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 0, 0));
        jButton4.setText("2");

        jButton5.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 0, 0));
        jButton5.setText("3");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 0, 0));
        jButton6.setText("4");

        jButton7.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 0, 0));
        jButton7.setText("5");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 0, 0));
        jButton8.setText("6");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 0, 0));
        jButton9.setText("7");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 0, 0));
        jButton10.setText("8");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(0, 0, 102));
        jButton12.setText("Answer");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton2)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jButton12))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 360, 600, 40));

        jButton11.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(0, 0, 102));
        jButton11.setText("Exit");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 410, -1, -1));

        jButton13.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton13.setForeground(new java.awt.Color(0, 0, 102));
        jButton13.setText("Hint");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 410, -1, -1));

        jButton15.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jButton15.setForeground(new java.awt.Color(0, 0, 102));
        jButton15.setText("Back");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton15, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 410, -1, -1));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 600, 250));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/menu backgrond.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 450));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        Levels form = new Levels(username);
        form.setVisible(true);
        form.pack();
        form.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jButton15ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Create an instance of GameEngine1 with a player name and selected level
                GameEngine1 gameEngine = new GameEngine1("YourDefaultUsername", 1);

                // Retrieve the game levels
                LevelList[] gameLevels = gameEngine.getGameLevels();

                // Assuming 5 images for level 1 as a placeholder, update it based on your logic
                int selectedLevel = 1;
                LevelList selectedLevelList = gameLevels[selectedLevel - 1];

                // Create an instance of Game_1 with the initialized gameEngine and selectedLevelList
                new Game_1(gameEngine, selectedLevelList, "YourDefaultUsername").setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel txtTimer;
    // End of variables declaration//GEN-END:variables

}
