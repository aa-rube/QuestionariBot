package app.bot.admin;

import app.bot.model.BotUser;
import app.questionary.model.Partner;
import app.questionary.repository.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class AdminMessage {
    @Autowired
    private AdminKeyboard keyboard;
    @Autowired
    private ModeratorKeyboard moderatorKeyboard;
    @Autowired
    private MongoService mongo;
    @Autowired
    private AudienceGrowthCalculator growthCalculator;

    private SendMessage getSendMsgObject(Long chatId, String text, InlineKeyboardMarkup kb) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(kb);
        return msg;
    }

    public SendMessage getStartMessage(long superUserChatId) {
        String text = "Привет админ. Это бот с тестами.\nНиже есть кнопки в инлайн.\nХорошего тебе дня!";
        return getSendMsgObject(superUserChatId, text, keyboard.getStartKeyboard());
    }

    public SendMessage getStartModeratorMessage(long superUserChatId) {
        String text = "Привет. Это бот с тестами.\nНиже есть кнопки для удаления тестов.\nХорошего тебе дня!";
        return getSendMsgObject(superUserChatId, text, moderatorKeyboard.getStartKeyboard());
    }

    public SendMessage getMenuToEditPartner(Long chatId) {
        String text = "Можно добавить, удалить, проверить дату окончания партнерки\nЧто бы все работало корректно, " +
                "попроси партнера добавить бота в его чат/группу ";
        return getSendMsgObject(chatId, text, keyboard.getPartnersKeyboards());
    }

    public SendMessage addNewPartner(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboard.getBackToEditPartnersButton());
    }

    public SendMessage getBackToMain(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboard.getBackToMain());
    }

    public SendMessage deletePartner(Long chatId) {
        String text = "Выберите партнера, которого хотите удалить";
        return getSendMsgObject(chatId, text, keyboard.getPartnersToDelete());
    }

    public SendMessage getPartnerShipStatus(Long chatId) {
        StringBuilder builder = new StringBuilder().append("Список партнерок: \n");
        int i = 0;
        for (Partner p : mongo.getAllPartners()) {
            builder.append(++i).append(". ").append(p.getGroupUserName()).append("\n")
                    .append("   до: ").append(p.getExpiredPartnerShipTime().toString()).append("\n\n");
        }
        return getSendMsgObject(chatId, builder.toString(), keyboard.getPartnersKeyboards());
    }

    public SendMessage getUserFindPanel(Long chatId, int lastIndex) {
        String text = "Введи @username, chatId или листай списки кнопок(их максимум по 12 штук на экран).";
        if (lastIndex < 0) {
            lastIndex = 0;
        }
        return getSendMsgObject(chatId, text, keyboard.getLookAfterToAllUsers(lastIndex));
    }

    public SendMessage getButtonAndInstructionHowSendTheSpam(Long chatId) {
        String text = "\uD83D\uDEE0Проверь свое сообщенние перед отправкой\uD83D\uDEE0\n" +
                "Отправь его сейчас сюда, посмотри если все ок нажми \n\"Отправить последнее сообщение\"";
        return getSendMsgObject(chatId, text, keyboard.sendTheLastMessageToEveryOne());
    }

    public SendMessage getAudienceGrowth(Long chatId, int userInDay) {
        String text = "Статистика прироста аудитории:\n" +
                "\nдень: " + growthCalculator.getNewUsersToday() +
                "\n7 дней: " + growthCalculator.getNewUsersLastSevenDays()  +
                "\n30 дней: " + growthCalculator.getNewUsersLastThirtyDays()
                +"\nУникальных посещений c 00-00(живые юзеры): " + userInDay;

        return getSendMsgObject(chatId,text, keyboard.getBackToMain());
    }

    public SendMessage getUserHandleMenuModerator(Long chatId,Long userChatId, int index) {
        BotUser user = mongo.getBotUser(userChatId);
        String text = "Кнопки управления пользователем: \n"
                + (user.getUserName() == null ? userChatId : user.getUserName()) + "\n"
                +"юзер: " + (user.isBlock() ? "❌" : "✅");
        return getSendMsgObject(chatId, text, moderatorKeyboard.getHandleUsersKeyboards(userChatId, index));
    }

    public SendMessage getUserHandleMenu(Long chatId,Long userChatId, int index) {
        BotUser user = mongo.getBotUser(userChatId);
        String text = "Кнопки управления пользователем: \n"
                + (user.getUserName() == null ? userChatId : user.getUserName()) + "\n"
                +"юзер: " + (user.isBlock() ? "❌" : "✅");
        return getSendMsgObject(chatId, text, keyboard.getHandleUsersKeyboards(userChatId, index));
    }

    public SendMessage getUserUnblock(Long chatId, Long userChatId) {
        BotUser user = mongo.getBotUser(userChatId);
        String text = "Пользователь " + (user.getUserName() == null ? userChatId : user.getUserName())
                + " успешно разблокирован✅";
        return getSendMsgObject(chatId, text, keyboard.realGetBack(userChatId));
    }

    public SendMessage getUserBlock(Long chatId, Long userChatId) {
        BotUser user = mongo.getBotUser(userChatId);
        String text = "Пользователь " + (user.getUserName() == null ? userChatId : user.getUserName())
                + " успешно заблокирован❌";
        return getSendMsgObject(chatId, text, keyboard.realGetBack(userChatId));
    }

    public SendMessage getListTests(Long chatId, Long userChatId, int lastIndex) {
        String text = "Выберите тест, который хотите удалить:";
        return getSendMsgObject(chatId, text, keyboard.getAllUsersTest(userChatId, lastIndex));
    }
}
