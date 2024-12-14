/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package tomato.crush;

import GameEngine.GameEngine1;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Chathuska Mahanama
 */
public class Levels extends javax.swing.JFrame {

    private final String username;
    private GameEngine1 gameEngine;
    int totalImages;
    private LevelList[] gameLevels;

    /**
     * Creates new form Levels
     */
    public Levels(String username) {
        initComponents();

        this.username = username;
        jLabel6.setText(username);

        int score = fetchScore(username);
        jLabel8.setText("Score: " + String.valueOf(score));

        this.gameEngine = new GameEngine1(username, totalImages);

        // Move the initialization here
        this.totalImages = gameEngine.getGameLevels()[0].getimageCount();
        this.gameLevels = gameEngine.getGameLevels();

        // Call the method to initialize levels
        initializeLevels(username);

        // Add mouse click event listeners to level labels
        addLevelClickListener(L1, 1);
        addLevelClickListener(L2, 2);
        addLevelClickListener(L3, 3);
        addLevelClickListener(L4, 4);
        addLevelClickListener(L5, 5);
        addLevelClickListener(L6, 6);
        addLevelClickListener(L7, 7);
        addLevelClickListener(L8, 8);
        addLevelClickListener(L9, 9);
        addLevelClickListener(L10, 10);
    }

    // Function to add mouse click event listener to a level label
    private void addLevelClickListener(javax.swing.JLabel label, final int level) {
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openLevel(level);
            }
        });
    }

    private void initializeLevels(String username) {
    // Fetch completed levels for the user
    Set<Integer> completedLevels = fetchCompletedLevels(username);

    // Add mouse click event listeners to level labels
    for (int i = 1; i <= 10; i++) {
        javax.swing.JLabel tickLabel = getTickLabel(i);

        // Check if the level is completed
        boolean isCompleted = completedLevels.contains(i);

        // Set the visibility of the tick labels based on completed levels
        setLabelVisibility(tickLabel, isCompleted);
        addLevelClickListener(tickLabel, i);
    }

    // Check if there are no completed levels
    if (completedLevels.isEmpty()) {
        // If no completed levels, mark Level 1 as unlocked
        setLabelVisibility(getLockLabel(1), false);
        setLabelVisibility(getLevelLabel(1), true);
    } else {
        // Iterate through all levels (assuming they are sequential from 1 to 10)
        for (int i = 1; i <= 10; i++) {
            javax.swing.JLabel levelLabel = getLevelLabel(i);
            javax.swing.JLabel lockLabel = getLockLabel(i);

            // Check if the level is completed
            boolean isCompleted = completedLevels.contains(i);

            // Set the visibility of the labels based on completed levels
            setLabelVisibility(levelLabel, isCompleted || completedLevels.contains(i - 1));
            setLabelVisibility(lockLabel, !isCompleted && !completedLevels.contains(i - 1));
        }
    }
}

// Helper method to set the visibility of a label
private void setLabelVisibility(javax.swing.JLabel label, boolean visible) {
    if (label != null) {
        label.setVisible(visible);
    }
}

// Helper method to get the Lock label based on level number
private javax.swing.JLabel getLockLabel(int level) {
    switch (level) {
        case 1:
            return lbllock1;
        case 2:
            return lbllock2;
        case 3:
            return lbllock3;
        case 4:
            return lbllock4;
        case 5:
            return lbllock5;
        case 6:
            return lbllock6;
        case 7:
            return lbllock7;
        case 8:
            return lbllock8;
        case 9:
            return lbllock9;
        case 10:
            return lbllock10;
        default:
            return null;
    }
}

// Helper method to get the Level label based on level number
private javax.swing.JLabel getLevelLabel(int level) {
    switch (level) {
        case 1:
            return L1;
        case 2:
            return L2;
        case 3:
            return L3;
        case 4:
            return L4;
        case 5:
            return L5;
        case 6:
            return L6;
        case 7:
            return L7;
        case 8:
            return L8;
        case 9:
            return L9;
        case 10:
            return L10;
        default:
            return null;
    }
}

