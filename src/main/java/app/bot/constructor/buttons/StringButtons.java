package app.bot.constructor.buttons;

public enum StringButtons {
    EDIT_QUESTION("/editQuestion_"),
    EDIT_OPTION("/editOption_"),
    EDIT_RESULT_BY_COMMAND("/editResult_"),

    REFRESH_EDIT_QUESTION_BY_USER_INDEX("/refreshQ_"),
    DELETE_QUESTION_BY_USER_INDEX("/deleteQ_"),
    ADD_OPTION_TO_QUESTION_BY_USER_INDEX("/addQ_"),
    ADD_PIC_TO_QUESTION_BY_USER_INDEX("/addQuestionPic_"),
    EDIT_QUESTION_TEXT_BY_USER_INDEX("/editeQuestionText_"),
    EDIT_OPTION_TEXT_BY_USER_INDEX("/editeOptText_"),
    DELETE_OPTION_AND_SCORE_BY_USER_INDEX("/delOption_"),
    EDIT_SCORE_OPTION_BY_USER_INDEX("/editeOptScore_"),
    CALL_MY_TEST_TO_PASS("passTest_");
    private final String string;

    StringButtons(String string) {
        this.string = string;
    }

    public String getValue() {
        return string;
    }
}
