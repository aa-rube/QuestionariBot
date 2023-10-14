package app.questionary.repository;

import app.bot.model.BotUser;
import app.questionary.model.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MongoService {
    MongoDBConnector mongoDBConnector = new MongoDBConnector("localhost", 27017, "testBot");
    public void saveQuestioner(Questioner questioner) {
        MongoCollection<Document> questionerCollection = mongoDBConnector.getQuestionerCollection();
        Document doc = new Document("chatId", questioner.getChatId())
                .append("name", questioner.getName())
                .append("filePath", questioner.getFilePath())
                .append("questionerId", questioner.getQuestionerId())
                .append("passCount", questioner.getPassCount())
                .append("userStarsCount", questioner.getUserStarsCount())
                .append("passWithoutStars", questioner.getPassWithoutStars());

        List<Document> questionDocs = new ArrayList<>();
        for (Question question : questioner.getQuestions()) {
            Document questionDoc = new Document("questionText", question.getQuestionText())
                    .append("filePath", question.getFilePath());

            List<Document> optionDocs = new ArrayList<>();
            for (Option option : question.getOptions()) {
                Document optionDoc = new Document("optionText", option.getOptionText())
                        .append("score", option.getScore());
                optionDocs.add(optionDoc);
            }
            questionDoc.append("options", optionDocs);

            questionDocs.add(questionDoc);
        }
        doc.append("questions", questionDocs);

        List<Document> resultDocs = new ArrayList<>();
        for (Result result : questioner.getResults()) {
            Document resultDoc = new Document("resultText", result.getResultText())
                    .append("topRate", result.getTopRate())
                    .append("lowRate", result.getLowRate())
                    .append("filePath", result.getFilePath());

            resultDocs.add(resultDoc);
        }
        doc.append("results", resultDocs);

        questionerCollection.insertOne(doc);
    }
    public Questioner getQuestionerByQuestionerId(String questionerId) {
        MongoCollection<Document> questionerCollection = mongoDBConnector.getQuestionerCollection();
        Document document = questionerCollection.find(Filters.eq("questionerId", questionerId)).first();

        if (document == null) {
            return null;
        }

        Questioner questioner = new Questioner();
        questioner.setChatId(document.getLong("chatId"));
        questioner.setName(document.getString("name"));
        questioner.setFilePath(document.getString("filePath"));
        questioner.setQuestionerId(document.getString("questionerId"));
        questioner.setPassCount(document.getInteger("passCount"));
        questioner.setUserStarsCount(document.getInteger("userStarsCount"));
        questioner.setPassWithoutStars(document.getInteger("passWithoutStars"));

        List<Document> questionDocs = (List<Document>) document.get("questions");
        List<Question> questions = new ArrayList<>();

        for (Document questionDoc : questionDocs) {
            Question question = new Question(questionDoc.getString("questionText"));
            question.setFilePath(questionDoc.getString("filePath"));

            List<Option> options = new ArrayList<>();
            List<Document> optionDocs = (List<Document>) questionDoc.get("options");

            for (Document optionDoc : optionDocs) {
                Option option = new Option(optionDoc.getString("optionText"));
                option.setScore(optionDoc.getInteger("score"));
                options.add(option);
            }

            question.setOptions(options);
            questions.add(question);
        }

        List<Document> resultDocs = (List<Document>) document.get("results");
        List<Result> results = new ArrayList<>();

        for (Document resultDoc : resultDocs) {
            Result result = new Result(resultDoc.getString("resultText"));
            result.setTopRate(resultDoc.getInteger("topRate"));
            result.setLowRate(resultDoc.getInteger("lowRate"));
            result.setFilePath(resultDoc.getString("filePath"));
            results.add(result);
        }

        questioner.setQuestions(questions);
        questioner.setResults(results);

        return questioner;
    }
    public void deleteQuestionerByQuestionerId(String questionerId) {
        MongoCollection<Document> questionerCollection = mongoDBConnector.getQuestionerCollection();
        DeleteResult result = questionerCollection.deleteOne(Filters.eq("questionerId", questionerId));
    }
    public List<Questioner> getQuestionersByChatId(Long chatId) {
        List<Questioner> questioners = new ArrayList<>();
        MongoCollection<Document> questionerCollection = mongoDBConnector.getQuestionerCollection();
        FindIterable<Document> documents = questionerCollection.find(Filters.eq("chatId", chatId));
        for (Document document : documents) {
            Questioner questioner = new Questioner();
            questioner.setChatId(document.getLong("chatId"));
            questioner.setName(document.getString("name"));
            questioner.setFilePath(document.getString("filePath"));
            questioner.setQuestionerId(document.getString("questionerId"));
            questioner.setPassCount(document.getInteger("passCount"));
            questioner.setPassCount(document.getInteger("userStarsCount"));

            List<Question> questions = new ArrayList<>();
            List<Document> questionDocs = (List<Document>) document.get("questions");

            for (Document questionDoc : questionDocs) {
                Question question = new Question(questionDoc.getString("questionText"));
                question.setFilePath(questionDoc.getString("filePath"));

                List<Option> options = new ArrayList<>();
                List<Document> optionDocs = (List<Document>) questionDoc.get("options");
                for (Document optionDoc : optionDocs) {
                    Option option = new Option(optionDoc.getString("optionText"));
                    option.setScore(optionDoc.getInteger("score"));
                    options.add(option);
                }
                question.setOptions(options);
                questions.add(question);
            }
            questioner.setQuestions(questions);

            List<Result> results = new ArrayList<>();
            List<Document> resultDocs = (List<Document>) document.get("results");
            for (Document resultDoc : resultDocs) {
                Result result = new Result(resultDoc.getString("resultText"));
                result.setTopRate(resultDoc.getInteger("topRate"));
                result.setLowRate(resultDoc.getInteger("lowRate"));
                result.setFilePath(resultDoc.getString("filePath"));
                results.add(result);
            }
            questioner.setResults(results);

            questioners.add(questioner);
        }
        return questioners;
    }
    public List<Questioner> getAllQuestioners() {
        MongoCollection<Document> questionerCollection = mongoDBConnector.getQuestionerCollection();
        MongoCursor<Document> cursor = questionerCollection.find().iterator();

        List<Questioner> questioners = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();

            Questioner questioner = new Questioner();
            questioner.setChatId(doc.getLong("chatId"));
            questioner.setName(doc.getString("name"));
            questioner.setFilePath(doc.getString("filePath"));
            questioner.setQuestionerId(doc.getString("questionerId"));
            questioner.setPassCount(doc.getInteger("passCount"));
            questioner.setUserStarsCount(doc.getInteger("userStarsCount"));
            questioner.setPassWithoutStars(doc.getInteger("passWithoutStars"));

            List<Document> questionDocs = (List<Document>) doc.get("questions");
            List<Question> questions = new ArrayList<>();

            for (Document questionDoc : questionDocs) {
                Question question = new Question(questionDoc.getString("questionText"));
                question.setFilePath(questionDoc.getString("filePath"));

                List<Option> options = new ArrayList<>();
                List<Document> optionDocs = (List<Document>) questionDoc.get("options");

                for (Document optionDoc : optionDocs) {
                    Option option = new Option(optionDoc.getString("optionText"));
                    option.setScore(optionDoc.getInteger("score"));
                    options.add(option);
                }

                question.setOptions(options);
                questions.add(question);
            }

            List<Document> resultDocs = (List<Document>) doc.get("results");
            List<Result> results = new ArrayList<>();

            for (Document resultDoc : resultDocs) {
                Result result = new Result(resultDoc.getString("resultText"));
                result.setTopRate(resultDoc.getInteger("topRate"));
                result.setLowRate(resultDoc.getInteger("lowRate"));
                result.setFilePath(resultDoc.getString("filePath"));
                results.add(result);
            }

            questioner.setQuestions(questions);
            questioner.setResults(results);

            questioners.add(questioner);
        }
        cursor.close();

        return questioners;
    }

    public void saveBotUser(BotUser botUser) {
        MongoCollection<Document> botUserCollection = mongoDBConnector.getBotUserCollection();
        Document existingDoc = botUserCollection.find(Filters.eq("chatId", botUser.getChatId())).first();

        if (existingDoc == null) {
            Document doc = new Document("name", botUser.getName())
                    .append("userName", botUser.getUserName())
                    .append("chatId", botUser.getChatId())
                    .append("dateReg", (botUser.getDateReg() != null) ? botUser.getDateReg().toString() : null)
                    .append("isBlock", botUser.isBlock())
                    .append("subscribe", botUser.isSubscribe());

            botUserCollection.insertOne(doc);
        }
    }
    public BotUser getBotUser(Long chatId) {
        MongoCollection<Document> botUserCollection = mongoDBConnector.getBotUserCollection();
        Document document = botUserCollection.find(Filters.eq("chatId", chatId)).first();

        if (document == null) {
            return null;
        }

        BotUser botUser = new BotUser();
        botUser.setName(document.getString("name"));
        botUser.setUserName(document.getString("userName"));
        botUser.setChatId(document.getLong("chatId"));
        String dateRegStr = document.getString("dateReg");
        botUser.setDateReg((dateRegStr != null) ? LocalDateTime.parse(dateRegStr) : null);
        botUser.setPhone(document.getString("phone"));
        botUser.setBlock(document.getBoolean("isBlock", false));
        botUser.setSubscribe(document.getBoolean("subscribe", false));

        return botUser;
    }

    public BotUser getBotUserByUserName(String userName) {
        MongoCollection<Document> botUserCollection = mongoDBConnector.getBotUserCollection();
        Document document = botUserCollection.find(Filters.eq("userName", userName)).first();

        if (document == null) {
            return null;
        }

        BotUser botUser = new BotUser();
        botUser.setName(document.getString("name"));
        botUser.setUserName(document.getString("userName"));
        botUser.setChatId(document.getLong("chatId"));
        String dateRegStr = document.getString("dateReg");
        botUser.setDateReg((dateRegStr != null) ? LocalDateTime.parse(dateRegStr) : null);
        botUser.setPhone(document.getString("phone"));
        botUser.setBlock(document.getBoolean("isBlock", false));
        botUser.setSubscribe(document.getBoolean("subscribe", false));

        return botUser;
    }

    public List<BotUser> getAllBotUsers() {
        MongoCollection<Document> botUserCollection = mongoDBConnector.getBotUserCollection();
        MongoCursor<Document> cursor = botUserCollection.find().iterator();

        List<BotUser> botUsers = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String name = doc.getString("name");
            String userName = doc.getString("userName");
            Long chatId = doc.getLong("chatId");
            String dateRegStr = doc.getString("dateReg");
            LocalDateTime dateReg = (dateRegStr != null) ? LocalDateTime.parse(dateRegStr) : null;
            String phone = doc.getString("phone");
            boolean isBlock = doc.getBoolean("isBlock", false);

            BotUser botUser = new BotUser();
            botUser.setChatId(chatId);
            botUser.setName(name);
            botUser.setUserName(userName);
            botUser.setDateReg(dateReg);
            botUser.setPhone(phone);
            botUser.setBlock(isBlock);

            botUsers.add(botUser);
        }
        cursor.close();

        return botUsers;
    }
    public List<BotUser> getBotUsersByRegistrationDate(LocalDate startDate, LocalDate endDate) {
        MongoCollection<Document> botUserCollection = mongoDBConnector.getBotUserCollection();

        Document dateFilter = new Document("$gte", startDate.toString()).append("$lte", endDate.toString());
        Document query = new Document("dateReg", dateFilter);

        MongoCursor<Document> cursor = botUserCollection.find(query).iterator();

        List<BotUser> users = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String name = doc.getString("name");
            String userName = doc.getString("userName");
            Long chatId = doc.getLong("chatId");
            boolean isBlock = doc.getBoolean("isBlock", false);
            String dateRegStr = doc.getString("dateReg");
            LocalDateTime dateReg = (dateRegStr != null) ? LocalDateTime.parse(dateRegStr) : null;
            String phone = doc.getString("phone");
            Boolean subscribe = doc.getBoolean("subscribe");
            boolean isSubscribed = (subscribe != null) ? subscribe : false;

            BotUser user = new BotUser(name, userName, chatId, dateReg, phone, isSubscribed, isBlock);

            users.add(user);
        }
        cursor.close();

        return users;
    }
    public void deleteUserByChatId(Long chatId) {
        MongoCollection<Document> botUserCollection = mongoDBConnector.getBotUserCollection();
        botUserCollection.deleteMany(new Document("chatId", chatId));
    }





    public void savePartner(Partner partner) {
        MongoCollection<Document> partnersCollection = mongoDBConnector.getPartnersCollection();

        Document doc = new Document("groupUserName", partner.getGroupUserName())
                .append("groupUrl", partner.getGroupUrl())
                .append("expiredPartnerShipTime", partner.getExpiredPartnerShipTime().toString());
        partnersCollection.insertOne(doc);
    }
    public List<Partner> getAllPartners() {
        MongoCollection<Document> partnerCollection = mongoDBConnector.getPartnersCollection();
        MongoCursor<Document> cursor = partnerCollection.find().iterator();

        List<Partner> partners = new ArrayList<>();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String groupUserName = doc.getString("groupUserName");
            String groupUrl = doc.getString("groupUrl");
            LocalDate expiredPartnerShipTime = LocalDate.parse(doc.getString("expiredPartnerShipTime"));

            Partner partner = new Partner(groupUserName, groupUrl, expiredPartnerShipTime);
            partners.add(partner);
        }
        cursor.close();

        return partners;
    }
    public void deletePartnerByGroupUserName(String groupUserName) {
        MongoCollection<Document> partnerCollection = mongoDBConnector.getPartnersCollection();
        partnerCollection.deleteMany(new Document("groupUserName", groupUserName));
    }
    public List<Partner> getAllExpiredPartners() {
        List<Partner> expiredPartners = new ArrayList<>();
        MongoCollection<Document> partnerCollection = mongoDBConnector.getPartnersCollection();
        MongoCursor<Document> cursor = partnerCollection.find().iterator();

        LocalDate currentDate = LocalDate.now();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            String groupUserName = doc.getString("groupUserName");
            String groupUrl = doc.getString("groupUrl");
            LocalDate expiredPartnerShipTime = LocalDate.parse(doc.getString("expiredPartnerShipTime"));

            if (expiredPartnerShipTime.isBefore(currentDate)) {
                Partner partner = new Partner(groupUserName, groupUrl, expiredPartnerShipTime);
                expiredPartners.add(partner);
            }
        }
        cursor.close();

        return expiredPartners;
    }
}