package app.questionary.model;

import java.io.File;

public class Result {
    private String resultText;
    private Integer topRate;
    private Integer lowRate;
    private File pic;
    private String filePath;

    public Result(String resultText) {
        this.resultText = resultText;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public Integer getTopRate() {
        return topRate;
    }

    public void setTopRate(Integer topLine) {
        this.topRate = topLine;
    }

    public Integer getLowRate() {
        return lowRate;
    }

    public void setLowRate(Integer downLine) {
        this.lowRate = downLine;
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
}
