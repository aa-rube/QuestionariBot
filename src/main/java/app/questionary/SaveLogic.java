package app.questionary;

import app.questionary.model.Option;
import app.questionary.model.Question;
import app.questionary.model.Questioner;
import app.questionary.model.Result;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class SaveLogic {
    public boolean isMoreThanMaxResult(Questioner q) {
        int max = 0;
        int scoreMaxSum = 0;
        for (Question question : q.getQuestions()) {
            for (Option option : question.getOptions()) {
                if (option.getScore() > max) {
                    max = option.getScore();
                }

            }
            scoreMaxSum = scoreMaxSum + max;
        }

        Result maxResult = q.getResults().stream()
                .max(Comparator.comparing(Result::getTopRate))
                .orElse(null);

        assert maxResult != null;
        return scoreMaxSum > maxResult.getTopRate();

    }
}