// Helper method to get the Tick label based on level number
private javax.swing.JLabel getTickLabel(int level) {
    switch (level) {
        case 1:
            return lbltick1;
        case 2:
            return lbltick2;
        case 3:
            return lbltick3;
        case 4:
            return lbltick4;
        case 5:
            return lbltick5;
        case 6:
            return lbltick6;
        case 7:
            return lbltick7;
        case 8:
            return lbltick8;
        case 9:
            return lbltick9;
        case 10:
            return lbltick10;
        default:
            return null;
    }
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
            // Handle the exception, e.g., log or print the error
            e.printStackTrace();
        }

        return score;
    }

   

    // Function to fetch completed levels for the user
    private Set<Integer> fetchCompletedLevels(String username) {
        Set<Integer> completedLevels = new HashSet<>();

        try (Connection connection = DatabaseConnector.connect(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT level_id FROM playerprogress WHERE player_id = (SELECT id FROM players WHERE user_name = ?) AND completed = 'yes'")) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int levelId = resultSet.getInt("level_id");

                    completedLevels.add(levelId);
                }
            }

        } catch (SQLException e) {
            // Handle the exception, e.g., log or print the error
            e.printStackTrace();
        }

        return completedLevels;
    }

    private void openLevel(int selectedLevel) {
        // Check if the gameEngine is null (add this check to avoid NullPointerException)
        if (gameEngine != null) {

            LevelList selectedLevelList = gameLevels[selectedLevel - 1];
            LevelList x = gameLevels[selectedLevel - 1];
            gameEngine.setCurrentLevel(selectedLevelList);

            Game_1 game1 = new Game_1(gameEngine, selectedLevelList, username); // Pass the selectedLevelList
            game1.setVisible(true);

            this.dispose();
        } else {
            System.out.println("Game engine is null. Cannot open level.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbltick10 = new javax.swing.JLabel();
        lbltick9 = new javax.swing.JLabel();
        lbltick8 = new javax.swing.JLabel();
        lbltick3 = new javax.swing.JLabel();
        lbllock10 = new javax.swing.JLabel();
        lbltick5 = new javax.swing.JLabel();
        lbltick6 = new javax.swing.JLabel();
        lbltick7 = new javax.swing.JLabel();
        lbltick2 = new javax.swing.JLabel();
        lbltick1 = new javax.swing.JLabel();
        lbltick4 = new javax.swing.JLabel();
        lbllock1 = new javax.swing.JLabel();
        lbllock2 = new javax.swing.JLabel();
        lbllock3 = new javax.swing.JLabel();
        lbllock4 = new javax.swing.JLabel();
        lbllock5 = new javax.swing.JLabel();
        lbllock6 = new javax.swing.JLabel();
        lbllock7 = new javax.swing.JLabel();
        lbllock8 = new javax.swing.JLabel();
        lbllock9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        L1 = new javax.swing.JLabel();
        L2 = new javax.swing.JLabel();
        L3 = new javax.swing.JLabel();
        L4 = new javax.swing.JLabel();
        L5 = new javax.swing.JLabel();
        L6 = new javax.swing.JLabel();
        L7 = new javax.swing.JLabel();
        L8 = new javax.swing.JLabel();
        L9 = new javax.swing.JLabel();
        L10 = new javax.swing.JLabel();
        Level_1 = new javax.swing.JLabel();
        Level_2 = new javax.swing.JLabel();
        Level_3 = new javax.swing.JLabel();
        Level_4 = new javax.swing.JLabel();
        Level_5 = new javax.swing.JLabel();
        Level_6 = new javax.swing.JLabel();
        Level_7 = new javax.swing.JLabel();
        Level_8 = new javax.swing.JLabel();
        Level_9 = new javax.swing.JLabel();
        Level_10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Exit = new javax.swing.JLabel();
        Back = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Background = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbltick10.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick10, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 300, 40, 50));

        lbltick9.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick9, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 290, 40, 50));

        lbltick8.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 290, 40, 50));

        lbltick3.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 40, 50));

        lbllock10.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock10, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 330, 40, 50));

        lbltick5.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 170, 40, 50));

        lbltick6.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        lbltick6.setText("o");
        getContentPane().add(lbltick6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 40, 50));

        lbltick7.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, 40, 50));

        lbltick2.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 70, 40, 50));

        lbltick1.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 40, 50));

        lbltick4.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbltick4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tick.png"))); // NOI18N
        getContentPane().add(lbltick4, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 170, 40, 50));

        lbllock1.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock1, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 40, 50));

        lbllock2.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 110, 40, 50));

        lbllock3.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 90, 40, 50));

        lbllock4.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 180, 40, 50));

        lbllock5.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 210, 40, 50));

        lbllock6.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock6, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 240, 40, 50));

        lbllock7.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock7, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 260, 40, 50));

        lbllock8.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock8, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 330, 40, 50));

        lbllock9.setFont(new java.awt.Font("Comic Sans MS", 1, 24)); // NOI18N
        lbllock9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/lock (1).png"))); // NOI18N
        getContentPane().add(lbllock9, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 330, 40, 50));

        jLabel2.setFont(new java.awt.Font("Comic Sans MS", 1, 22)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("LEVELS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 30, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/menu board.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 160, 80));

        jLabel8.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Score : 23300023");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, -1, -1));

        jLabel6.setFont(new java.awt.Font("Comic Sans MS", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("C.Mahanama");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, -1, -1));

        L1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L1.setForeground(new java.awt.Color(255, 255, 0));
        L1.setText("1");
        getContentPane().add(L1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, -1, -1));

        L2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L2.setForeground(new java.awt.Color(255, 255, 0));
        L2.setText("2");
        getContentPane().add(L2, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, -1, -1));

        L3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L3.setForeground(new java.awt.Color(255, 255, 0));
        L3.setText("3");
        getContentPane().add(L3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, -1, -1));

        L4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L4.setForeground(new java.awt.Color(255, 255, 0));
        L4.setText("4");
        getContentPane().add(L4, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 190, -1, -1));

        L5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L5.setForeground(new java.awt.Color(255, 255, 0));
        L5.setText("5");
        getContentPane().add(L5, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 220, -1, -1));

        L6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L6.setForeground(new java.awt.Color(255, 255, 0));
        L6.setText("6");
        getContentPane().add(L6, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 250, -1, -1));

        L7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L7.setForeground(new java.awt.Color(255, 255, 0));
        L7.setText("7");
        getContentPane().add(L7, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 270, -1, -1));

        L8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L8.setForeground(new java.awt.Color(255, 255, 0));
        L8.setText("8");
        getContentPane().add(L8, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 340, -1, -1));

        L9.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L9.setForeground(new java.awt.Color(255, 255, 0));
        L9.setText("9");
        getContentPane().add(L9, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 340, -1, -1));

        L10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        L10.setForeground(new java.awt.Color(255, 255, 0));
        L10.setText("10");
        getContentPane().add(L10, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 340, -1, -1));

        Level_1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 110, 60, 50));

        Level_2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 60, 50));

        Level_3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, 60, 50));

        Level_4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_4, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 180, 60, 50));

        Level_5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_5, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 210, 60, 50));

        Level_6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_6, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 240, 60, 50));

        Level_7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 260, 60, 50));

        Level_8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_8, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 330, 60, 50));

        Level_9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_9, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 330, 60, 50));

        Level_10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/tomato.png"))); // NOI18N
        getContentPane().add(Level_10, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 330, 60, 50));

        jLabel5.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Exit");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 400, -1, -1));

        jLabel4.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Exit");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 400, -1, -1));

        Exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/arrow back.png"))); // NOI18N
        getContentPane().add(Exit, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 390, 70, 40));

        Back.setFont(new java.awt.Font("Comic Sans MS", 1, 12)); // NOI18N
        Back.setForeground(new java.awt.Color(255, 255, 255));
        Back.setText("Back");
        Back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BackMouseClicked(evt);
            }
        });
        getContentPane().add(Back, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 390, -1, 40));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/arrow back1.png"))); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, -1, 40));

        Background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/New folder/Level background.png"))); // NOI18N
        getContentPane().add(Background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 600, 450));

        jLabel7.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("C.Mahanama");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void BackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BackMouseClicked
        // TODO add your handling code here:

        Menu form = new Menu(username);
        form.setVisible(true);
        form.pack();
        form.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_BackMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Levels.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Pass a username when creating an instance of Levels
                new Levels("your_username").setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Back;
    private javax.swing.JLabel Background;
    private javax.swing.JLabel Exit;
    private javax.swing.JLabel L1;
    private javax.swing.JLabel L10;
    private javax.swing.JLabel L2;
    private javax.swing.JLabel L3;
    private javax.swing.JLabel L4;
    private javax.swing.JLabel L5;
    private javax.swing.JLabel L6;
    private javax.swing.JLabel L7;
    private javax.swing.JLabel L8;
    private javax.swing.JLabel L9;
    private javax.swing.JLabel Level_1;
    private javax.swing.JLabel Level_10;
    private javax.swing.JLabel Level_2;
    private javax.swing.JLabel Level_3;
    private javax.swing.JLabel Level_4;
    private javax.swing.JLabel Level_5;
    private javax.swing.JLabel Level_6;
    private javax.swing.JLabel Level_7;
    private javax.swing.JLabel Level_8;
    private javax.swing.JLabel Level_9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel lbllock1;
    private javax.swing.JLabel lbllock10;
    private javax.swing.JLabel lbllock2;
    private javax.swing.JLabel lbllock3;
    private javax.swing.JLabel lbllock4;
    private javax.swing.JLabel lbllock5;
    private javax.swing.JLabel lbllock6;
    private javax.swing.JLabel lbllock7;
    private javax.swing.JLabel lbllock8;
    private javax.swing.JLabel lbllock9;
    private javax.swing.JLabel lbltick1;
    private javax.swing.JLabel lbltick10;
    private javax.swing.JLabel lbltick2;
    private javax.swing.JLabel lbltick3;
    private javax.swing.JLabel lbltick4;
    private javax.swing.JLabel lbltick5;
    private javax.swing.JLabel lbltick6;
    private javax.swing.JLabel lbltick7;
    private javax.swing.JLabel lbltick8;
    private javax.swing.JLabel lbltick9;
    // End of variables declaration//GEN-END:variables
}
