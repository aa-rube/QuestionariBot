package app.questionary.constructor.inlineQueryData;

import app.questionary.model.Questioner;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class InlineQueryData {
    private InlineKeyboardMarkup getMarkup(String botUserName, Questioner questioner) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> startRow = new ArrayList<>();
        InlineKeyboardButton start = new InlineKeyboardButton();
        start.setText("Начать тест");
        String s = "https://t.me/" + botUserName + "?start=" + "got_" + questioner.getQuestionerId();
        start.setUrl(s);

        startRow.add(start);
        keyboard.add(startRow);
        markup.setKeyboard(keyboard);
        return markup;
    }

    public InlineQueryResultPhoto getInlineQueryResult(String name, Questioner questioner) {
        InlineQueryResultPhoto article = new InlineQueryResultPhoto();

        article.setPhotoUrl(questioner.getFilePath());
        article.setThumbUrl(questioner.getFilePath());

        article.setId(questioner.getQuestionerId());
        article.setTitle(questioner.getName());

        article.setDescription("Рейтинг " + questioner.getAverageScore() + "⭐️ "
                + "\nПройдено раз: " + questioner.getPassCount());

        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.setMessageText("Давай пройдем тест \"" + questioner.getName() + "\"");

        InlineKeyboardMarkup markup = getMarkup(name, questioner);
        article.setReplyMarkup(markup);
        article.setInputMessageContent(messageContent);
        return article;
    }
}
