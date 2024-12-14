package GameEngine;

import tomato.crush.LevelList;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class GameEngine1 {

    private String thePlayer = null;
    private int counter = 0;
    private int score = 0;
    private List<Game> games;
    private Game current;

    private int totalAttempts = 0;
    private int correctAnswers = 0;

    private GameServer theGames = new GameServer();

    private int answeredQuestions = 0;

    private LevelList[] gameLevels;
    private LevelList currentLevel;

    public GameEngine1(String player, int selectedLevel) {
        thePlayer = player;
        initializeGameLevels();
        setCurrentLevel(selectedLevel);
    }

    public void setCurrentLevel(int selectedLevel) {
        if (selectedLevel >= 1 && selectedLevel <= gameLevels.length) {
            currentLevel = gameLevels[selectedLevel - 1];
        } else {
            System.out.println("Invalid level selection");
        }
    }

    public LevelList getCurrentLevel() {
        return currentLevel;
    }
// game level conditions
    public void initializeGameLevels() {

        gameLevels = new LevelList[]{
            new LevelList(1, 5, 180, 3),
            new LevelList(2, 10, 300, 7),
            new LevelList(3, 15, 420, 12),
            new LevelList(4, 20, 540, 16),
            new LevelList(5, 25, 660, 21),
            new LevelList(6, 30, 780, 25),
            new LevelList(7, 35, 900, 30),
            new LevelList(8, 40, 1020, 35),
            new LevelList(9, 45, 1140, 40),
            new LevelList(10, 50, 1260, 45),};
    }
// image loading 
    public BufferedImage nextGame() {
        if (answeredQuestions <= currentLevel.getAnswerCount()) {
            current = theGames.getRandomGame();
            counter++;

            return current.getImage(); // Returns the image of the next game
        } else {

        }
        return null;
    }

    public int getNextLevelIndex() {
        for (int i = 0; i < gameLevels.length - 1; i++) {
            if (currentLevel == gameLevels[i]) {
                return i + 1;
            }
        }
        return -1;
    }

    public boolean checkSolution(int i) {
        if (!isGameOver()) {
            totalAttempts++;

            if (i == current.getSolution()) {
                score++;
                correctAnswers++;
                answeredQuestions++;  // Increment the count of answered questions for the current level
                return true;  // Correct answer
            } else {
                answeredQuestions++;
                return false;  // Incorrect answer
            }
        } else {
            return false;
        }
    }

    private boolean isGameOver() {
        return answeredQuestions >= currentLevel.getAnswerCount();
    }

    private boolean hasEnoughCorrectAnswers() {
        return correctAnswers >= currentLevel.getCorrectAnswersToPass();
    }

    public int getScore() {
        return score;
    }

    public int getGameCounter() {
        return counter;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public LevelList[] getGameLevels() {
        return gameLevels;
    }

    public void setCurrentLevel(LevelList level) {
        currentLevel = level;
    }

    public int getCurrentLevelNumber() {
        return Arrays.asList(gameLevels).indexOf(currentLevel) + 1;
    }

}
