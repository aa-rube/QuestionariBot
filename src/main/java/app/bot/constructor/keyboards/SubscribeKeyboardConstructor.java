package app.bot.constructor.keyboards;

import app.questionary.model.Partner;
import app.questionary.repository.MongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SubscribeKeyboardConstructor {
    @Autowired
    private MongoService mongo;
    public InlineKeyboardMarkup getSubscribeButtons() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        for (Partner p : mongo.getAllPartners()) {
            List<InlineKeyboardButton> list = new ArrayList<>();
            InlineKeyboardButton subscribe = new InlineKeyboardButton();

            subscribe.setText(p.getGroupUserName());
            subscribe.setUrl(p.getGroupUrl());
            list.add(subscribe);
            keyboardMatrix.add(list);
        }

        List<InlineKeyboardButton> theNext = new ArrayList<>();
        InlineKeyboardButton toTheNextStep = new InlineKeyboardButton();
        toTheNextStep.setText("Continue");
        toTheNextStep.setCallbackData("/check");
        theNext.add(toTheNextStep);


        keyboardMatrix.add(theNext);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
