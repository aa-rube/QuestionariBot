package app.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Value("${bot.username}")
    String botUserName;

    @Value("${bot.token}")
    String token;

    @Value("${super.user}")
    Long superUserChatId;

    @Value("${bot.url}")
    String botUrl;

    @Value("${moderator}")
    Long moderatorId;

    public Long getModeratorId() {
        return moderatorId;
    }

    public String getBotUserName() {
        return botUserName;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getSuperUserChatId() {
        return superUserChatId;
    }

    public void setSuperUserChatId(long superUserChatId) {
        this.superUserChatId = superUserChatId;
    }

    public String getBotUrl() {
        return botUrl;
    }

    public void setBotUrl(String botUrl) {
        this.botUrl = botUrl;
    }

    public void setSuperUserChatId(Long superUserChatId) {
        this.superUserChatId = superUserChatId;
    }

    public long getModeratorChatId() {
        return moderatorId;
    }

    public void setModeratorId(Long moderatorId) {
        this.moderatorId = moderatorId;
    }
}