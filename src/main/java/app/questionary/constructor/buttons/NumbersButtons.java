package app.questionary.constructor.buttons;

public enum NumbersButtons {
    PASS_THE_TEST(0),
    CREATE_TEST(1),
    MY_TESTS(2),
    HELP(3),
    BACK_TO_START_MENU(4),
    REFRESH_MAIN_CREATE_TEST_MENU(5),
    SAVE(6),
    ADD_NAME(7),
    ADD_QUESTION(8),
    LIST_QUESTIONS(9),
    MENU_ADD_RESULT(10),
    CHANGE_MAIN_TEST_PIC(12),
    BACK_TO_CREATE_MAIN(13),
    REFRESH_CREATE_QUESTION(14),
    DELETE_QUESTION(15),
    ADD_OPTION(16),
    LIST_OPTION(17),
    ADD_QUESTION_PIC(18),
    CREATE_SCORE_OPTION(19),
    DELETE_OPTION(20),
    REFRESH_OPTIONS_PARAMETER(21),
    GET_BACK_TO_CREATE_QUESTION(22),
    DELETE_RESULT(23),
    CREATE_NEW_RESULT_FOR_TEST(24),
    GET_BACK_TO_CREATE_QUESTION_PARAMETERS(25),
    ADD_RES_PIC(26),
    EDIT_RESULT_TEXT_BY_USER_ELEMENT(27);

    private final int number;

    NumbersButtons(int number) {
        this.number = number;
    }

    public int getValue() {
        return number;
    }
}
