package app.questionary.constructor.message;

import app.questionary.constructor.keyboards.PassTheTestKeyBoardsConstructor;
import app.questionary.model.Questioner;
import app.questionary.model.Result;
import app.questionary.repository.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class MessageForPassTest {
    @Autowired
    private PassTheTestKeyBoardsConstructor keyboard;
    @Autowired
    private MongoService mongo;
    private final StringBuilder builder = new StringBuilder();

    private SendMessage getSendMsgObject(Long chatId, String text, InlineKeyboardMarkup k) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(k);
        return msg;
    }

    private SendPhoto getSendPhotoMsgObject(Long chatId, String text, InlineKeyboardMarkup kb, String fileId) {
        SendPhoto msg = new SendPhoto();
        msg.setChatId(chatId);
        msg.setCaption(text);
        msg.setReplyMarkup(kb);
        msg.setPhoto(new InputFile(fileId));
        return msg;
    }


    private String getTextForTestPresentMessage(Questioner questioner) {
        builder.setLength(0);
        builder.append("Тест: ").append(questioner.getName()).append("\n")
                .append("Вопросов: ").append(questioner.getQuestions().size());
        return builder.toString();
    }

    public SendMessage getTextForMessageForChooseTestForEditOrPass(Long chatId, Questioner questioner) {
        return getSendMsgObject(chatId, getTextForTestPresentMessage(questioner),
                keyboard.getChangeParamsQuestionerOrPassTheTestKeys(questioner.getName(), questioner.getQuestionerId()));
    }

    public SendPhoto getTextForMessageForChooseTestForEditOrPassPhoto(Long chatId, Questioner questioner) {
        String fileId = questioner.getFilePath();
        return getSendPhotoMsgObject(chatId, getTextForTestPresentMessage(questioner),
                keyboard.getChangeParamsQuestionerOrPassTheTestKeys(questioner.getName(), questioner.getQuestionerId()), fileId);
    }


    private String getTextForQuestion(Questioner questioner, int i) {
        builder.setLength(0);
        builder.append(questioner.getQuestions().get(i).getQuestionText())
                .append("\n\n Ответы:");
        return builder.toString();
    }

    public SendMessage getMessageQuestion(Long chatId, Questioner questioner, int i) {
        return getSendMsgObject(chatId, getTextForQuestion(questioner, i),
                keyboard.getOptions(questioner.getQuestions().get(i).getOptions(), i));
    }

    public SendPhoto getMessageQuestionPhoto(Long chatId, Questioner questioner, int i) {

        String fileId = questioner.getQuestions().get(i).getFilePath();
        return getSendPhotoMsgObject(chatId, getTextForQuestion(questioner, i),
                keyboard.getOptions(questioner.getQuestions().get(i).getOptions(), i), fileId);

    }

    private String getResultText(Result result) {
        builder.setLength(0);
        builder.append("Ваш результат:\n").append(result.getResultText());
        return builder.toString();
    }

    public SendMessage getResultTestMessage(Long chatId, Result result) {
        return getSendMsgObject(chatId, getResultText(result), null);
    }

    public SendPhoto getResultTestMessagePhoto(Long chatId, Result result) {
        String fileId = result.getFilePath();
        return getSendPhotoMsgObject(chatId, getResultText(result), null, fileId);
    }

    public SendMessage getStarsForTest(Long chatId) {
        String text = "Вы ответили на все вопросы, пожалуйста, оцените тест, пока мы подсчитываем результаты...";
        return getSendMsgObject(chatId, text, keyboard.stars());
    }
}
