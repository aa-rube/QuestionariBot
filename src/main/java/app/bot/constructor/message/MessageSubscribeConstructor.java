package app.bot.constructor.message;

import app.bot.constructor.keyboards.SubscribeKeyboardConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class MessageSubscribeConstructor {
    @Autowired
    private SubscribeKeyboardConstructor keyboard;
    private SendMessage getSendMsgObject(Long chatId, String text, InlineKeyboardMarkup kb) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(kb);
        return msg;
    }
    public SendMessage getSubscribeOffer(Long chatId, Update update) {
        String text = "Привет, @" +update.getMessage().getFrom().getUserName() + "! \n"
                + "Перед тем как создавать свои тесты, подпишись на наших спонсоров\uD83D\uDE09";
        return getSendMsgObject(chatId, text, keyboard.getSubscribeButtons());
    }
}
