package app.questionary.constructor.message;

import app.questionary.constructor.keyboards.CreateTestKeyboardConstructor;
import app.questionary.constructor.buttons.StringButtons;
import app.questionary.model.Questioner;
import app.questionary.model.Option;
import app.questionary.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class MessageForCreateTestConstructor {
    @Autowired
    private CreateTestKeyboardConstructor keyboards;

    private SendMessage getSendMsgObject(Long chatId, String text, InlineKeyboardMarkup kb) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(kb);
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




    public SendMessage getWelcomeMessage(Long chatId) {
        String text = "Привет!\nНу что, начнем создавать тесты?";
        return getSendMsgObject(chatId, text, keyboards.getMainKeyboard());
    }
    public SendMessage getPassTestMessage(Long chatId) {
        String text = "Здесь вы можете проходить тесты созданные участниками бота\n\n"
                + "Для поиска определенного теста - напишите его название";
        return getSendMsgObject(chatId, text, keyboards.getPassTestKeyboard());
    }




    private String getQuestionAndOptionsList(Questioner questioner) {
        StringBuilder builder = new StringBuilder();
        try {
            if (!questioner.getQuestions().isEmpty()) {
                builder.append("\uD83D\uDCDDРЕДАКТИРУЕМ ПОСЛЕДНИЙ ВОПРОС:\n\"")
                        .append(questioner.getQuestionsListLastElementIndex()).append(". ")
                        .append(questioner.getQuestions().get(questioner.getQuestionsListLastElementIndex()).getQuestionText()).append("\"\n");

                int i = 0;
                builder.append("\nСписок вопросов: \n");
                for (Question q : questioner.getQuestions()) {
                    builder.append(i).append(". \"").append(q.getQuestionText()).append("\"");
                    builder.append("\nред: ").append(StringButtons.EDIT_QUESTION.getValue()).append(i).append(" \n");

                    for (int j = 0; j < questioner.getQuestions().get(i).getOptions().size(); j++) {
                        Option option = questioner.getQuestions().get(i).getOptions().get(j);
                        builder.append("Ответ: \"").append(option.getOptionText()).append("\", балл: ").append(option.getScore()).append(", ");
                        builder.append("\nред: ").append(StringButtons.EDIT_OPTION.getValue())
                                .append(i).append("_").append(j).append(" \n");
                    }
                    builder.append("\n\n");
                    i++;
                }
            } else {
                return "Вопросы еще не добавлены";
            }
        } catch (Exception e) {
            return "Вопросы еще не добавлены";
        }
        return builder.toString();
    }
    public SendPhoto getCreateNewQuestionParametersSendPhoto(Long chatId, Questioner questioner, int lastIndex) {
        String fileId;
        try {
            fileId = questioner.getQuestions().get(lastIndex).getFilePath();
        } catch (Exception e) {
            fileId = null;
        }
        return getSendPhotoMsgObject(chatId, getQuestionAndOptionsList(questioner),
                keyboards.getCreateQuestionMenuKeyboard(), fileId);
    }
    public SendMessage getCreateNewQuestionParametersSendMessage(Long chatId, Questioner questioner) {
        return getSendMsgObject(chatId, getQuestionAndOptionsList(questioner), keyboards.getCreateQuestionMenuKeyboard());
    }
    public SendPhoto getEditeQuestionByUserElementWithPhoto(Long chatId, Questioner questioner, int editeIndexElement) {
        StringBuilder builder = new StringBuilder();
        String fileId = questioner.getQuestions().get(editeIndexElement).getFilePath();
        try {
            if (!questioner.getQuestions().isEmpty()) {
                getEditQuestionText(questioner, editeIndexElement, builder);
                return getSendPhotoMsgObject(chatId, builder.toString(),
                        keyboards.getEditQuestionUserIndexElement(editeIndexElement), fileId);
            } else {
                builder.setLength(0);
                builder.append("Вопросы еще не добавлены");
                return getSendPhotoMsgObject(chatId, builder.toString(),
                        keyboards.getEditQuestionUserIndexElement(editeIndexElement), fileId);

            }
        } catch (Exception e) {
            builder.setLength(0);
            builder.append("Вопросы еще не добавлены");
            return getSendPhotoMsgObject(chatId, builder.toString(),
                    keyboards.getEditQuestionUserIndexElement(editeIndexElement), fileId);

        }

    }

    public SendMessage getEditeQuestionByUserElement(Long chatId, Questioner questioner, int editeIndexElement) {
        StringBuilder builder = new StringBuilder();
        try {
            if (!questioner.getQuestions().isEmpty()) {
                getEditQuestionText(questioner, editeIndexElement, builder);

            } else {
                builder.setLength(0);
                builder.append("Вопросы еще не добавлены");

            }
            return getSendMsgObject(chatId, builder.toString(), keyboards.getEditQuestionUserIndexElement(editeIndexElement));
        } catch (Exception e) {
            builder.setLength(0);
            builder.append("Вопросы еще не добавлены");
            return getSendMsgObject(chatId, builder.toString(), keyboards.getEditQuestionUserIndexElement(editeIndexElement));

        }
    }

    private void getEditQuestionText(Questioner questioner, int editeIndexElement, StringBuilder builder) {
        builder.append("\uD83D\uDCDDРЕДАКТИРУЕМ ВОПРОС:\n\"")
                .append(editeIndexElement).append(". ")
                .append(questioner.getQuestions().get(editeIndexElement).getQuestionText()).append("\"\n");

        int i = 0;
        builder.append("\nСписок вопросов: \n");
        for (Question q : questioner.getQuestions()) {
            builder.append(i).append(". \"").append(q.getQuestionText()).append("\"");
            builder.append(", ред: ").append(StringButtons.EDIT_QUESTION.getValue()).append(i).append(" \n");

            for (int j = 0; j < questioner.getQuestions().get(i).getOptions().size(); j++) {
                Option option = questioner.getQuestions().get(i).getOptions().get(j);
                builder.append("Ответ: \"").append(option.getOptionText()).append("\", балл: ").append(option.getScore()).append(", ");
                builder.append("ред: ").append(StringButtons.EDIT_OPTION.getValue())
                        .append(i).append("_").append(j).append(" \n");
            }
            builder.append("\n\n");
            i++;
        }
    }

    public SendMessage getBackToMainCreateQuestionParametersAfterNoAddPic(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboards.getToCreatePictureChooseMenu());
    }
    public SendMessage getChangeParamAndBackToCreateQuestionParameters(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboards.getBackToMainCreateQuestionParametersButton());
    }
    public SendMessage getChangeParamAndBackToMainMenuCreateQuestion(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboards.getSingleBackButtonToMainCreateMenu());
    }




    private String mainMenuCreateQuestionText(Questioner questioner) {
        StringBuilder builder = new StringBuilder();
        builder.append(("⭐\uFE0Fрежим создания нового теста⭐\uFE0F\n\n").toUpperCase())
                .append(("\uD83C\uDF00Проверьте информацию о тесте:\n").toUpperCase())
                .append("Название: ").append(questioner.getName() == null ? "Нет названия" : questioner.getName()).append("\n")
                .append("Максимум вопросов: 30\n")
                .append("Сейчас вопросов: ").append(questioner.getQuestions().size()).append("\n")
                .append("Сейчас результатов: ").append(questioner.getResults().size()).append("\n");
        return builder.toString();
    }
    public SendPhoto getMainCreateQuestionMenuSendPhoto(Long chatId, Questioner questioner) {
        return getSendPhotoMsgObject(chatId, mainMenuCreateQuestionText(questioner),
                keyboards.getMainMenuCreateQuestionsAndOtherParameters(), questioner.getFilePath());
    }
    public SendMessage getMainCreateQuestionMenuSendMessage(Long chatId, Questioner questioner) {
        return getSendMsgObject(chatId, mainMenuCreateQuestionText(questioner), keyboards.getMainMenuCreateQuestionsAndOtherParameters());
    }




    private String getTextForCreateResult(Questioner questioner, int lastResultIndex) {
        StringBuilder builder = new StringBuilder();
        builder.append("\nВведите сначала текст вашего результата, затем верхнию и нижнию границы.")
                .append("Результат будет расчитан по баллам которые есть у каждого варианта ответа.")
                .append("Данный результат будет засчитан если сумма баллов по тесту будет в пределах границ.\n\n");
        try {
            builder.append("\nСейчас результатов: ").append(questioner.getResultListLastElementNumber() + 1)
                    .append("\nТЕСТ: ").append(questioner.getName() != null ? questioner.getName() : "название не введено").append("\n");

            for (int i = 0; i < questioner.getResults().size(); i++) {
                builder.append(i).append(". ").append(questioner.getResults().get(i).getResultText()).append("\n")
                        .append("   Нижняя граница:").append(questioner.getResults().get(i).getLowRate()).append("\n")
                        .append("   Верхняя граница: ").append(questioner.getResults().get(i).getTopRate()).append("\n")
                        .append("Редактировать: ").append(StringButtons.EDIT_RESULT_BY_COMMAND.getValue()).append(i)
                        .append("\n\n");
            }

            builder.append("\nРЕДАКТИРУЕМ РЕЗУЛЬТАТ: ").append(questioner.getResults().get(lastResultIndex).getResultText());
        } catch (Exception e) {
            builder.append("\n\nЕще не добавлено ни одного результата.");
        }
        return builder.toString();
    }
    public SendMessage getMessageForCreateResult(Long chatId, Questioner questioner, int lastResultIndex) {
        return getSendMsgObject(chatId, getTextForCreateResult(questioner, lastResultIndex), keyboards.getMainKeyboardForCreateResults());
    }
    public SendPhoto getMessageForCreateResultAndSendPhoto(Long chatId, Questioner questioner, int lastResultIndex) {
        String fileId = questioner.getResults().get(lastResultIndex).getFilePath();
        return getSendPhotoMsgObject(chatId, getTextForCreateResult(questioner, lastResultIndex),
                keyboards.getMainKeyboardForCreateResults(), fileId);
    }




    private String getTextForUpdateOptionsParameters(Questioner questioner, int lastIndexQuestion, int lastIndexOption) {
        StringBuilder builder = new StringBuilder();
        String score;
        String answerText;
        try {
            score = String.valueOf(questioner.getQuestions().get(lastIndexQuestion).getOptions().get(lastIndexOption).getScore());
        } catch (Exception e) {
            score = "Балл не указан";
        }
        try {
            answerText = questioner.getQuestions().get(lastIndexQuestion).getOptions().get(lastIndexOption).getOptionText();
        } catch (Exception e) {
            answerText = "Ответ не указан";
        }
        builder.append("Вопрос: ").append(questioner.getQuestions().get(lastIndexQuestion).getQuestionText()).append("\n")
                .append("Проверьте информацию о варианте ответа:\n")
                .append("Вариант ответа: ").append(answerText).append("\n")
                .append("Баллы: ").append(score).append("\n");
        return builder.toString();
    }
    public SendMessage getUpdateOptionsParameters(Long chatId, Questioner questioner, int lastIndexQuestion, int lastIndexOption) {
        return getSendMsgObject(chatId,
                getTextForUpdateOptionsParameters(questioner, lastIndexQuestion, lastIndexOption),
                keyboards.getCreateOptionsParameters());
    }
    public SendMessage getUpdateOptionsParametersByUserIndex(Long chatId, Questioner questioner, String userTextData) {
        int lastIndexQuestion = Integer.parseInt(userTextData.split("_")[1]);
        int lastIndexOption = Integer.parseInt(userTextData.split("_")[2]);
        String data = lastIndexQuestion + "_" + lastIndexOption;

        return getSendMsgObject(chatId, getTextForUpdateOptionsParameters(questioner, lastIndexQuestion, lastIndexOption),
                keyboards.getEditeOptionsByUserIndex(data));
    }




    public SendMessage getListQuestionOptions(Long chatId, Questioner questioner) {
        StringBuilder builder = new StringBuilder();
        int lastIndexQuestion = questioner.getQuestionsListLastElementIndex();
        builder.append("Список всех ответов на вопрос: \"")
                .append(questioner.getQuestions().get(lastIndexQuestion).getQuestionText()).append("\"");

        if (questioner.getQuestions().isEmpty()) {
            builder.setLength(0);
            builder.append("\nНи одного ответа на вопрос не добавлено");
            return getSendMsgObject(chatId, builder.toString(), keyboards.getCreateQuestionMenuKeyboard());
        }

        for (int i = 0; i < questioner.getQuestions().get(lastIndexQuestion).getOptions().size(); i++) {
            String answerText = questioner.getQuestions().get(lastIndexQuestion).getOptions().get(i).getOptionText();
            Integer score;
            try {
                score = questioner.getQuestions().get(lastIndexQuestion).getOptions().get(i).getScore();
            } catch (Exception e) {
                score = null;
            }
            builder.append(i).append(". ").append(answerText).append(", балл: ")
                    .append((score == null ? "Значение не указано" : score)).append("\n");
        }
        return getSendMsgObject(chatId, builder.toString(), keyboards.getCreateQuestionMenuKeyboard());
    }
    public SendMessage getNoOneTest(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboards.getNoOneTestBack());
    }






    public SendMessage getBackFromAddPicToResult(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboards.getBackToMenuAddResult());
    }
    public SendMessage getChangeParamAndBackToCreateResultOption(Long chatId, String text) {
        return getSendMsgObject(chatId, text, keyboards.getBackToMainCreateResultParameters());
    }

    private String getStringForMenuToEditResultByUserIndex(Questioner questioner, int element) {
        return "Редактируем результат для теста: " + questioner.getName() + "\n"
                + "Текст результата: " + questioner.getResults().get(element).getResultText() + "\n"
                + "Верхняя граница: " + questioner.getResults().get(element).getTopRate() + "\n"
                + "Нижняя граница: " + questioner.getResults().get(element).getLowRate() + "\n";
    }
    public SendPhoto getMenuToEditResultByUserIndexWithPhoto(Long chatId,Questioner questioner,  int element) {
        String fileId = questioner.getResults().get(element).getFilePath();
        return getSendPhotoMsgObject(chatId,
                getStringForMenuToEditResultByUserIndex(questioner, element),
                keyboards.getEditResultByUserElementCommand(element), fileId);
    }
    public SendMessage getMenuToEditResultByUserIndex(Long chatId,Questioner questioner,  int element) {
        return getSendMsgObject(chatId,
                getStringForMenuToEditResultByUserIndex(questioner, element),
                keyboards.getEditResultByUserElementCommand(element));
    }


    public SendMessage getChangeParamMsgEmptyKeyboard(Long chatId, String text) {
        return getSendMsgObject(chatId, text, null);
    }

    public SendMessage youWereBlocked(Long chatId) {
        String text = "Администратор заблокировал Вас.";
        return getSendMsgObject(chatId, text, null);
    }

    public SendMessage getHelpMsg(Long chatId) {
        return getSendMsgObject(chatId,
                "⭐ Добавьте бота в группу и выдайте администратора, что бы полноценно проходить тесты группой человек",
                keyboards.getNoOneTestBack());
    }
}