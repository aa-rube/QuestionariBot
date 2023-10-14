package app.questionary.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionText;
    private String filePath;
    private File pic;
    private List<Option> options = new ArrayList<>();

    public Question(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Option> getOptions() {
        return options;
    }

    public int getOptionsLastElementIndex() {
        return options.size() - 1;
    }

    public File getPic() {
        return pic;
    }

    public void setPic(File pic) {
        this.pic = pic;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}


