package app.questionary.model;

public class Option {
    private String optionText;
    private Integer score;

    public Option(String optionText) {
        this.optionText = optionText;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }


}