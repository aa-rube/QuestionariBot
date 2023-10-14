package app.bot.model;

import java.time.LocalDateTime;

public class BotUser {
    private String name;
    private String userName;
    private Long chatId;
    private LocalDateTime dateReg;
    private String phone;
    private boolean subscribe;

    private boolean isBlock;

    public BotUser() {
    }

    public BotUser(String name, String userName, Long chatId, LocalDateTime dateReg, String phone, boolean subscribe, boolean isBlock) {
        this.name = name;
        this.userName = userName;
        this.chatId = chatId;
        this.dateReg = dateReg;
        this.phone = phone;
        this.subscribe = subscribe;
        this.isBlock = isBlock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getDateReg() {
        return dateReg;
    }

    public void setDateReg(LocalDateTime dateReg) {
        this.dateReg = dateReg;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }


}
