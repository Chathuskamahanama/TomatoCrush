package tomato.crush;

import java.util.List;

public class LevelList {

    private int AnswerCount;
    private int timeLimit;
    private int correctAnswersToPass; // New variable

    public LevelList(int levelNo, int AnswerCount, int timeLimit, int correctAnswersToPass) {
        this.AnswerCount = AnswerCount;
        this.timeLimit = timeLimit;
        this.correctAnswersToPass = correctAnswersToPass;
    }

     public int getimageCount() {
        return this.AnswerCount;
    }

    public int getTimeLimitInSeconds() {
        return timeLimit;
    }

    public int getCorrectAnswersToPass() {
        return correctAnswersToPass;
    }

    public int getTotalImages() {
        return AnswerCount;
    }

      public int getAnswerCount() {
        return AnswerCount;
    }
}
