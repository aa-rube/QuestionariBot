package app.questionary.constructor.keyboards;

import app.bot.config.BotConfig;
import app.questionary.constructor.buttons.NumbersButtons;
import app.questionary.constructor.buttons.StringButtons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateTestKeyboardConstructor {
    @Autowired
    private BotConfig config;

    public InlineKeyboardMarkup getMainKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton pass = new InlineKeyboardButton();
        InlineKeyboardButton create = new InlineKeyboardButton();
        pass.setText("✔️ Пройти тест");
        pass.setCallbackData(String.valueOf(NumbersButtons.PASS_THE_TEST.getValue()));

        create.setText("\uD83D\uDD8CСоздать тест");
        create.setCallbackData(String.valueOf(NumbersButtons.CREATE_TEST.getValue()));
        firstRow.add(pass);
        firstRow.add(create);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton myTests = new InlineKeyboardButton();
        myTests.setText("\uD83D\uDCD2Мои тесты");
        myTests.setCallbackData(String.valueOf(NumbersButtons.MY_TESTS.getValue()));
        secondRow.add(myTests);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton help = new InlineKeyboardButton();
        help.setText("❔Помощь");
        help.setCallbackData(String.valueOf(NumbersButtons.HELP.getValue()));
        thirdRow.add(help);


        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getPassTestKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();
        List<InlineKeyboardButton> firstRow = new ArrayList<>();


        InlineKeyboardButton pass = new InlineKeyboardButton();
        pass.setText("Проходить тесты");
        pass.setSwitchInlineQueryCurrentChat("");

        InlineKeyboardButton share = new InlineKeyboardButton();
        share.setText("Поделиться с друзьями");
        share.setSwitchInlineQuery("");


        firstRow.add(pass);
        firstRow.add(share);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(getBackToMainRow());

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getMainMenuCreateQuestionsAndOtherParameters() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton refresh = new InlineKeyboardButton();
        refresh.setText("\uD83D\uDD04Обновить");
        refresh.setCallbackData(String.valueOf(NumbersButtons.REFRESH_MAIN_CREATE_TEST_MENU.getValue()));

        InlineKeyboardButton save = new InlineKeyboardButton();
        save.setText("\uD83D\uDCBEСохранить");
        save.setCallbackData(String.valueOf(NumbersButtons.SAVE.getValue()));
        firstRow.add(refresh);
        firstRow.add(save);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton addName = new InlineKeyboardButton();
        addName.setText("\uD83D\uDCEFИзменить название");
        addName.setCallbackData(String.valueOf(NumbersButtons.ADD_NAME.getValue()));
        secondRow.add(addName);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton addQuestion = new InlineKeyboardButton();
        addQuestion.setText("➕Добавить вопрос");
        addQuestion.setCallbackData(String.valueOf(NumbersButtons.ADD_QUESTION.getValue()));

        InlineKeyboardButton listQuestion = new InlineKeyboardButton();
        listQuestion.setText("\uD83D\uDCDCСписок вопросов");
        listQuestion.setCallbackData(String.valueOf(NumbersButtons.LIST_QUESTIONS.getValue()));
        thirdRow.add(addQuestion);
        thirdRow.add(listQuestion);

        List<InlineKeyboardButton> fourthRow = new ArrayList<>();
        InlineKeyboardButton addResult = new InlineKeyboardButton();
        addResult.setText("➕Результаты");
        addResult.setCallbackData(String.valueOf(NumbersButtons.MENU_ADD_RESULT.getValue()));

        fourthRow.add(addResult);
        List<InlineKeyboardButton> fiveRow = new ArrayList<>();
        InlineKeyboardButton changePicture = new InlineKeyboardButton();
        changePicture.setText("\uD83D\uDDBCИзменить картинку");
        changePicture.setCallbackData(String.valueOf(NumbersButtons.CHANGE_MAIN_TEST_PIC.getValue()));
        fiveRow.add(changePicture);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        keyboardMatrix.add(fourthRow);
        keyboardMatrix.add(fiveRow);
        keyboardMatrix.add(getBackToMainRow());

        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getSingleBackButtonToMainCreateMenu() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();
        keyboardMatrix.add(getBackToMainCreateQuestionRow());
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getNoOneTestBack() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("Назад");
        back.setCallbackData("/check");

        backRow.add(back);
        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getCreateQuestionMenuKeyboard() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton refresh = new InlineKeyboardButton();
        refresh.setText("\uD83D\uDD04Обновить");
        refresh.setCallbackData(String.valueOf(NumbersButtons.REFRESH_CREATE_QUESTION.getValue()));

        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText("\uD83D\uDDD1Удалить");
        delete.setCallbackData(String.valueOf(NumbersButtons.DELETE_QUESTION.getValue()));
        firstRow.add(refresh);
        firstRow.add(delete);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton addOption = new InlineKeyboardButton();
        addOption.setText("➕Добавить ответ");
        addOption.setCallbackData(String.valueOf(NumbersButtons.ADD_OPTION.getValue()));

        InlineKeyboardButton listOption = new InlineKeyboardButton();
        listOption.setText("\uD83D\uDCDCСписок ответов");
        listOption.setCallbackData(String.valueOf(NumbersButtons.LIST_OPTION.getValue()));
        secondRow.add(addOption);
        secondRow.add(listOption);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton addPic = new InlineKeyboardButton();
        addPic.setText("\uD83D\uDDBCИзменить картинку");
        addPic.setCallbackData(String.valueOf(NumbersButtons.ADD_QUESTION_PIC.getValue()));
        thirdRow.add(addPic);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        keyboardMatrix.add(getBackToMainCreateQuestionRow());
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }


    public InlineKeyboardMarkup getCreateOptionsParameters() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton changeScore = new InlineKeyboardButton();
        changeScore.setText("\uD83E\uDDEEИзменить баллы");
        changeScore.setCallbackData(String.valueOf(NumbersButtons.CREATE_SCORE_OPTION.getValue()));

        InlineKeyboardButton deleteScore = new InlineKeyboardButton();
        deleteScore.setText("\uD83D\uDDD1Удалить");
        deleteScore.setCallbackData(String.valueOf(NumbersButtons.DELETE_OPTION.getValue()));
        firstRow.add(changeScore);
        firstRow.add(deleteScore);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton refresh = new InlineKeyboardButton();
        refresh.setText("\uD83D\uDD04Обновить");
        refresh.setCallbackData(String.valueOf(NumbersButtons.REFRESH_OPTIONS_PARAMETER.getValue()));
        secondRow.add(refresh);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀\uFE0FНазад");
        back.setCallbackData(String.valueOf(NumbersButtons.GET_BACK_TO_CREATE_QUESTION.getValue()));
        backRow.add(back);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getMainKeyboardForCreateResults() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton addNewRes = new InlineKeyboardButton();
        addNewRes.setText("➕Добавить результат");
        addNewRes.setCallbackData(String.valueOf(NumbersButtons.CREATE_NEW_RESULT_FOR_TEST.getValue()));

        InlineKeyboardButton deleteTheRes = new InlineKeyboardButton();
        deleteTheRes.setText("\uD83D\uDDD1Удалить");
        deleteTheRes.setCallbackData(String.valueOf(NumbersButtons.DELETE_RESULT.getValue()));
        firstRow.add(addNewRes);
        firstRow.add(deleteTheRes);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton changePicture = new InlineKeyboardButton();
        changePicture.setText("\uD83D\uDDBCИзменить картинку");
        changePicture.setCallbackData(String.valueOf(NumbersButtons.ADD_RES_PIC.getValue()));
        secondRow.add(changePicture);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton refresh = new InlineKeyboardButton();
        refresh.setText("\uD83D\uDD04Обновить");
        refresh.setCallbackData(String.valueOf(NumbersButtons.MENU_ADD_RESULT.getValue()));
        thirdRow.add(refresh);

        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀\uFE0FНазад");
        back.setCallbackData(String.valueOf(NumbersButtons.BACK_TO_CREATE_MAIN.getValue()));
        backRow.add(back);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        keyboardMatrix.add(backRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    private List<InlineKeyboardButton> getBackToMainRow() {
        List<InlineKeyboardButton> getBackRow = new ArrayList<>();
        InlineKeyboardButton backToMain = new InlineKeyboardButton();
        backToMain.setText("◀\uFE0FНазад");
        backToMain.setCallbackData(String.valueOf(NumbersButtons.BACK_TO_START_MENU.getValue()));
        getBackRow.add(backToMain);
        return getBackRow;
    }

    private List<InlineKeyboardButton> getBackToMainCreateQuestionRow() {
        List<InlineKeyboardButton> getBackRow = new ArrayList<>();
        InlineKeyboardButton backToMain = new InlineKeyboardButton();
        backToMain.setText("◀\uFE0FНазад");
        backToMain.setCallbackData(String.valueOf(NumbersButtons.BACK_TO_CREATE_MAIN.getValue()));
        getBackRow.add(backToMain);
        return getBackRow;
    }

    public InlineKeyboardMarkup getBackToMainCreateQuestionParametersButton() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> getBackRow = new ArrayList<>();
        InlineKeyboardButton backToMain = new InlineKeyboardButton();
        backToMain.setText("◀\uFE0FНазад");
        backToMain.setCallbackData(String.valueOf(NumbersButtons.REFRESH_CREATE_QUESTION.getValue()));
        getBackRow.add(backToMain);

        keyboardMatrix.add(getBackRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);

        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackToMainCreateResultParameters() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> getBackRow = new ArrayList<>();
        InlineKeyboardButton backToMain = new InlineKeyboardButton();
        backToMain.setText("◀\uFE0FНазад");
        backToMain.setCallbackData(String.valueOf(NumbersButtons.GET_BACK_TO_CREATE_QUESTION_PARAMETERS.getValue()));
        getBackRow.add(backToMain);

        keyboardMatrix.add(getBackRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);

        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getToCreatePictureChooseMenu() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> getBackRow = new ArrayList<>();
        InlineKeyboardButton backToMain = new InlineKeyboardButton();
        backToMain.setText("◀\uFE0FНазад");
        backToMain.setCallbackData(String.valueOf(NumbersButtons.REFRESH_CREATE_QUESTION.getValue()));
        getBackRow.add(backToMain);

        keyboardMatrix.add(getBackRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackToMenuAddResult() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> back = new ArrayList<>();
        InlineKeyboardButton refresh = new InlineKeyboardButton();
        refresh.setText("◀\uFE0FНазад");
        refresh.setCallbackData(String.valueOf(NumbersButtons.MENU_ADD_RESULT.getValue()));

        keyboardMatrix.add(back);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getEditQuestionUserIndexElement(int editeIndexElement) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> zeroRow = new ArrayList<>();
        InlineKeyboardButton edit = new InlineKeyboardButton();
        edit.setText("\uD83E\uDE84Изменить текст вопроса");
        edit.setCallbackData(StringButtons.EDIT_QUESTION_TEXT_BY_USER_INDEX.getValue() + editeIndexElement);
        zeroRow.add(edit);

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton refresh = new InlineKeyboardButton();
        refresh.setText("\uD83D\uDD04Обновить");
        refresh.setCallbackData(StringButtons.REFRESH_EDIT_QUESTION_BY_USER_INDEX.getValue() + editeIndexElement);

        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText("\uD83D\uDDD1Удалить");
        delete.setCallbackData(StringButtons.DELETE_QUESTION_BY_USER_INDEX.getValue() + editeIndexElement);
        firstRow.add(refresh);
        firstRow.add(delete);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton addOption = new InlineKeyboardButton();
        addOption.setText("➕Добавить ответ");
        addOption.setCallbackData(StringButtons.ADD_OPTION_TO_QUESTION_BY_USER_INDEX.getValue() + editeIndexElement);

        InlineKeyboardButton listOption = new InlineKeyboardButton();
        listOption.setText("\uD83D\uDCDCСписок ответов");
        listOption.setCallbackData(String.valueOf(NumbersButtons.LIST_OPTION.getValue()));
        secondRow.add(addOption);
        secondRow.add(listOption);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton addPic = new InlineKeyboardButton();
        addPic.setText("\uD83D\uDDBCИзменить картинку");
        addPic.setCallbackData(StringButtons.ADD_PIC_TO_QUESTION_BY_USER_INDEX.getValue() + editeIndexElement);
        thirdRow.add(addPic);

        keyboardMatrix.add(zeroRow);
        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(thirdRow);
        keyboardMatrix.add(getBackToMainCreateQuestionRow());
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getEditeOptionsByUserIndex(String userIndex) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton edit = new InlineKeyboardButton();
        edit.setText("\uD83E\uDE84Изменить текст ответа");
        edit.setCallbackData(StringButtons.EDIT_OPTION_TEXT_BY_USER_INDEX.getValue() + userIndex);

        InlineKeyboardButton editScore = new InlineKeyboardButton();
        editScore.setText("\uD83E\uDDEEИзменить баллы");
        editScore.setCallbackData(StringButtons.EDIT_SCORE_OPTION_BY_USER_INDEX.getValue() + userIndex);
        firstRow.add(edit);
        firstRow.add(editScore);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText("\uD83D\uDDD1Удалить");
        delete.setCallbackData(StringButtons.DELETE_OPTION_AND_SCORE_BY_USER_INDEX.getValue() + userIndex);
        secondRow.add(delete);

        List<InlineKeyboardButton> back = new ArrayList<>();
        InlineKeyboardButton backToMain = new InlineKeyboardButton();
        backToMain.setText("◀\uFE0FНазад");
        backToMain.setCallbackData(String.valueOf(NumbersButtons.REFRESH_CREATE_QUESTION.getValue()));
        back.add(backToMain);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(back);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getEditResultByUserElementCommand(int userIndex) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton edit = new InlineKeyboardButton();
        edit.setText("\uD83E\uDE84Изменить текст результата");
        edit.setCallbackData("/goRefactRes_" + userIndex);

        InlineKeyboardButton editScore = new InlineKeyboardButton();
        editScore.setText("\uD83E\uDDEEИзменить баллы");
        editScore.setCallbackData("/changeRes_" + userIndex);
        firstRow.add(edit);
        firstRow.add(editScore);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText("\uD83D\uDDD1Удалить");
        delete.setCallbackData("/resultRemove_" + userIndex);
        secondRow.add(delete);

        List<InlineKeyboardButton> picRow = new ArrayList<>();
        InlineKeyboardButton pic = new InlineKeyboardButton();
        pic.setText("\uD83D\uDDBCИзменить картинку");
        pic.setCallbackData("/resulPictEdit_" + userIndex);
        picRow.add(pic);

        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton refresh = new InlineKeyboardButton();
        refresh.setText("Назад");
        refresh.setCallbackData(String.valueOf(NumbersButtons.MENU_ADD_RESULT.getValue()));
        thirdRow.add(refresh);

        keyboardMatrix.add(firstRow);
        keyboardMatrix.add(secondRow);
        keyboardMatrix.add(picRow);
        keyboardMatrix.add(thirdRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

}
