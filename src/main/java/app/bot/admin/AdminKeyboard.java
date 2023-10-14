package app.bot.admin;

import app.bot.model.BotUser;
import app.questionary.model.Partner;
import app.questionary.model.Questioner;
import app.questionary.repository.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminKeyboard {
    @Autowired
    private MongoService mongo;

    public InlineKeyboardMarkup getStartKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton addPartner = new InlineKeyboardButton();
        addPartner.setText("Партнеры");
        addPartner.setCallbackData("0");
        firstRow.add(addPartner);

        InlineKeyboardButton findUser = new InlineKeyboardButton();
        findUser.setText("Найти пользователя");
        findUser.setCallbackData("1");
        firstRow.add(findUser);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton stata = new InlineKeyboardButton();
        stata.setText("Cтатисктика");
        stata.setCallbackData("5");
        secondRow.add(stata);

        InlineKeyboardButton spam = new InlineKeyboardButton();
        spam.setText("Рассылка");
        spam.setCallbackData("6");
        secondRow.add(spam);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getPartnersKeyboards() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton addPartner = new InlineKeyboardButton();
        addPartner.setText("Добавить");
        addPartner.setCallbackData("2");

        InlineKeyboardButton deletePartner = new InlineKeyboardButton();
        deletePartner.setText("Удалить");
        deletePartner.setCallbackData("3");
        firstRow.add(addPartner);
        firstRow.add(deletePartner);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton status = new InlineKeyboardButton();
        status.setText("Статус");
        status.setCallbackData("4");
        secondRow.add(status);


        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("99");
        backRow.add(back);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackToEditPartnersButton() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("0");
        backRow.add(back);

        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackToMain() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("99");
        backRow.add(back);

        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getPartnersToDelete() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        for (Partner p : mongo.getAllPartners()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton partner = new InlineKeyboardButton();
            partner.setText("Удалить " + p.getGroupUserName());
            partner.setCallbackData("delete_" + p.getGroupUserName());
            row.add(partner);
            keyboardMatrix.add(row);

        }

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("0");
        backRow.add(back);

        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup sendTheLastMessageToEveryOne() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> sendRow = new ArrayList<>();
        InlineKeyboardButton send = new InlineKeyboardButton();
        send.setText("\uD83D\uDCE4Отправить последнее сообщение");
        send.setCallbackData("7");
        sendRow.add(send);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("99");
        backRow.add(back);

        keyboardMatrix.add(sendRow);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getLookAfterToAllUsers(int lastIndex) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<BotUser> users = mongo.getAllBotUsers();
        int userSize = users.size();
        int maxIndex = 12;

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = lastIndex; i < Math.min(lastIndex + maxIndex, userSize); i++) {
            InlineKeyboardButton user = new InlineKeyboardButton();
            user.setText(users.get(i).getUserName());
            user.setCallbackData("user_" + users.get(i).getChatId() + "_" + i);
            row.add(user);
            if ((i + 1) % 3 == 0) {
                keyboardMatrix.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            keyboardMatrix.add(row);
        }

        List<InlineKeyboardButton> nextOrPrevious = new ArrayList<>();
        InlineKeyboardButton previous = new InlineKeyboardButton();
        previous.setText("Предыдущая");
        previous.setCallbackData("getUsers_" + Math.max(0, lastIndex - maxIndex));

        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText("Следующая");
        next.setCallbackData("getUsers_" + Math.min(userSize - maxIndex, lastIndex + maxIndex));

        nextOrPrevious.add(previous);
        nextOrPrevious.add(next);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("99");
        backRow.add(back);

        keyboardMatrix.add(nextOrPrevious);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getHandleUsersKeyboards(Long userChatId, int index) {
            InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

            List<InlineKeyboardButton> firstRow = new ArrayList<>();
            InlineKeyboardButton block = new InlineKeyboardButton();
            block.setText("❌Блокировать");
            block.setCallbackData("block_" + userChatId);
            firstRow.add(block);


            InlineKeyboardButton unblock = new InlineKeyboardButton();
            unblock.setText("✅Разблокировать");
            unblock.setCallbackData("acces_" + userChatId);
            firstRow.add(unblock);

            List<InlineKeyboardButton> secondRow = new ArrayList<>();
            InlineKeyboardButton tests = new InlineKeyboardButton();
            tests.setText("\uD83D\uDD0EТесты пользователя");
            tests.setCallbackData("getAllTests_" + userChatId + "_" + 0);
            secondRow.add(tests);

            List<InlineKeyboardButton> backRow = new ArrayList<>();
            InlineKeyboardButton back = new InlineKeyboardButton();
            back.setText("Назад");
            back.setCallbackData("/getUsers_" + index);
            backRow.add(back);

            keyboardMatrix.add(firstRow);
            keyboardMatrix.add(secondRow);
            keyboardMatrix.add(backRow);

            inLineKeyBoard.setKeyboard(keyboardMatrix);
            return inLineKeyBoard;
    }

    public InlineKeyboardMarkup realGetBack(Long userChatId) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("user_" + userChatId);
        backRow.add(back);

        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getAllUsersTest(Long userChatId,int lastIndex) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<Questioner> questioners = mongo.getQuestionersByChatId(userChatId);
        int userSize = questioners.size();
        int maxIndex = 12;

        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = lastIndex; i < Math.min(lastIndex + maxIndex, userSize); i++) {
            InlineKeyboardButton user = new InlineKeyboardButton();
            String text = questioners.get(i).getName();
            user.setText("❌" + text.substring(0, Math.min(text.length(), 20)));
            user.setCallbackData("dellTest_" + questioners.get(i).getQuestionerId());
            row.add(user);
            if ((i + 1) % 3 == 0) {
                keyboardMatrix.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            keyboardMatrix.add(row);
        }

        List<InlineKeyboardButton> nextOrPrevious = new ArrayList<>();
        InlineKeyboardButton previous = new InlineKeyboardButton();
        previous.setText("Предыдущая");
        previous.setCallbackData("getAllTests_" + Math.max(0, lastIndex - maxIndex));

        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText("Следующая");
        next.setCallbackData("getAllTests" + Math.min(userSize - maxIndex, lastIndex + maxIndex));

        nextOrPrevious.add(previous);
        nextOrPrevious.add(next);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("user_" + userChatId);
        backRow.add(back);

        keyboardMatrix.add(nextOrPrevious);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
