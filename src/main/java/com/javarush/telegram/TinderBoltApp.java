package com.javarush.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "my_tbh_tinder_bot"; //TODO: додай ім'я бота в лапках
    public static final String TELEGRAM_BOT_TOKEN = "7366650902:AAFQCZ9-3hfvBGS0p3hylXC0ZFhJTIwpVM4"; //TODO: додай токен бота в лапках
    public static final String OPEN_AI_TOKEN = "gpt:write_your_secret_token";
    public DialogMode mode = DialogMode.MAIN;
    public ChatGPTService gptService = new ChatGPTService(OPEN_AI_TOKEN);
    private List<String> chat;
    private UserInfo myInfo;
    private UserInfo personInfo;
    private int questionNumber;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        String message = getMessageText();
        switch (message) {
            case "/start" -> {
                mode = DialogMode.MAIN;
                showMainMenu(
                        "головне меню бота", "/start",
                        "генерація Tinder-профілю \uD83D\uDE0E", "/profile",
                        "повідомлення для знайомства \uD83E\uDD70", "/opener",
                        "листування від вашого імені \uD83D\uDE08", "/message",
                        "листування із зірками \uD83D\uDD25", "/date",
                        "поставити запитання чату GPT \uD83E\uDDE0", "/gpt"
                );
                sendPhotoMessage("main");
                String menu = loadMessage("main");
                sendTextMessage(menu);
                return;
            }
            case "/gpt" -> {
                mode = DialogMode.GPT;
                sendPhotoMessage("gpt");
                String gptMessage = loadMessage("gpt");
                sendTextMessage(gptMessage);
                return;
            }
            case "/date" -> {
                mode = DialogMode.DATE;
                sendPhotoMessage("date");
                String dateMessage = loadMessage("date");
                sendTextButtonsMessage(dateMessage,
                        "Аріана Гранде 🔥", "date_grande",
                        "Марго Роббі 🔥🔥", "date_robbie",
                        "Зендея 🔥🔥🔥", "date_zendaya",
                        "Райан Гослінг 😎", "date_gosling",
                        "Том Харді 😎😎", "date_hardy");
                return;
            }
            case "/message" -> {
                mode = DialogMode.MESSAGE;
                sendPhotoMessage("message");
                String gptMessageHelper = loadMessage("message");
                sendTextButtonsMessage(gptMessageHelper,
                        "Наступне повідомлення", "message_next",
                        "Запросити на побачення", "message_date");
                chat = new ArrayList<>();
                return;
            }
            case "/profile" -> {
                mode = DialogMode.PROFILE;
                sendPhotoMessage("profile");
                String profileMessage = loadMessage("profile");
                sendTextMessage(profileMessage);
                myInfo = new UserInfo();
                questionNumber = 1;
                sendTextMessage("Введіть ім'я:");
                return;
            }
            case "/opener" -> {
                mode = DialogMode.OPENER;
                sendPhotoMessage("opener");
                String profileMessage = loadMessage("opener");
                sendTextMessage(profileMessage);
                personInfo = new UserInfo();
                questionNumber = 1;
                sendTextMessage("Введіть ім'я:");
                return;
            }
        }


        switch (mode) {
            case GPT -> {
                String prompt = loadPrompt("gpt");
                Message msg = sendTextMessage("почекай...");
                String answer = gptService.sendMessage(prompt, message);
                updateTextMessage(msg, answer);
            }
            case DATE -> {
                String query = getCallbackQueryButtonKey();
                if (query.startsWith("date_")) {
                    sendPhotoMessage(query);
                    String prompt = loadPrompt(query);
                    gptService.setPrompt(prompt);
                    return;
                }
                Message msg = sendTextMessage("почекай...");
                String answer = gptService.addMessage(message);
                updateTextMessage(msg, answer);
            }
            case MESSAGE -> {
                String query = getCallbackQueryButtonKey();
                if (query.startsWith("message_")) {
                    String prompt = loadPrompt(query);
                    String history = String.join("/n/n", chat);
                    Message msg = sendTextMessage("почекай...");
                    String answer = gptService.sendMessage(prompt, history);
                    updateTextMessage(msg, answer);
                }
                chat.add(message);
            }
            case PROFILE -> {
                if (questionNumber <= 6) {
                    askQuestion(message, myInfo, "profile");
                }
            }
            case OPENER -> {
                if (questionNumber <= 6) {
                    askQuestion(message, personInfo, "opener");
                }
            }
        }
    }


    private void askQuestion(String message, UserInfo user, String profileName) {
        switch (questionNumber) {
            case 1 -> {
                user.name = message;
                questionNumber = 2;
                sendTextMessage("Введіть вік:");
                return;
            }
            case 2 -> {
                user.age = message;
                questionNumber = 3;
                sendTextMessage("Введіть місто:");
                return;
            }
            case 3 -> {
                user.city = message;
                questionNumber = 4;
                sendTextMessage("Введіть професію:");
                return;
            }
            case 4 -> {
                user.occupation = message;
                questionNumber = 5;
                sendTextMessage("Введіть хоббі:");
                return;
            }
            case 5 -> {
                user.hobby = message;
                questionNumber = 6;
                sendTextMessage("Введіть цілі знайомства?");
                return;
            }
            case 6 -> {
                user.goals = message;
                String prompt = loadPrompt(profileName);
                Message msg = sendTextMessage("почекай.чат думає...");
                String answer = gptService.sendMessage(prompt, user.toString());
                updateTextMessage(msg, answer);
                return;
            }
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
