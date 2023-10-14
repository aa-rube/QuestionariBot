package app.bot.constructor.keyboards;

import app.bot.constructor.buttons.NumbersButtons;
import app.questionary.model.Option;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class PassTheTestKeyBoardsConstructor {
    public InlineKeyboardMarkup getChangeParamsQuestionerOrPassTheTestKeys(String name, String questionId) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton pass = new InlineKeyboardButton();
        pass.setText(name + " ⏩");
        pass.setCallbackData("/passTheTest_" + questionId);
        firstRow.add(pass);


        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton edit = new InlineKeyboardButton();
        edit.setText("Редактировать");
        edit.setCallbackData("star_" + questionId);
        secondRow.add(edit);

        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText("Удалить");
        delete.setCallbackData("removeT_" + questionId);
        secondRow.add(delete);


        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀\uFE0FНазад");
        back.setCallbackData(String.valueOf(NumbersButtons.BACK_TO_START_MENU.getValue()));
        backRow.add(back);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(backRow);

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getOptions(List<Option> options, int index) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        for (Option o : options) {
            List<InlineKeyboardButton> firstRow = new ArrayList<>();
            InlineKeyboardButton pass = new InlineKeyboardButton();
            pass.setText(o.getOptionText());
            pass.setCallbackData("/goThrough_" + o.getScore() + "_" + index);
            firstRow.add(pass);
            keyboardMatrix.add(firstRow);
        }


        keyboardMatrix.add(getBackToMainRow());
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    private List<InlineKeyboardButton> getBackToMainRow() {
        List<InlineKeyboardButton> getBackRow = new ArrayList<>();
        InlineKeyboardButton backToMain = new InlineKeyboardButton();
        backToMain.setText("\uD83D\uDEABОтмена");
        backToMain.setCallbackData(String.valueOf(NumbersButtons.BACK_TO_START_MENU.getValue()));
        getBackRow.add(backToMain);
        return getBackRow;
    }

    public InlineKeyboardMarkup stars() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();
        String star = "/newStar_";
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton five = new InlineKeyboardButton();
        five.setText("⭐\uFE0F⭐\uFE0F⭐\uFE0F⭐\uFE0F⭐\uFE0F");
        five.setCallbackData(star + 5);
        firstRow.add(five);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton four = new InlineKeyboardButton();
        four.setText("⭐\uFE0F⭐\uFE0F⭐\uFE0F⭐\uFE0F");
        four.setCallbackData(star + 4);
        secondRow.add(four);

        InlineKeyboardButton three = new InlineKeyboardButton();
        three.setText("⭐\uFE0F⭐\uFE0F⭐\uFE0F");
        three.setCallbackData(star + 3);
        secondRow.add(three);

        InlineKeyboardButton two = new InlineKeyboardButton();
        two.setText("⭐\uFE0F⭐\uFE0F");
        two.setCallbackData(star + 2);
        secondRow.add(two);

        InlineKeyboardButton one = new InlineKeyboardButton();
        one.setText("⭐\uFE0F");
        one.setCallbackData(star + 1);
        secondRow.add(one);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton cancel = new InlineKeyboardButton();
        cancel.setText("Прододжить");
        cancel.setCallbackData(star + 0);
        thirdRow.add(cancel);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
