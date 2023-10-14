package app.bot.admin;

import app.questionary.repository.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModeratorKeyboard {
    @Autowired
    private MongoService mongo;

    public InlineKeyboardMarkup getStartKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton findUser = new InlineKeyboardButton();
        findUser.setText("Найти пользователя");
        findUser.setCallbackData("1");
        firstRow.add(findUser);



        keyboardMatrix.add(firstRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getHandleUsersKeyboards(Long userChatId, int index) {
            InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

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

            keyboardMatrix.add(secondRow);
            keyboardMatrix.add(backRow);

            inLineKeyBoard.setKeyboard(keyboardMatrix);
            return inLineKeyBoard;
    }
}
