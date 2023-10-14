package app.questionary.model;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Questioner {
    private Long chatId;
    private String name;
    List<Question> questions = new ArrayList<>();
    List<Result> results = new ArrayList<>();
    private String filePath;
    private String questionerId;
    private int passCount;
    private int userStarsCount;
    private int passWithoutStars;

    public Long getChatId() {
        return chatId;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Questioner() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public int getQuestionsListLastElementIndex() {
        return questions.size() - 1;
    }

    public List<Result> getResults() {
        return results;
    }

    public int getResultListLastElementNumber() {
        return results.size() -1;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getQuestionerId() {
        return questionerId;
    }

    public void setQuestionerId(String questionerId) {
        this.questionerId = questionerId;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public int getUserStarsCount() {
        return userStarsCount;
    }

    public void setUserStarsCount(int userStarsCount) {
        this.userStarsCount = userStarsCount;
    }

    public float getAverageScore() {
        if (passCount == 0) {
            return 0.0f;
        }

        float averageScore = (float) userStarsCount / (passCount - passWithoutStars);
        float limitedAverageScore = Math.min(Math.max(averageScore, 0.0f), 5.0f);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#.#", symbols);
        String formattedScore = decimalFormat.format(limitedAverageScore);

        return Float.parseFloat(formattedScore);
    }

    public int getPassWithoutStars() {
        return passWithoutStars;
    }

    public void setPassWithoutStars(int passWithoutStars) {
        this.passWithoutStars = passWithoutStars;
    }
}
