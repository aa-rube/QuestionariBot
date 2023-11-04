package app.questionary.constructor;

import app.bot.model.BotUser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.time.LocalDateTime;

@Service
public class BotUserConstructor {
    public BotUser getBotUserObject(Long chatId, Update update, String userName) {
        BotUser user = new BotUser();
        user.setDateReg(LocalDateTime.now());
        user.setUserName(userName);
        user.setChatId(chatId);
        user.setSubscribe(true);
        return user;
    }
}
