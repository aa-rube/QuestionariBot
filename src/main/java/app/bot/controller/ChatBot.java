package app.bot.controller;

import app.bot.admin.AdminMessage;
import app.bot.config.BotConfig;
import app.bot.constructor.BotUserConstructor;
import app.bot.constructor.message.MessageForCreateTestConstructor;
import app.bot.constructor.message.MessageForPassTest;
import app.bot.constructor.message.MessageSubscribeConstructor;
import app.bot.constructor.buttons.NumbersButtons;
import app.bot.constructor.buttons.StringButtons;
import app.bot.model.BotUser;
import app.questionary.model.*;
import app.questionary.repository.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ChatBot extends TelegramLongPollingBot {
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private MessageForCreateTestConstructor createTestMsg;
    @Autowired
    private MessageSubscribeConstructor subscribe;
    @Autowired
    private MessageForPassTest passTest;
    @Autowired
    private AdminMessage adminMessage;
    @Autowired
    private MongoService mongo;
    @Autowired
    private BotUserConstructor botConstructor;
    private final HashSet<Long> waitForName = new HashSet<>();
    private final HashSet<Long> waitForQuestion = new HashSet<>();
    private final HashSet<Long> waitForNewOption = new HashSet<>();
    private final HashSet<Long> waitForScoreParameter = new HashSet<>();
    private final HashSet<Long> isNotCompleteCreateResult = new HashSet<>();
    private final HashSet<Long> waitForResultText = new HashSet<>();
    private final HashSet<Long> waitForTopRate = new HashSet<>();
    private final HashSet<Long> waitForLowRate = new HashSet<>();
    private final HashSet<Long> waitForMainTestPic = new HashSet<>();
    private final HashSet<Long> waitForQuestionPic = new HashSet<>();
    private final HashSet<Long> waitForResultPic = new HashSet<>();
    private final HashMap<Long, Integer> chatIdMsgId = new HashMap<>();
    private final HashMap<Long, Questioner> create = new HashMap<>();
    private final HashMap<Long, Integer> waitForEditOption = new HashMap<>();
    private final HashMap<Long, Integer> waitForEditQuestionText = new HashMap<>();
    private final HashMap<Long, Integer> waitForEditQuestionPic = new HashMap<>();
    private final HashMap<Long, Integer> waitForNewOptionForExistingQuestion = new HashMap<>();
    private final HashMap<Long, String> waitForNewOptionsParamsForOldQuestion = new HashMap<>();
    private final HashMap<Long, String> waitForNewOptionsScoreForOldQuestion = new HashMap<>();
    private final HashMap<Long, Integer> waitForNewScoreFromEditKeyBoard = new HashMap<>();
    private final HashSet<Long> addUserNameGroupPartner = new HashSet<>();
    private final HashSet<Long> addInviteLinkToPartnerGroup = new HashSet<>();
    private final HashSet<Long> addData = new HashSet<>();
    private final HashMap<Long, Partner> partner = new HashMap<>();
    private final HashMap<Long, String> userUserName = new HashMap<>();
    private final HashMap<Long, SendMessage> lastMessageToSend = new HashMap<>();
    private final HashMap<Long, Integer> lastIndexInUserList = new HashMap<>();
    private final HashSet<Long> blockedUsers = new HashSet<>();
    private final HashMap<Long, Integer> waitForNewResultText = new HashMap<>();
    private final HashMap<Long, Integer> waitForEditTopRate = new HashMap<>();
    private final HashMap<Long, Integer> waitForEditLowRate = new HashMap<>();
    private final HashMap<Long, Questioner> passTheTest = new HashMap<>();
    private final HashMap<Long, Integer> testTotalScoreMap = new HashMap<>();
    private final HashSet<Long> userInDay = new HashSet<>();
    private final HashMap<Long, SendMessage> testResultMsg = new HashMap<>();
    private final HashMap<Long, SendPhoto> testResultMsgPhoto = new HashMap<>();
    private final HashMap<Long, Integer> compareTopAndLowRates = new HashMap<>();
    private final HashMap<Long, HashSet<String>> cantStarsDouble = new HashMap<>();
    private final HashMap<Long, List<Integer>> doubleMsg = new HashMap<>();
    private final String dataPath = "data/";

    @Override
    public String getBotUsername() {
        return botConfig.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Scheduled(cron = "0 0 * * * ?")
    private void scheduleEvery24Hours() {
        for (Partner p : mongo.getAllExpiredPartners()) {
            mongo.deletePartnerByGroupUserName(p.getGroupUserName());
        }
        userInDay.clear();
        cantStarsDouble.clear();
    }

    @PostConstruct
    public void init() {
        for (BotUser u : mongo.getAllBotUsers()) {
            if (u.isBlock()) {
                blockedUsers.add(u.getChatId());
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasInlineQuery()) {
            inlineAnswer(update);
            return;
        }

        try {
            Long chatSuperUserId = update.getMessage().getChatId();
            if (chatSuperUserId == botConfig.getSuperUserChatId()) {
                superUserTextMessageHandle(update, chatSuperUserId);
                return;
            } else if (chatSuperUserId == botConfig.getModeratorChatId()) {
                moderatorText(update, chatSuperUserId);
                return;
            }
        } catch (Exception e) {
            try {
                Long chatSuperUserId = update.getCallbackQuery().getMessage().getChatId();
                if (chatSuperUserId == botConfig.getSuperUserChatId()) {
                    superUserHandleCallBackData(update, chatSuperUserId);
                    return;
                } else if (chatSuperUserId == botConfig.getModeratorChatId()) {
                    moderatorCallBackData(update, botConfig.getModeratorChatId());
                    return;
                }
            } catch (Exception ex) {
            }
        }

        try {
            if (update.hasCallbackQuery()) {
                Long chatId = update.getCallbackQuery().getMessage().getChatId();

                if (blockedUsers.contains(chatId)) {
                    executeSendMessage(createTestMsg.youWereBlocked(chatId));
                    return;
                }
                userInDay.add(chatId);
                callBackDataHandle(chatId, update);

                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (update.getMessage().isCommand()) {
                Long chatId = update.getMessage().getChatId();
                if (blockedUsers.contains(chatId)) {
                    executeSendMessage(createTestMsg.youWereBlocked(chatId));
                    return;
                }
                userInDay.add(chatId);
                commandMessageHandle(chatId, update);

                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (update.hasMessage() && update.getMessage().hasText() && !update.getMessage().hasPhoto()) {

                Long chatId = update.getMessage().getChatId();
                if (blockedUsers.contains(chatId)) {
                    executeSendMessage(createTestMsg.youWereBlocked(chatId));
                    return;
                }
                userInDay.add(chatId);
                textMessageHandle(chatId, update);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (update.getMessage().hasPhoto()) {
                Long chatId = update.getMessage().getChatId();
                if (blockedUsers.contains(chatId)) {
                    executeSendMessage(createTestMsg.youWereBlocked(chatId));
                    return;
                }
                userInDay.add(chatId);
                photoMessageHandle(chatId, update);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void inlineAnswer(Update update) {
        InlineQuery inlineQuery = update.getInlineQuery();
        String query = inlineQuery.getQuery();
        List<InlineQueryResult> results = new ArrayList<>();

        List<Questioner> questioners = mongo.getAllQuestioners();

        Comparator<Questioner> comparator = Comparator
                .comparing(Questioner::getPassCount, Comparator.reverseOrder())
                .thenComparing(Questioner::getAverageScore, Comparator.reverseOrder());

        Collections.sort(questioners, comparator);

        for (Questioner questioner : questioners) {
            if (questioner.getName().toLowerCase().contains(query.toLowerCase())) {
                InlineQueryResultArticle article = new InlineQueryResultArticle();

                article.setId(questioner.getQuestionerId());
                article.setTitle(questioner.getName());
                article.setDescription("Рейтинг " + questioner.getAverageScore() + "⭐\uFE0F "
                        + "\nПройдено раз: " + questioner.getPassCount());

                InputTextMessageContent messageContent = new InputTextMessageContent();
                messageContent.setMessageText("Давай пройдем тест \"" + questioner.getName() + "\"");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                List<InlineKeyboardButton> startRow = new ArrayList<>();
                InlineKeyboardButton start = new InlineKeyboardButton();
                start.setText("Начать тест");
                String s = "https://t.me/" + botConfig.getBotUserName() + "?start=" + "got_" + questioner.getQuestionerId();
                start.setUrl(s);

                startRow.add(start);

                keyboard.add(startRow);

                markup.setKeyboard(keyboard);

                article.setReplyMarkup(markup);

                article.setInputMessageContent(messageContent);
                results.add(article);
            }
        }

        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setResults(results);

        try {
            execute(answerInlineQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void superUserTextMessageHandle(Update update, Long chatId) {
        String text = update.getMessage().getText();
        if (text.equals("/start")) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getStartMessage(chatId));
            return;
        }
        if (text.contains("@") && !addUserNameGroupPartner.contains(chatId)) {
            deleteMessage(chatId);
            BotUser user = mongo.getBotUserByUserName(text.trim());
            if (user != null) {
                executeSendMessage(adminMessage.getUserHandleMenu(chatId, user.getChatId(), 0));
                return;
            }

            String[] splitData = text.split("@");
            if (splitData.length > 1) {
                BotUser userByChatId = mongo.getBotUser(Long.valueOf(splitData[1]));
                if (userByChatId != null) {
                    executeSendMessage(adminMessage.getUserHandleMenu(chatId, userByChatId.getChatId(), 0));
                    return;
                }
            }

            executeSendMessage(adminMessage.addNewPartner(chatId, "Не смог найти пользователя " + text
                    + "\nПроверьте имя/chatId и повторите\n Формат: \n@username\n@123455769708"));

        }

        if (addUserNameGroupPartner.contains(chatId)) {
            deleteMessage(chatId);
            if (text.contains("@")) {
                Partner p = new Partner();
                p.setGroupUserName(text.trim());
                partner.put(chatId, p);
                removeAdminWaitLists(chatId);
                addInviteLinkToPartnerGroup.add(chatId);
                executeSendMessage(adminMessage.addNewPartner(chatId, "Теперь ссылку:"));
            } else {
                executeSendMessage(adminMessage.addNewPartner(chatId, "Кажется ты ввел что-то другое." + "\nФормат:@userNameGroup"));
            }
            return;
        }

        if (addInviteLinkToPartnerGroup.contains(chatId)) {
            deleteMessage(chatId);
            if (text.contains("https://t.me")) {
                partner.get(chatId).setGroupUrl(text.trim());
                removeAdminWaitLists(chatId);
                addData.add(chatId);
                executeSendMessage(adminMessage.addNewPartner(chatId, "Теперь дату, когда удалить партнера в формате 01.01.2024"));
            } else {
                executeSendMessage(adminMessage.addNewPartner(chatId, "Кажется ты ввел что-то другое." + "\nФормат:https://t.me/medu8zalive"));
            }
            return;
        }

        if (addData.contains(chatId)) {
            deleteMessage(chatId);
            try {
                deleteAllUsersFile(chatId);
                removeAdminWaitLists(chatId);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate expiredDate = LocalDate.parse(text, formatter);
                partner.get(chatId).setExpiredPartnerShipTime(expiredDate);
                mongo.savePartner(partner.get(chatId));
                executeSendMessage(adminMessage.addNewPartner(chatId, "Партнер " + partner.get(chatId).getGroupUserName() + " успешно сохранен до " + partner.get(chatId).getExpiredPartnerShipTime().toString()));
                executeSendMessage(adminMessage.getStartMessage(chatId));
                partner.clear();
                return;
            } catch (Exception e) {
                executeSendMessage(adminMessage.addNewPartner(chatId, "Кажется, что-то пошло не так." + "\nПопробуй формат: 01.01.2024"));


                return;
            }
        }

        if (lastMessageToSend.containsKey(chatId)) {
            deleteMessage(chatId);
            SendMessage spam = new SendMessage();
            spam.setText(update.getMessage().getText());
            spam.setEntities(update.getMessage().getEntities());
            lastMessageToSend.put(chatId, spam);
            executeSendMessage(adminMessage.getButtonAndInstructionHowSendTheSpam(chatId));

        }
    }

    private void superUserHandleCallBackData(Update update, Long chatId) {
        try {
            superUserCallBackDataNumberHandle(chatId, Integer.parseInt(update.getCallbackQuery().getData()));
        } catch (Exception e) {
            superUserCallBacDataTextHandle(chatId, update);


        }
        return;
    }

    private void superUserCallBacDataTextHandle(Long chatId, Update update) {
        String data = update.getCallbackQuery().getData();

        if (data.contains("delete_@")) {
            try {
                String[] splitData = data.split("_");
                mongo.deletePartnerByGroupUserName(splitData[1]);
                deleteMessage(chatId);
                executeSendMessage(adminMessage.getMenuToEditPartner(chatId));
            } catch (Exception e) {
                executeSendMessage(adminMessage.addNewPartner(chatId, "Кажется, что-то пошло не так."));
                executeSendMessage(adminMessage.getMenuToEditPartner(chatId));


            }
            return;
        }

        if (data.contains("getUsers_")) {
            deleteMessage(chatId);
            int index = Integer.parseInt(data.split("_")[1]);
            lastIndexInUserList.put(chatId, index);
            executeSendMessage(adminMessage.getUserFindPanel(chatId, index));
            return;
        }

        if (data.contains("user_")) {
            deleteMessage(chatId);
            Long userChatId = Long.valueOf(data.split("_")[1]);
            executeSendMessage(adminMessage.getUserHandleMenu(chatId, userChatId, lastIndexInUserList.get(chatId)));
            return;
        }

        if (data.contains("block_")) {
            deleteMessage(chatId);
            Long userChatId = Long.valueOf(data.split("_")[1]);
            blockedUsers.add(userChatId);
            BotUser u = mongo.getBotUser(userChatId);
            mongo.deleteUserByChatId(userChatId);

            u.setBlock(true);
            mongo.saveBotUser(u);
            executeSendMessage(adminMessage.getUserBlock(chatId, userChatId));
            return;
        }

        if (data.contains("acces_")) {
            deleteMessage(chatId);
            Long userChatId = Long.valueOf(data.split("_")[1]);
            blockedUsers.remove(userChatId);
            BotUser u = mongo.getBotUser(userChatId);
            mongo.deleteUserByChatId(userChatId);
            u.setBlock(false);
            mongo.saveBotUser(u);
            executeSendMessage(adminMessage.getUserUnblock(chatId, userChatId));
            return;
        }

        if (data.contains("getAllTests_")) {
            deleteMessage(chatId);
            String[] splitData = data.split("_");
            Long userChatId = Long.valueOf(splitData[1]);
            int index = Integer.parseInt(splitData[2]);
            executeSendMessage(adminMessage.getListTests(chatId, userChatId, index));
            return;
        }

        if (data.contains("dellTest_")) {
            deleteMessage(chatId);
            String splitData = data.split("_")[1];
            Long l = mongo.getQuestionerByQuestionerId(splitData).getChatId();
            mongo.deleteQuestionerByQuestionerId(splitData);
            executeSendMessage(adminMessage.getListTests(chatId, l, 0));
        }

    }

    private void superUserCallBackDataNumberHandle(Long chatId, int number) {

        if (number == 99) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getStartMessage(chatId));
        }
        if (number == 0) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getMenuToEditPartner(chatId));
        }

        if (number == 1) {
            deleteMessage(chatId);
            try {
                int y = lastIndexInUserList.get(chatId);
            } catch (Exception e) {
                lastIndexInUserList.put(chatId, 0);
            }

            executeSendMessage(adminMessage.getUserFindPanel(chatId, 0));
        }

        if (number == 2) {
            deleteMessage(chatId);
            addUserNameGroupPartner.add(chatId);
            executeSendMessage(adminMessage.addNewPartner(chatId, "Введи @userNameGroup"));
        }

        if (number == 3) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.deletePartner(chatId));
        }


        if (number == 4) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getPartnerShipStatus(chatId));
        }

        if (number == 5) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getAudienceGrowth(chatId, userInDay.size()));
        }

        if (number == 6) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getButtonAndInstructionHowSendTheSpam(chatId));
            lastMessageToSend.put(chatId, null);
        }

        if (number == 7 && lastMessageToSend.containsKey(chatId)) {
            deleteMessage(chatId);
            int i = 0;
            try {
                SendMessage m = lastMessageToSend.get(chatId);
                for (BotUser u : mongo.getAllBotUsers()) {
                    m.setChatId(u.getChatId());
                    executeSendMessage(m);
                    i++;
                }
                removeAdminWaitLists(chatId);
                executeSendMessage(adminMessage.getBackToMain(chatId, "Всего отправлено: " + i));
            } catch (Exception e) {
                executeSendMessage(adminMessage.getBackToMain(chatId, "Всего отправлено: " + i));


            }
            return;

        }

    }

    private void moderatorText(Update update, Long chatId) {
        String text = update.getMessage().getText();

        if (text.equals("/start")) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getStartModeratorMessage(chatId));
            return;
        }
    }

    private void moderatorCallBackData(Update update, Long chatId) {
        try {
            moderatorCallBackDataNumber(chatId, Integer.parseInt(update.getCallbackQuery().getData()));
        } catch (Exception e) {
            moderatorCallBackDataText(update, chatId);


        }
        return;
    }

    private void moderatorCallBackDataText(Update update, Long chatId) {
        String data = update.getCallbackQuery().getData();

        if (data.contains("delete_@")) {
            try {
                String[] splitData = data.split("_");
                mongo.deletePartnerByGroupUserName(splitData[1]);
                deleteMessage(chatId);
                executeSendMessage(adminMessage.getMenuToEditPartner(chatId));
            } catch (Exception e) {
                executeSendMessage(adminMessage.addNewPartner(chatId, "Кажется, что-то пошло не так."));
                executeSendMessage(adminMessage.getMenuToEditPartner(chatId));


            }
            return;
        }

        if (data.contains("getUsers_")) {
            deleteMessage(chatId);
            int index = Integer.parseInt(data.split("_")[1]);
            lastIndexInUserList.put(chatId, index);
            executeSendMessage(adminMessage.getUserFindPanel(chatId, index));
            return;
        }

        if (data.contains("user_")) {
            deleteMessage(chatId);
            Long userChatId = Long.valueOf(data.split("_")[1]);
            executeSendMessage(adminMessage.getUserHandleMenuModerator(chatId, userChatId, lastIndexInUserList.get(chatId)));
            return;
        }


        if (data.contains("getAllTests_")) {
            deleteMessage(chatId);
            String[] splitData = data.split("_");
            Long userChatId = Long.valueOf(splitData[1]);
            int index = Integer.parseInt(splitData[2]);
            executeSendMessage(adminMessage.getListTests(chatId, userChatId, index));
            return;
        }

        if (data.contains("dellTest_")) {
            deleteMessage(chatId);
            String splitData = data.split("_")[1];
            Long l = mongo.getQuestionerByQuestionerId(splitData).getChatId();
            mongo.deleteQuestionerByQuestionerId(splitData);
            executeSendMessage(adminMessage.getListTests(chatId, l, 0));
        }
    }

    private void moderatorCallBackDataNumber(Long chatId, int number) {
        if (number == 99) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getStartModeratorMessage(chatId));
        }
        if (number == 0) {
            deleteMessage(chatId);
            executeSendMessage(adminMessage.getMenuToEditPartner(chatId));
        }

        if (number == 1) {
            deleteMessage(chatId);
            try {
                int y = lastIndexInUserList.get(chatId);
            } catch (Exception e) {
                lastIndexInUserList.put(chatId, 0);


            }

            executeSendMessage(adminMessage.getUserFindPanel(chatId, 0));
        }

    }

    private void removeAdminWaitLists(Long chatId) {
        addUserNameGroupPartner.remove(chatId);
        addInviteLinkToPartnerGroup.remove(chatId);
        addData.remove(chatId);
        lastMessageToSend.remove(chatId);
    }

    private void removeFromAllWaitingLists(Long chatId) {
        waitForName.remove(chatId);
        waitForQuestion.remove(chatId);
        waitForNewOption.remove(chatId);
        waitForScoreParameter.remove(chatId);
        waitForResultText.remove(chatId);
        waitForTopRate.remove(chatId);
        waitForLowRate.remove(chatId);
        waitForMainTestPic.remove(chatId);
        waitForQuestionPic.remove(chatId);
        waitForResultPic.remove(chatId);
        waitForEditOption.remove(chatId);
        waitForEditQuestionPic.remove(chatId);
        waitForNewOptionForExistingQuestion.remove(chatId);
        waitForNewOptionsParamsForOldQuestion.remove(chatId);
        waitForNewOptionsScoreForOldQuestion.remove(chatId);
        waitForNewScoreFromEditKeyBoard.remove(chatId);
        waitForNewResultText.remove(chatId);
        waitForEditTopRate.remove(chatId);
        waitForEditLowRate.remove(chatId);
        waitForEditQuestionText.remove(chatId);
        compareTopAndLowRates.remove(chatId);
    }

    private void commandMessageHandle(Long chatId, Update update) {
        String command = update.getMessage().getText();

        if (command.contains("/start got_")) {
            deleteMessage(chatId);
            Questioner questioner = mongo.getQuestionerByQuestionerId(command.split("_")[1]);
            passTheTest.put(chatId, questioner);
            testTotalScoreMap.put(chatId, 0);
            sendTextOrPhotoQuestion(chatId, questioner, 0);
        }


        if (chatId == botConfig.getSuperUserChatId()) {

            executeSendMessage(adminMessage.getStartMessage(chatId));
            return;
        }

        if (chatId == botConfig.getModeratorChatId()) {
            executeSendMessage(adminMessage.getStartModeratorMessage(chatId));
            return;
        }

        if (command.equals("/start")) {
            userUserName.put(chatId, "@" + update.getMessage().getFrom().getUserName());
            try {
                BotUser botUser = mongo.getBotUser(chatId);
                if (botUser == null) {
                    executeSendMessage(subscribe.getSubscribeOffer(chatId, update));
                    return;
                }
            } catch (Exception e) {

            }
            BotUser botUser = mongo.getBotUser(chatId);
            if (botUser.isSubscribe()) {
                executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
                return;
            }
            executeSendMessage(subscribe.getSubscribeOffer(chatId, update));
            return;
        }

        if (command.contains(StringButtons.EDIT_QUESTION.getValue())) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            int editeIndexElement = Integer.parseInt(command.trim().split("_")[1]);
            waitForEditQuestionText.put(chatId, editeIndexElement);
            sendTestPhotoOrRegularTextWhenYouEditQuestion(chatId, editeIndexElement);
            return;
        }

        if (command.contains(StringButtons.EDIT_OPTION.getValue())) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            executeSendMessage(createTestMsg.getUpdateOptionsParametersByUserIndex(chatId, create.get(chatId), command.trim()));
            return;
        }

        if (command.contains(StringButtons.EDIT_RESULT_BY_COMMAND.getValue())) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            int element = Integer.parseInt(command.split("_")[1]);
            sendRegularTextOrPhotoMessage(chatId, element);
        }
    }

    private void sendRegularTextOrPhotoMessage(Long chatId, int element) {
        if (create.get(chatId).getResults().get(element).getPic() != null) {
            executeSendPhoto(createTestMsg.getMenuToEditResultByUserIndexWithPhoto(chatId, create.get(chatId), element));
        } else {
            executeSendMessage(createTestMsg.getMenuToEditResultByUserIndex(chatId, create.get(chatId), element));
        }
        return;
    }

    private void callBackDataHandle(Long chatId, Update update) {
        String data = update.getCallbackQuery().getData();
        try {
            callBackNumberHandle(chatId, Integer.parseInt(data.trim()));
        } catch (Exception e) {
            callBackDataTextHandle(update, chatId, data);
        }
    }

    private void callBackNumberHandle(Long chatId, Integer number) {
        if (number == NumbersButtons.HELP.getValue()) {
            deleteMessage(chatId);
            executeSendMessage(createTestMsg.getHelpMsg(chatId));
        }

        if (number == NumbersButtons.MY_TESTS.getValue()) {
            deleteMessage(chatId);
            sendTheListOfTest(chatId);
            return;
        }

        if (number == NumbersButtons.SAVE.getValue()) {
            deleteMessage(chatId);
            Questioner q = create.get(chatId);

            try {
                for (Question qu : q.getQuestions()) {
                    try {
                        if (qu.getOptions().isEmpty()) {
                            executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId,
                                    "Вы не добавили ни одного ответа на вопрос " + qu.getQuestionText()));
                        }
                    } catch (Exception e) {
                        executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId,
                                "Вы не добавили ни одного ответа на вопрос " + qu.getQuestionText()));

                    }
                }
            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId,
                        "Вы не добавили ни одного вопроса"));
                return;
            }

            try {
                if (q.getResults().isEmpty()) {
                    executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId,
                            "Вы не добавили ни одного результата"));
                    return;
                }

                for (Result r : q.getResults()) {
                    if (r.getTopRate() < r.getLowRate()) {
                        executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId,
                                "Исправьте результат " + r.getResultText()
                                        + ". В этом рехультате верхняя граница меньше нижней"));
                        return;
                    }
                }
            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId,
                        "Вы не добавили ни одного результата"));
                return;
            }


            if (q.getName().equals("null") || q.getName().isEmpty()) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId,
                        "Вы не введи имя для Вашего теста"));
                return;
            }

            mongo.deleteQuestionerByQuestionerId(q.getQuestionerId());
            q.setChatId(chatId);
            mongo.saveQuestioner(q);
            create.remove(chatId);
            removeFromAllWaitingLists(chatId);
            //deleteAllUsersFile(chatId);
            executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
            return;
        }

        if (number == NumbersButtons.PASS_THE_TEST.getValue()) {
            deleteMessage(chatId);
            executeSendMessage(createTestMsg.getPassTestMessage(chatId));
            return;
        }

        if (number == NumbersButtons.CREATE_TEST.getValue()) {
            deleteMessage(chatId);
            Questioner questioner = new Questioner();
            questioner.setChatId(chatId);
            questioner.setQuestionerId(String.valueOf(System.currentTimeMillis()));
            create.put(chatId, questioner);
            executeSendMessage(createTestMsg.getMainCreateQuestionMenuSendMessage(chatId, create.get(chatId)));
            return;
        }

        if (number == NumbersButtons.BACK_TO_START_MENU.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            deleteAllUsersFile(chatId);
            create.remove(chatId);
            executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
            return;
        }

        if (number == NumbersButtons.BACK_TO_CREATE_MAIN.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            sendTestPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.REFRESH_MAIN_CREATE_TEST_MENU.getValue()) {
            deleteMessage(chatId);

            sendTestPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.ADD_NAME.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            waitForName.add(chatId);
            executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId, "Введите имя для вашего теста:"));
            return;
        }

        if (number == NumbersButtons.ADD_QUESTION.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            waitForQuestion.add(chatId);

            executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId, "Введите вопрос:"));
            return;
        }

        if (number == NumbersButtons.LIST_QUESTIONS.getValue()) {
            deleteMessage(chatId);
            sendQuestionPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.REFRESH_CREATE_QUESTION.getValue()) {
            deleteMessage(chatId);
            sendQuestionPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.DELETE_QUESTION.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            int lastIndexQuestion = create.get(chatId).getQuestionsListLastElementIndex();
            String q = create.get(chatId).getQuestions().get(lastIndexQuestion).getQuestionText();
            create.get(chatId).getQuestions().remove(lastIndexQuestion);
            executeSendMessage(createTestMsg.getChangeParamMsgEmptyKeyboard(chatId, "Вопрос \"" + q + "\" удален"));
            sendTestPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.ADD_OPTION.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            waitForNewOption.add(chatId);
            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId, "Введите вариант ответа:"));
            return;
        }

        if (number == NumbersButtons.GET_BACK_TO_CREATE_QUESTION.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            sendQuestionPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.REFRESH_OPTIONS_PARAMETER.getValue()) {
            deleteMessage(chatId);
            int lastIndexQuestion = create.get(chatId).getQuestionsListLastElementIndex();
            int lastIndexOption = create.get(chatId).getQuestions().get(lastIndexQuestion).getOptionsLastElementIndex();
            executeSendMessage(createTestMsg.getUpdateOptionsParameters(chatId, create.get(chatId), lastIndexQuestion, lastIndexOption));
            return;
        }

        if (number == NumbersButtons.DELETE_OPTION.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            int lastIndexQuestion = create.get(chatId).getQuestionsListLastElementIndex();
            int lastIndexOptions = create.get(chatId).getQuestions().get(lastIndexQuestion).getOptionsLastElementIndex();
            create.get(chatId).getQuestions().get(lastIndexQuestion).getOptions().remove(lastIndexOptions);
            executeSendMessage(createTestMsg.getListQuestionOptions(chatId, create.get(chatId)));
            return;
        }

        if (number == NumbersButtons.LIST_OPTION.getValue()) {
            deleteMessage(chatId);
            sendQuestionPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.MENU_ADD_RESULT.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            sendResultPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.CREATE_NEW_RESULT_FOR_TEST.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            waitForResultText.add(chatId);

            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateResultOption(chatId, "Введите текст результата:"));
            return;
        }

        if (number == NumbersButtons.GET_BACK_TO_CREATE_QUESTION_PARAMETERS.getValue()) {
            deleteMessage(chatId);

            if (isNotCompleteCreateResult.contains(chatId)) {
                int i = create.get(chatId).getResultListLastElementNumber();
                create.get(chatId).getResults().remove(i);
            }

            removeFromAllWaitingLists(chatId);
            sendResultPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.DELETE_RESULT.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            int i = create.get(chatId).getResultListLastElementNumber();
            create.get(chatId).getResults().remove(i);
            sendResultPhotoOrRegularText(chatId);
            return;
        }

        if (number == NumbersButtons.CHANGE_MAIN_TEST_PIC.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            waitForMainTestPic.add(chatId);

            executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId, "Отправьте картинку, которую хотите использовать как главную для теста " + create.get(chatId).getName()));
            return;
        }

        if (number == NumbersButtons.ADD_QUESTION_PIC.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            waitForQuestionPic.add(chatId);

            int i = create.get(chatId).getQuestionsListLastElementIndex();
            executeSendMessage(createTestMsg.getBackToMainCreateQuestionParametersAfterNoAddPic(chatId, "Отправьте картинку, которую хотите использовать для вопроса \"" + create.get(chatId).getQuestions().get(i).getQuestionText() + "\" :"));
            return;
        }

        if (number == NumbersButtons.ADD_RES_PIC.getValue()) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            waitForResultPic.add(chatId);
            int lastIndex = create.get(chatId).getResultListLastElementNumber();
            executeSendMessage(createTestMsg.getBackFromAddPicToResult(chatId, "Отправьте картинку, которую хотите использовать для результата: \"" + create.get(chatId).getResults().get(lastIndex).getResultText() + "\""));
            return;
        }
    }

    private void callBackDataTextHandle(Update update, Long chatId, String data) {
        if (data.equals("/check")) {
            int subscribe = 0;
            List<Partner> partner = mongo.getAllPartners();
            if (!partner.isEmpty()) {
                for (Partner p : partner) {
                    if (checkChannelSubscription(chatId, p.getGroupUserName())) {
                        subscribe++;
                    }
                }

                if (subscribe == mongo.getAllPartners().size()) {
                    deleteMessage(chatId);
                    executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
                    mongo.saveBotUser(botConstructor.getBotUserObject(chatId, update, userUserName.get(chatId)));
                }
                return;
            }
            deleteMessage(chatId);
            BotUser user = botConstructor.getBotUserObject(chatId, update, userUserName.get(chatId));
            mongo.saveBotUser(user);
            executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
            userUserName.remove(chatId);
            return;
        }

        if (data.contains("removeT_")) {
            deleteMessage(chatId);
            String questionerId = data.split("_")[1];
            deleteUsersFileWhenDeleteQuestioner(mongo.getQuestionerByQuestionerId(questionerId));

            mongo.deleteQuestionerByQuestionerId(questionerId);
            sendTheListOfTest(chatId);
            return;
        }

        if (data.contains("star_")) {
            deleteMessage(chatId);
            create.put(chatId, mongo.getQuestionerByQuestionerId(data.split("_")[1]));
            executeSendMessage(createTestMsg.getMainCreateQuestionMenuSendMessage(chatId, create.get(chatId)));
            return;

        }

        if (data.contains(StringButtons.DELETE_OPTION_AND_SCORE_BY_USER_INDEX.getValue())) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            create.get(chatId).getQuestions().get(Integer.parseInt(data.split("_")[1])).getOptions().remove(Integer.parseInt(data.split("_")[2]));

            executeSendMessage(createTestMsg.getUpdateOptionsParametersByUserIndex(chatId, create.get(chatId), data));
            return;
        }

        if (data.contains(StringButtons.EDIT_SCORE_OPTION_BY_USER_INDEX.getValue())) {
            deleteMessage(chatId);
            executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId, "Введите новое число баллов для ответа: \"" + create.get(chatId).getQuestions().get(Integer.parseInt(data.split("_")[1])).getOptions().get(Integer.parseInt(data.split("_")[2])).getOptionText() + "\""));
            removeFromAllWaitingLists(chatId);
            waitForNewOptionsScoreForOldQuestion.put(chatId, data);
            return;
        }

        if (data.contains(StringButtons.EDIT_OPTION_TEXT_BY_USER_INDEX.getValue())) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            waitForNewOptionsParamsForOldQuestion.put(chatId, data);
            executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId, "Введите новый текст ответа:"));
            return;
        }

        if (data.contains(StringButtons.ADD_OPTION_TO_QUESTION_BY_USER_INDEX.getValue())) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            int index = Integer.parseInt(data.trim().split("_")[1]);
            waitForNewOptionForExistingQuestion.put(chatId, index);

            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId, "Введите вариант ответа:"));
            return;
        }

        if (data.contains(StringButtons.ADD_PIC_TO_QUESTION_BY_USER_INDEX.getValue())) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            int index = Integer.parseInt(data.trim().split("_")[1]);
            waitForEditQuestionPic.put(chatId, index);
            executeSendMessage(createTestMsg.getBackToMainCreateQuestionParametersAfterNoAddPic(chatId, "Отправьте картинку, которую хотите использовать для вопроса \"" + create.get(chatId).getQuestions().get(index).getQuestionText() + "\" :"));
            return;
        }

        if (data.contains(StringButtons.DELETE_QUESTION_BY_USER_INDEX.getValue())) {
            deleteMessage(chatId);

            int index = Integer.parseInt(data.trim().split("_")[1]);
            create.get(chatId).getQuestions().remove(index);
            executeSendMessage(createTestMsg.getCreateNewQuestionParametersSendMessage(chatId, create.get(chatId)));
            return;
        }

        if (data.contains(StringButtons.EDIT_QUESTION_TEXT_BY_USER_INDEX.getValue())) {
            deleteMessage(chatId);
            int i = Integer.parseInt(data.split("_")[1]);
            waitForEditQuestionText.put(chatId, i);
            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId, "Введите новый текст вопроса:"));
            return;
        }

        if (data.contains("goRefactRes_")) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            int index = Integer.parseInt(data.trim().split("_")[1]);
            waitForEditOption.put(chatId, index);
            executeSendMessage(createTestMsg.getChangeParamAndBackToMainMenuCreateQuestion(chatId, "Название теста: " + create.get(chatId).getName() + "\n" + "Текст результата: " + create.get(chatId).getResults().get(index).getResultText() + "\nВведите новый текст:"));
            return;
        }

        if (data.contains("resultRemove_")) {
            deleteMessage(chatId);
            removeAdminWaitLists(chatId);
            int i = Integer.parseInt(data.split("_")[1]);
            create.get(chatId).getResults().remove(i);

            sendResultPhotoOrRegularText(chatId);
            return;
        }

        if (data.contains("/changeRes_")) {
            deleteMessage(chatId);
            removeAdminWaitLists(chatId);
            int i = Integer.parseInt(data.split("_")[1]);
            waitForEditTopRate.put(chatId, i);
            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                    "Введите верхнюю границу баллов за результат:\n" + create.get(chatId).getResults().get(i).getResultText()));
            return;
        }

        if (data.contains("/resulPictEdit_")) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);
            int i = Integer.parseInt(data.split("_")[1]);
            waitForNewResultPic.put(chatId, i);
            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                    "Отправьте новое фото для результата: \n" + create.get(chatId).getResults().get(i).getResultText()));
        }

        passTheTestHandle(chatId, data);
    }

    private final HashMap<Long, Integer> waitForNewResultPic = new HashMap<>();

    private void textMessageHandle(Long chatId, Update update) {
        String text = update.getMessage().getText();

        if (waitForName.contains(chatId)) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            create.get(chatId).setName(text.trim().toLowerCase());
            sendTestPhotoOrRegularText(chatId);
            return;
        }

        if (waitForQuestion.contains(chatId)) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            if (create.get(chatId).getQuestions().size() == 30) {
                sendTestPhotoOrRegularText(chatId);
                return;
            }

            create.get(chatId).getQuestions().add(new Question(text.trim()));
            sendQuestionPhotoOrRegularText(chatId);

            return;
        }

        if (waitForNewOption.contains(chatId)) {
            deleteMessage(chatId);
            removeFromAllWaitingLists(chatId);

            int lastIndexQuestion = create.get(chatId).getQuestionsListLastElementIndex();
            create.get(chatId).getQuestions().get(lastIndexQuestion).getOptions().add(new Option(text.trim()));
            waitForScoreParameter.add(chatId);
            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId, "Введите число баллов за ответ:"));
            return;
        }

        if (waitForScoreParameter.contains(chatId)) {
            deleteMessage(chatId);

            try {
                removeFromAllWaitingLists(chatId);
                int lastIndexQuestion = create.get(chatId).getQuestionsListLastElementIndex();
                int lastIndexOption = create.get(chatId).getQuestions().get(lastIndexQuestion).getOptionsLastElementIndex();

                create.get(chatId).getQuestions().get(lastIndexQuestion).getOptions().get(lastIndexOption).setScore(Integer.valueOf(text.trim()));

                sendQuestionPhotoOrRegularText(chatId);
            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                        "Вы ввели что-то кроме цифры. Повторите попытку.\n" + "Введите число баллов за ответ:"));


            }
            return;
        }

        if (waitForResultText.contains(chatId)) {
            deleteMessage(chatId);

            if (text.length() <= 1000) {
                removeFromAllWaitingLists(chatId);

                isNotCompleteCreateResult.add(chatId);

                create.get(chatId).getResults().add(new Result(text.trim()));
                waitForTopRate.add(chatId);
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateResultOption(chatId,
                        "Введите верхнюю границу для результата в виде числа:"));
                return;
            }

            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateResultOption(chatId,
                    "Похоже, вы ввели более 1000 символов. Попробуйте укоротить ваш текст"));
            return;
        }


        if (waitForTopRate.contains(chatId)) {
            deleteMessage(chatId);

            try {
                waitForLowRate.add(chatId);
                int topRate = Integer.valueOf(text.trim());
                int index = create.get(chatId).getResultListLastElementNumber();

                compareTopAndLowRates.put(chatId, topRate);

                create.get(chatId).getResults().get(index).setTopRate(topRate);
                waitForTopRate.remove(chatId);
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateResultOption(chatId,
                        "Введите нижнюю границу для результата в виде числа:"));
                return;
            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateResultOption(chatId,
                        "Вы ввели что-то кроме цифры. Повторите попытку.\nВведите число баллов за ответ:"));
            }
            return;
        }


        if (waitForLowRate.contains(chatId)) {
            deleteMessage(chatId);
            try {
                int lowRate = Integer.parseInt(text.trim());

                if (compareTopAndLowRates.get(chatId) < lowRate) {
                    executeSendMessage(createTestMsg.getChangeParamAndBackToCreateResultOption(chatId,
                            "Верхняя граница должна быть выше нижней. \nВведите нижнюю границу баллов за ответ:"));
                    return;
                }
                waitForLowRate.remove(chatId);

                int index = create.get(chatId).getResultListLastElementNumber();
                create.get(chatId).getResults().get(index).setLowRate(lowRate);

                isNotCompleteCreateResult.remove(chatId);
                sendResultPhotoOrRegularText(chatId);
                return;
            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateResultOption(chatId,
                        "Вы ввели что-то кроме цифры. Повторите попытку.\nВведите число баллов за ответ:"));
                e.printStackTrace();
            }
            return;
        }

        if (waitForEditOption.containsKey(chatId)) {
            deleteMessage(chatId);
            create.get(chatId).getResults().get(waitForEditOption.get(chatId)).setResultText(text.trim());
            sendRegularTextOrPhotoMessage(chatId, waitForEditOption.get(chatId));
            removeFromAllWaitingLists(chatId);
            return;
        }

        if (waitForNewOptionForExistingQuestion.containsKey(chatId)) {
            deleteMessage(chatId);
            int index = waitForNewOptionForExistingQuestion.get(chatId);
            removeFromAllWaitingLists(chatId);

            create.get(chatId).getQuestions().get(index).getOptions().add(new Option(text.trim()));

            waitForNewScoreFromEditKeyBoard.put(chatId, index);
            executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                    "Введите число баллов за ответ: \"" + text + "\""));
            return;
        }

        if (waitForNewOptionsParamsForOldQuestion.containsKey(chatId)) {
            deleteMessage(chatId);
            try {
                String[] userData = waitForNewOptionsParamsForOldQuestion.get(chatId).split("_");

                create.get(chatId).getQuestions().get(Integer.parseInt(userData[1]))
                        .getOptions().get(Integer.parseInt(userData[2])).setOptionText(text.trim());

            } catch (Exception e) {


            }

            executeSendMessage(createTestMsg.getUpdateOptionsParametersByUserIndex(chatId,
                    create.get(chatId), waitForNewOptionsParamsForOldQuestion.get(chatId)));

            removeFromAllWaitingLists(chatId);
            return;
        }

        if (waitForNewOptionsScoreForOldQuestion.containsKey(chatId)) {
            deleteMessage(chatId);

            try {
                String[] userData = waitForNewOptionsScoreForOldQuestion.get(chatId).split("_");
                create.get(chatId).getQuestions().get(Integer.parseInt(userData[1])).getOptions()
                        .get(Integer.parseInt(userData[2])).setScore(Integer.valueOf(text));

                executeSendMessage(createTestMsg.getUpdateOptionsParametersByUserIndex(chatId,
                        create.get(chatId), waitForNewOptionsScoreForOldQuestion.get(chatId)));

                removeFromAllWaitingLists(chatId);
            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                        "Вы ввели что-то кроме цифр. Повторите попытку.\n" + "Введите число баллов за ответ:"));


            }
            return;
        }

        if (waitForNewScoreFromEditKeyBoard.containsKey(chatId)) {
            deleteMessage(chatId);
            System.out.println("waitForNewScoreFromEditKeyBoard.get(chatId): " + waitForNewScoreFromEditKeyBoard.get(chatId));
            System.out.println("text: " + text);
            try {
                int score = Integer.parseInt(text.trim());
                int questionIndex = waitForNewScoreFromEditKeyBoard.get(chatId);
                int lastOptElement = create.get(chatId).getQuestions().get(questionIndex).getOptionsLastElementIndex();

                create.get(chatId).getQuestions().get(questionIndex).getOptions().get(lastOptElement).setScore(score);

                executeSendMessage(createTestMsg.getEditeQuestionByUserElement(chatId, create.get(chatId), questionIndex));
                removeFromAllWaitingLists(chatId);


            } catch (Exception e) {
                int questionIndex = waitForNewScoreFromEditKeyBoard.get(chatId);
                int lastOptElement = create.get(chatId).getQuestions().get(questionIndex).getOptionsLastElementIndex();
                String answer = create.get(chatId).getQuestions().get(questionIndex).getOptions().get(lastOptElement).getOptionText();
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                        "Вы ввели что-то кроме цифр. Повторите попытку." +
                                "\nВведите число баллов за ответ: " + answer));
                e.printStackTrace();

            }
            return;
        }

        if (waitForNewResultText.containsKey(chatId)) {
            deleteMessage(chatId);
            int element = waitForNewResultText.get(chatId);
            removeFromAllWaitingLists(chatId);

            create.get(chatId).getResults().get(element).setResultText(text.trim());
            sendRegularTextOrPhotoMessage(chatId, element);

            return;
        }

        if (waitForEditTopRate.containsKey(chatId)) {
            deleteMessage(chatId);
            try {
                int score = Integer.parseInt(text.trim());
                int i = waitForEditTopRate.get(chatId);

                create.get(chatId).getResults().get(i).setTopRate(score);
                removeAdminWaitLists(chatId);
                waitForEditLowRate.put(chatId, i);
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                        "Введите нижнюю границу баллов за результат:" + create.get(chatId).getResults().get(i)));
            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                        "Кажется вы ввели что-то кроме цифр.\nВведите верхнюю границу баллов за результат:"));

            }
            return;
        }

        if (waitForEditLowRate.containsKey(chatId)) {
            deleteMessage(chatId);
            try {
                int score = Integer.parseInt(text.trim());
                int index = waitForEditLowRate.get(chatId);

                create.get(chatId).getResults().get(index).setLowRate(score);
                sendRegularTextOrPhotoMessage(chatId, index);
                removeAdminWaitLists(chatId);

            } catch (Exception e) {
                executeSendMessage(createTestMsg.getChangeParamAndBackToCreateQuestionParameters(chatId,
                        "Кажется вы ввели что-то кроме цифр.\nВведите нижнюю границу баллов за результат:"));
            }
            return;
        }

        if (waitForEditQuestionText.containsKey(chatId)) {
            deleteMessage(chatId);
            create.get(chatId).getQuestions().get(waitForEditQuestionText.get(chatId)).setQuestionText(text.trim());
            sendTestPhotoOrRegularTextWhenYouEditQuestion(chatId, waitForEditQuestionText.get(chatId));
            removeFromAllWaitingLists(chatId);
            return;
        }
    }

    private void passTheTestHandle(Long chatId, String data) {
        if (data.contains("/passTheTest")) {
            deleteMessage(chatId);
            Questioner questioner = mongo.getQuestionerByQuestionerId(data.split("_")[1]);
            passTheTest.put(chatId, questioner);
            testTotalScoreMap.put(chatId, 0);
            sendTextOrPhotoQuestion(chatId, questioner, 0);
            return;
        }

        if (data.contains("/goThrough_") && passTheTest.containsKey(chatId)) {
            deleteMessage(chatId);

            Questioner questioner = passTheTest.get(chatId);
            String[] splitData = data.split("_");

            int index = Integer.parseInt(splitData[2]) + 1;
            int totalScore = testTotalScoreMap.get(chatId) + Integer.parseInt(splitData[1]);
            int lastIndex = questioner.getQuestionsListLastElementIndex();

            if (index > lastIndex) {
                for (Result result : passTheTest.get(chatId).getResults()) {
                    if (result.getLowRate() <= totalScore && totalScore <= result.getTopRate()) {

                        try {
                            testResultMsgPhoto.put(chatId, passTest.getResultTestMessagePhoto(chatId, result));
                            executeSendMessage(passTest.getStarsForTest(chatId));
                            return;
                        } catch (Exception e) {
                            testResultMsg.put(chatId, passTest.getResultTestMessage(chatId, result));
                            executeSendMessage(passTest.getStarsForTest(chatId));

                            return;
                        }
                    }
                }
                return;
            }

            testTotalScoreMap.put(chatId, totalScore);
            sendTextOrPhotoQuestion(chatId, questioner, index);
            return;
        }

        if (data.contains("/newStar_")) {
            deleteMessage(chatId);

            Questioner questioner = passTheTest.get(chatId);
            if (!cantStarsDouble.containsKey(chatId)) {
                cantStarsDouble.put(chatId, new HashSet<>());
            }

            try {
                if (cantStarsDouble.get(chatId).contains(questioner.getQuestionerId())) {
                    if (testResultMsgPhoto.containsKey(chatId)) {
                        executeSendPhoto(testResultMsgPhoto.get(chatId));
                        executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
                    } else {
                        executeSendMessage(testResultMsg.get(chatId));
                        executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
                    }
                    testResultMsgPhoto.remove(chatId);
                    testResultMsg.remove(chatId);
                    passTheTest.remove(chatId);
                    return;
                }
            } catch (Exception e) {
            }

            cantStarsDouble.get(chatId).add(questioner.getQuestionerId());

            int stars = Integer.parseInt(data.split("_")[1]);
            int p = questioner.getPassCount() + 1;
            int usersCountStars = questioner.getUserStarsCount() + stars;
            if (stars == 0) {
                int without = questioner.getPassWithoutStars() + 1;
                questioner.setPassWithoutStars(without);
            }
            questioner.setPassCount(p);
            questioner.setUserStarsCount(usersCountStars);
            mongo.deleteQuestionerByQuestionerId(questioner.getQuestionerId());
            mongo.saveQuestioner(questioner);

            if (testResultMsgPhoto.containsKey(chatId)) {
                executeSendPhoto(testResultMsgPhoto.get(chatId));
                executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
            } else {
                executeSendMessage(testResultMsg.get(chatId));
                executeSendMessage(createTestMsg.getWelcomeMessage(chatId));
            }
            testResultMsgPhoto.remove(chatId);
            testResultMsg.remove(chatId);
            passTheTest.remove(chatId);
            return;
        }
    }

    private void sendTextOrPhotoQuestion(Long chatId, Questioner questioner, int index) {
        try {
            String s = questioner.getQuestions().get(index).getFilePath();
            System.out.println(s);
            executeSendPhoto(passTest.getMessageQuestionPhoto(chatId, questioner, index));
        } catch (Exception e) {
            executeSendMessage(passTest.getMessageQuestion(chatId, questioner, index));
            e.printStackTrace();
        }
    }

    private void photoMessageHandle(Long chatId, Update update) {

        if (waitForNewResultPic.containsKey(chatId)) {
            deleteMessage(chatId);
            int index = waitForNewResultPic.get(chatId);

            String filePath = dataPath + chatId + "_resultPic_" + System.currentTimeMillis() + index + ".jpg";
            try {
                new java.io.File(create.get(chatId).getResults().get(index).getFilePath()).delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            savePhoto(update, filePath);
            create.get(chatId).getResults().get(index).setFilePath(filePath);
            waitForNewResultPic.remove(chatId);
            removeFromAllWaitingLists(chatId);
            sendRegularTextOrPhotoMessage(chatId, index);
            return;
        }

        if (waitForMainTestPic.contains(chatId)) {

            String filePath = dataPath + chatId + "_main_" + System.currentTimeMillis() + ".jpg";
            create.get(chatId).setFilePath(filePath);

            savePhoto(update, filePath);
            create.get(chatId).setFilePath(filePath);
            sendTestPhotoOrRegularText(chatId);
        }

        if (waitForQuestionPic.contains(chatId)) {
            int lastIndexQuestionElement = create.get(chatId).getQuestionsListLastElementIndex();
            try {
                new java.io.File(create.get(chatId)
                        .getQuestions().get(lastIndexQuestionElement).getFilePath()).delete();
            } catch (Exception e) {

            }


            String filePath = dataPath + chatId + "_question_pic_" + System.currentTimeMillis() + lastIndexQuestionElement + ".jpg";
            create.get(chatId).getQuestions().get(lastIndexQuestionElement).setFilePath(filePath);

            savePhoto(update, filePath);
            create.get(chatId).getQuestions().get(lastIndexQuestionElement).setPic(new java.io.File(filePath));
            sendQuestionPhotoOrRegularText(chatId);
            removeFromAllWaitingLists(chatId);
        }

        if (waitForResultPic.contains(chatId)) {
            int lastResulListIndex = create.get(chatId).getResultListLastElementNumber();

            try {
                new java.io.File(create.get(chatId).getResults()
                        .get(lastResulListIndex).getFilePath()).delete();
            } catch (Exception e) {

            }

            String filePath = dataPath + chatId + "_resultPic_" + System.currentTimeMillis() + lastResulListIndex + ".jpg";
            create.get(chatId).getResults().get(lastResulListIndex).setFilePath(filePath);

            savePhoto(update, filePath);
            create.get(chatId).getResults().get(lastResulListIndex).setPic(new java.io.File(filePath));
            sendResultPhotoOrRegularText(chatId);
            removeFromAllWaitingLists(chatId);
        }

        if (waitForEditQuestionPic.containsKey(chatId)) {
            deleteMessage(chatId);
            int index = waitForEditQuestionPic.get(chatId);
            try {
                new java.io.File(create.get(chatId).getQuestions().get(index).getFilePath()).delete();
            } catch (Exception e) {


            }

            String filePath = dataPath + chatId + "_question_pic_" + System.currentTimeMillis() + index + ".jpg";
            create.get(chatId).getQuestions().get(index).setFilePath(filePath);

            savePhoto(update, filePath);
            create.get(chatId).getQuestions().get(index).setPic(new java.io.File(filePath));
            removeFromAllWaitingLists(chatId);
            sendQuestionPhotoOrRegularText(chatId);
        }
    }

    private void sendTestPhotoOrRegularText(Long chatId) {
        if (create.get(chatId).getFilePath() != null) {
            executeSendPhoto(createTestMsg.getMainCreateQuestionMenuSendPhoto(chatId, create.get(chatId)));
        } else {
            executeSendMessage(createTestMsg.getMainCreateQuestionMenuSendMessage(chatId, create.get(chatId)));
        }
    }

    private void sendTestPhotoOrRegularTextWhenYouEditQuestion(Long chatId, int editeIndexElement) {
        try {
            String s = create.get(chatId).getFilePath();
            executeSendPhoto(createTestMsg.getEditeQuestionByUserElementWithPhoto(chatId, create.get(chatId), editeIndexElement));
        } catch (Exception e) {
            executeSendMessage(createTestMsg.getEditeQuestionByUserElement(chatId, create.get(chatId), editeIndexElement));
        }
    }

    private void sendQuestionPhotoOrRegularText(Long chatId) {
        int lastQuestionIndexElement = create.get(chatId).getQuestionsListLastElementIndex();
        try {
            create.get(chatId).getQuestions().get(lastQuestionIndexElement).getPic().getName();
            executeSendPhoto(createTestMsg.getCreateNewQuestionParametersSendPhoto(chatId, create.get(chatId), lastQuestionIndexElement));


        } catch (Exception e) {
            if (createTestMsg.getCreateNewQuestionParametersSendMessage(chatId, create.get(chatId)).getText().equals("Вопросы еще не добавлены")) {
                sendTestPhotoOrRegularText(chatId);
            } else {
                executeSendMessage(createTestMsg.getCreateNewQuestionParametersSendMessage(chatId, create.get(chatId)));
            }


        }

    }

    private void sendResultPhotoOrRegularText(Long chatId) {
        try {
            int index = create.get(chatId).getResultListLastElementNumber();
            if (create.get(chatId).getResults().get(index).getPic() != null) {
                executeSendPhoto(createTestMsg.getMessageForCreateResultAndSendPhoto(chatId, create.get(chatId), index));
            } else {
                executeSendMessage(createTestMsg.getMessageForCreateResult(chatId, create.get(chatId), index));
            }
        } catch (Exception e) {
            int index = create.get(chatId).getResultListLastElementNumber();
            executeSendMessage(createTestMsg.getMessageForCreateResult(chatId, create.get(chatId), index));


        }
    }

    private void sendPhotoOrTextPassTest(Long chatId, Questioner questioner) {
        if (questioner.getFilePath() == null) {
            executeSendMessage(passTest.getTextForMessageForChooseTestForEditOrPass(chatId, questioner));
        } else {
            executeSendPhoto(passTest.getTextForMessageForChooseTestForEditOrPassPhoto(chatId, questioner));
        }
    }

    private void savePhoto(Update update, String filePath) {
        removeFromAllWaitingLists(update.getMessage().getChatId());

        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize largestPhoto = photos.get(photos.size() - 1);
        String fileId = largestPhoto.getFileId();
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);

        try {
            File file = execute(getFile);
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();

            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();

            FileOutputStream outputStream = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {


        }
    }

    private void deleteUsersFileWhenDeleteQuestioner(Questioner questioner) {
        try {
            new java.io.File(questioner.getFilePath()).delete();
        } catch (Exception e) {
        }

        for (Question q : questioner.getQuestions()) {
            try {
                new java.io.File(q.getFilePath()).delete();
            } catch (Exception e) {
            }

        }

        for (Result r : questioner.getResults()) {
            try {
                new java.io.File(r.getFilePath()).delete();
            } catch (Exception e) {
            }
        }
    }

    private void deleteAllUsersFile(Long chatId) {
        try {
            new java.io.File(create.get(chatId).getFilePath()).delete();
        } catch (Exception e) {


        }

        try {
            for (Question q : create.get(chatId).getQuestions()) {
                new java.io.File(q.getFilePath()).delete();
            }
        } catch (Exception e) {


        }

        try {
            for (Result r : create.get(chatId).getResults()) {
                new java.io.File(r.getFilePath()).delete();
            }
        } catch (Exception e) {


        }
    }

    private boolean checkChannelSubscription(Long chatId, String channelUsername) {
        try {
            ChatMember chatMember = execute(new GetChatMember(channelUsername, chatId));
            return chatMember.getStatus().equals("member") || chatMember.getStatus().equals("creator") || chatMember.getStatus().equals("administrator");
        } catch (TelegramApiException e) {


            return false;
        }
    }

    private void sendTheListOfTest(Long chatId) {
        List<Questioner> qList = mongo.getQuestionersByChatId(chatId);

        try {
            qList.get(0).getName();
            for (Questioner q : qList) {
                sendPhotoOrTextPassTest(chatId, q);
            }
        } catch (Exception e) {
            executeSendMessage(createTestMsg.getNoOneTest(chatId, "Ни одного теста не создано"));
        }
    }

    private List<String> splitString(String originalText, int maxLength) {
        List<String> parts = new ArrayList<>();
        int length = Math.min(originalText.length(), maxLength);
        String part1 = originalText.substring(0, length);
        String part2 = originalText.substring(length);

        if (part2.length() > 0) {
            int index = part2.indexOf("\n");
            if (index > 0 && index < maxLength * 0.3) {
                parts.add(part1 + part2.substring(0, index));
                parts.addAll(splitString(part2.substring(index + 1), maxLength));
            } else {
                index = part2.lastIndexOf(" ", maxLength);
                if (index == -1) {
                    index = maxLength;
                }
                parts.add(part1 + part2.substring(0, index));
                parts.addAll(splitString(part2.substring(index), maxLength));
            }
        } else {
            parts.add(part1);
        }
        return parts;
    }

    private void executeSendPhoto(SendPhoto msg) {

        if (doubleMsg.containsKey(Long.valueOf(msg.getChatId()))) {
            DeleteMessage deleteMessage = new DeleteMessage();
            Long chatId = Long.valueOf(msg.getChatId());
            deleteMessage.setChatId(chatId);

            for(Integer i : doubleMsg.get(chatId)) {

                deleteMessage.setMessageId(i);
                doubleMsg.remove(chatId);

                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        String originalText = msg.getCaption();
        if(originalText.length() > 1024) {
            List<String> parts = splitString(originalText, 1024);
            msg.setCaption(parts.get(0));

            try {
                List<Integer> intList = new ArrayList<>();
                intList.add(execute(msg).getMessageId());

                SendMessage second = new SendMessage();
                second.setChatId(Long.valueOf(msg.getChatId()));
                second.setText(parts.get(1));
                intList.add(execute(second).getMessageId());
                doubleMsg.put(Long.valueOf(msg.getChatId()), intList);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            chatIdMsgId.put(Long.valueOf(msg.getChatId()), execute(msg).getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeSendMessage(SendMessage msg) {
        Long chatId = Long.valueOf(msg.getChatId());
        String originalText = msg.getText();

        if (doubleMsg.containsKey(chatId)) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);

            for(Integer i : doubleMsg.get(chatId)) {

                deleteMessage.setMessageId(i);
                doubleMsg.remove(chatId);

                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

        if(originalText.length() > 3000) {
            List<String> parts = splitString(originalText, 3000);
            msg.setText(parts.get(0));

            try {
                List<Integer> intList = new ArrayList<>();
                intList.add(execute(msg).getMessageId());

                SendMessage second = new SendMessage();
                second.setChatId(Long.valueOf(msg.getChatId()));
                second.setText(parts.get(1));
                intList.add(execute(second).getMessageId());
                doubleMsg.put(Long.valueOf(msg.getChatId()), intList);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            chatIdMsgId.put(Long.valueOf(msg.getChatId()), execute(msg).getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    public void deleteMessage(Long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(chatIdMsgId.get(chatId));
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}