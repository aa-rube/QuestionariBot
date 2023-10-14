package app.bot.admin;

import app.bot.model.BotUser;
import app.questionary.repository.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AudienceGrowthCalculator {
    @Autowired
    private MongoService mongo;

    public AudienceGrowthCalculator(MongoService mongo) {
        this.mongo = mongo;
    }

    public int getNewUsersToday() {
        LocalDate today = LocalDate.now();
        List<BotUser> users = mongo.getBotUsersByRegistrationDate(today, today);
        return users.size();
    }

    public int getNewUsersLastSevenDays() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        List<BotUser> users = mongo.getBotUsersByRegistrationDate(startDate, endDate);
        return users.size();
    }

    public int getNewUsersLastThirtyDays() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        List<BotUser> users = mongo.getBotUsersByRegistrationDate(startDate, endDate);
        return users.size();
    }
}
