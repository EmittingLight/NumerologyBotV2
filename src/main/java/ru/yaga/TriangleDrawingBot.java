package ru.yaga;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriangleDrawingBot extends TelegramLongPollingBot {

    private static final String USERS_FILE_PATH = "users.txt";
    private static final String GREETING_FILE_PATH = "greeting.txt";
    private static final String REMINDER_FILE_PATH = "reminder.txt";
    private static final String SUPPORT_FILE_PATH = "support.txt";
    private static final String DESCRIPTION_FILE_PATH = "alterEgo.txt";
    private static final String PERSONALITY_DESCRIPTION_FILE_PATH = "personalityCenter.txt";
    private static final String DESTINATION_CENTER_FILE_PATH = "destinationCenter.txt";
    private static final String CENTER_FAMILY_PROGRAMS_FILE_PATH = "centerFamilyPrograms.txt";
    private static final String KEY_DESTINY_REALIZATION_FILE_PATH = "keydestinyrealization.txt";
    private static final String TALENT_KEY_REALIZATION_FILE_PATH = "talentKey.txt";
    private static final String MASKS_RED_DESCRIPTION_FILE_PATH = "masksreddescription.txt";
    private static final String MASKS_GREEN_DESCRIPTION_FILE_PATH = "masksgreendescription.txt";
    private static final String MASKS_PURPLE_DESCRIPTION_FILE_PATH = "maskspurpedescription.txt";
    private static final String SHADOW1_FILE_PATH = "shadow1.txt";
    private static final String SHADOW2_FILE_PATH = "shadow2.txt";
    private static final String SHADOW3_FILE_PATH = "shadow3.txt";
    private static final String TYPAGE_FILE_PATH = "typage.txt";
    private static final String ASSEMBLAGE_POINT_FILE_PATH = "assemblagePoint.txt";
    private static final String GREETING_MESSAGE = "Добро пожаловать в NumerologyBot!";
    private static final String IMAGE_PATH = "pic1.jpg";
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;
    private int alterEgo;
    private int centerPersonality;
    private int centerDestiny;
    private int centerFamilyPrograms;
    private int destinyKey;
    private int talentKey;
    private int maskLoveScenario;
    private int maskKarmicTask;
    private int maskLoveTransmission;
    private int maskFinancialHealing;
    private int maskTalentRealization;
    private int maskScenarioTransmission;
    private int maskHealingLoveScenario;
    private int maskKarmicDestiny;
    private int maskHeartLine;
    private int shadow1;
    private int shadow2;
    private int shadow3;
    private int typage;
    private Map<Long, String> registrationSteps = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "NumerologyBot";
    }

    @Override
    public String getBotToken() {
        return "REMOVED_TOKEN";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                if (messageText.matches("\\d{4}")) {
                    try {
                        selectedYear = Integer.parseInt(messageText);
                        if (selectedYear < 1000 || selectedYear > 9999) {
                            promptYearInput(chatId, "Введите корректный год в формате: YYYY (например, 1987).");
                        } else {
                            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
                            sendImage(chatId, image);
                            showResultButtons(chatId);
                        }
                    } catch (NumberFormatException e) {
                        promptYearInput(chatId, "Введите корректный год в формате: YYYY (например, 1987).");
                    } catch (IOException | TelegramApiException e) {
                        e.printStackTrace();
                        sendMessage(chatId, "Произошла ошибка при обработке вашей команды. Пожалуйста, попробуйте снова.");
                    }
                } else if (messageText.equals("/start")) {
                    handleStartCommand(chatId);
                } else if (registrationSteps.containsKey(chatId)) {
                    handleRegistrationSteps(chatId, messageText);
                } else {
                    sendMessage(chatId, "Введите корректный год в формате: YYYY (например, 1987).");
                }
            } else if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();

                if (callbackData.startsWith("date:day:")) {
                    selectedDay = Integer.parseInt(callbackData.split(":")[2]);
                    showMonthPicker(chatId);
                } else if (callbackData.startsWith("date:month:")) {
                    selectedMonth = Integer.parseInt(callbackData.split(":")[2]);
                    promptYearInput(chatId, "Теперь введите год в формате: YYYY");
                } else if (callbackData.equals("description")) {
                    showDescriptionButtons(chatId);
                } else if (callbackData.equals("masks_red_description")) {
                    handleMaskRedDescription(chatId);
                } else if (callbackData.equals("masks_green_description")) {
                    handleMaskGreenDescription(chatId);
                } else if (callbackData.equals("masks_purple_description")) {
                    handleMaskPurpleDescription(chatId);
                } else if (callbackData.equals("shadow1_description")) {
                    handleShadowDescription(chatId, shadow1, SHADOW1_FILE_PATH);
                } else if (callbackData.equals("shadow2_description")) {
                    handleShadowDescription(chatId, shadow2, SHADOW2_FILE_PATH);
                } else if (callbackData.equals("shadow3_description")) {
                    handleShadowDescription(chatId, shadow3, SHADOW3_FILE_PATH);
                } else if (callbackData.equals("typage_description")) {
                    handleTypageDescription(chatId);
                } else if (callbackData.equals("assembly_point_description")) {
                    handleAssemblyPointDescription(chatId);
                } else {
                    switch (callbackData) {
                        case "register":
                            handleStartCommand(chatId);
                            break;
                        case "subscribe":
                            showSubscriptionOptions(chatId);
                            break;
                        case "calculator":
                            showDayPicker(chatId);
                            break;
                        case "reminder":
                            sendReminder(chatId);
                            break;
                        case "support":
                            sendSupport(chatId);
                            break;
                        case "new_date":
                            showDayPicker(chatId);
                            break;
                        case "alter_ego_description":
                            sendDescription(chatId, alterEgo, DESCRIPTION_FILE_PATH, "Альтер-Эго");
                            break;
                        case "personality_description":
                            sendDescription(chatId, centerPersonality, PERSONALITY_DESCRIPTION_FILE_PATH, "Центр Личности");
                            break;
                        case "center_destiny_description":
                            sendDescription(chatId, centerDestiny, DESTINATION_CENTER_FILE_PATH, "Центр Предназначения");
                            break;
                        case "center_family_programs_description":
                            sendDescription(chatId, centerFamilyPrograms, CENTER_FAMILY_PROGRAMS_FILE_PATH, "Центр Родовых Программ");
                            break;
                        case "key_destiny_realization":
                            sendKeyDestinyRealizationDescription(chatId, destinyKey, KEY_DESTINY_REALIZATION_FILE_PATH, "Ключ Реализации Предназначения");
                            break;
                        case "key_talent_realization":
                            sendKeyTalentRealizationDescription(chatId, talentKey, TALENT_KEY_REALIZATION_FILE_PATH, "Ключ Реализации Таланта");
                            break;
                        case "back":
                            sendGreeting(chatId);
                            break;
                        case "subscribe_1_month":
                            sendMessage(chatId, "Вы выбрали подписку на 1 месяц за 1500.");
                            break;
                        case "subscribe_3_months":
                            sendMessage(chatId, "Вы выбрали подписку на 3 месяца за 3000.");
                            break;
                        case "subscribe_12_months":
                            sendMessage(chatId, "Вы выбрали подписку на 12 месяцев за 20000.");
                            break;
                        default:
                            sendDescription(chatId, Integer.parseInt(callbackData.split("_")[0]), callbackData.split("_")[1] + ".txt", callbackData);
                            break;
                    }
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(update.getMessage().getChatId(), "Произошла ошибка при обработке вашей команды. Пожалуйста, попробуйте снова.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleAssemblyPointDescription(long chatId) throws TelegramApiException {
        try {
            int assemblyPoint = calculateAssemblyPoint(destinyKey, talentKey, centerFamilyPrograms, centerPersonality, centerDestiny);
            String description = getAssemblyPointDescription(assemblyPoint);

            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            String formattedText = "Точка Сборки для даты рождения " + String.format("%02d.%02d.%d", selectedDay, selectedMonth, selectedYear) + ":\n\n" + description;
            sendMessage(chatId, formattedText);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание для Точки Сборки.");
        }
        showDescriptionButtons(chatId);
    }

    private String getAssemblyPointDescription(int key) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(ASSEMBLAGE_POINT_FILE_PATH));
        for (String line : lines) {
            if (line.startsWith(Integer.toString(key))) {
                return line.substring(line.indexOf(' ') + 1);
            }
        }
        return "Описание не найдено для значения: " + key;
    }

    private void handleStartCommand(long chatId) throws IOException, TelegramApiException {
        if (isUserRegistered(chatId)) {
            sendMessage(chatId, "Вы уже зарегистрированы.");
            sendBackButton(chatId);
        } else {
            sendMessage(chatId, "Для регистрации введите ваше имя:");
            registrationSteps.put(chatId, "name");
        }
    }

    private void handleRegistrationSteps(long chatId, String messageText) throws IOException, TelegramApiException {
        String step = registrationSteps.get(chatId);
        if (step.equals("name")) {
            sendMessage(chatId, "Введите ваш телефон:");
            registrationSteps.put(chatId, "phone:" + messageText);
        } else if (step.startsWith("phone:")) {
            String name = step.split(":")[1];
            String phone = messageText;
            saveUser(chatId, name, phone);
            sendMessage(chatId, "Регистрация прошла успешно.");
            registrationSteps.remove(chatId);
            sendBackButton(chatId);
        }
    }

    private boolean isUserRegistered(long chatId) throws IOException {
        File file = new File(USERS_FILE_PATH);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(Long.toString(chatId))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveUser(long chatId, String name, String phone) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE_PATH, true))) {
            writer.write(chatId + "," + name + "," + phone);
            writer.newLine();
        }
    }

    private void handleMaskRedDescription(long chatId) throws TelegramApiException {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            String maskLoveScenarioDescription = getMaskDescription(maskLoveScenario, MASKS_RED_DESCRIPTION_FILE_PATH);
            String maskKarmicTaskDescription = getMaskDescription(maskKarmicTask, MASKS_RED_DESCRIPTION_FILE_PATH);
            String maskLoveTransmissionDescription = getMaskDescription(maskLoveTransmission, MASKS_RED_DESCRIPTION_FILE_PATH);

            sendFormattedMessage(chatId, "Маски 🔴", maskLoveScenarioDescription);
            sendFormattedMessage(chatId, "Маски 🔴", maskKarmicTaskDescription);
            sendFormattedMessage(chatId, "Маски 🔴", maskLoveTransmissionDescription);

            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание масок.");
        }
    }

    private void handleMaskGreenDescription(long chatId) throws TelegramApiException {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            String maskFinancialHealingDescription = getMaskDescription(maskFinancialHealing, MASKS_GREEN_DESCRIPTION_FILE_PATH);
            String maskTalentRealizationDescription = getMaskDescription(maskTalentRealization, MASKS_GREEN_DESCRIPTION_FILE_PATH);
            String maskScenarioTransmissionDescription = getMaskDescription(maskScenarioTransmission, MASKS_GREEN_DESCRIPTION_FILE_PATH);

            sendFormattedMessage(chatId, "Маски 🟢", maskFinancialHealingDescription);
            sendFormattedMessage(chatId, "Маски 🟢", maskTalentRealizationDescription);
            sendFormattedMessage(chatId, "Маски 🟢", maskScenarioTransmissionDescription);

            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание масок.");
        }
    }

    private void handleMaskPurpleDescription(long chatId) throws TelegramApiException {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            String maskHealingLoveScenarioDescription = getMaskDescription(maskHealingLoveScenario, MASKS_PURPLE_DESCRIPTION_FILE_PATH);
            String maskKarmicDestinyDescription = getMaskDescription(maskKarmicDestiny, MASKS_PURPLE_DESCRIPTION_FILE_PATH);
            String maskHeartLineDescription = getMaskDescription(maskHeartLine, MASKS_PURPLE_DESCRIPTION_FILE_PATH);

            sendFormattedMessage(chatId, "Маски 🟣", maskHealingLoveScenarioDescription);
            sendFormattedMessage(chatId, "Маски 🟣", maskKarmicDestinyDescription);
            sendFormattedMessage(chatId, "Маски 🟣", maskHeartLineDescription);

            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание масок.");
        }
    }

    private void handleShadowDescription(long chatId, int shadow, String filePath) throws TelegramApiException {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            String shadowDescription = getDescription(shadow, filePath);
            sendFormattedMessage(chatId, "Тень", shadowDescription);

            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание для " + filePath);
        }
    }

    private void handleTypageDescription(long chatId) throws TelegramApiException {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            String typageDescription = getDescription(typage, TYPAGE_FILE_PATH);
            sendFormattedMessage(chatId, "Типаж", typageDescription);

            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание типажа.");
        }
    }

    private void sendFormattedMessage(long chatId, String buttonName, String text) throws TelegramApiException {
        String formattedText = buttonName + " для даты рождения " + String.format("%02d.%02d.%d", selectedDay, selectedMonth, selectedYear) + ":\n\n" + text;
        sendLongMessage(chatId, formattedText);
    }

    private void sendLongMessage(long chatId, String text) throws TelegramApiException {
        int maxMessageLength = 4096;
        for (int i = 0; i < text.length(); i += maxMessageLength) {
            int end = Math.min(text.length(), i + maxMessageLength);
            sendMessage(chatId, text.substring(i, end));
        }
    }

    private String getMaskDescription(int maskValue, String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.startsWith(Integer.toString(maskValue))) {
                return line.substring(line.indexOf(' ') + 1);
            }
        }
        return "Описание не найдено для значения: " + maskValue;
    }

    private String getDescription(int key, String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.startsWith(Integer.toString(key))) {
                return line.substring(line.indexOf(' ') + 1);
            }
        }
        return "Описание не найдено.";
    }

    private void sendGreeting(long chatId) {
        try {
            String greeting = new String(Files.readAllBytes(Paths.get(GREETING_FILE_PATH)));
            sendMessage(chatId, greeting);

            sendImage(chatId, new File(IMAGE_PATH));

            sendButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить приветственное сообщение.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось отправить изображение.");
        }
    }

    private void sendReminder(long chatId) {
        try {
            String reminder = new String(Files.readAllBytes(Paths.get(REMINDER_FILE_PATH)));
            sendMessage(chatId, reminder);
            sendBackButton(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить текст памятки.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось отправить кнопку.");
        }
    }

    private void sendSupport(long chatId) {
        try {
            String support = new String(Files.readAllBytes(Paths.get(SUPPORT_FILE_PATH)));
            sendMessage(chatId, support);
            sendBackButton(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить текст обращения в службу поддержки.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось отправить кнопку.");
        }
    }

    private void sendDescription(long chatId, int key, String filePath, String buttonName) {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            String description = getDescription(key, filePath);
            String formattedDescription = formatDescription(description, buttonName);
            sendMessage(chatId, formattedDescription);
            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось отправить кнопку.");
        }
    }

    private void sendKeyDestinyRealizationDescription(long chatId, int key, String filePath, String buttonName) {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            if (isKeyInFile(key, filePath)) {
                String description = getDescription(key, filePath);
                String formattedDescription = formatDescription(description, buttonName);
                sendMessage(chatId, formattedDescription);
            } else {
                sendMessage(chatId, "Описание для данного ключа не найдено.");
            }
            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось отправить кнопку.");
        }
    }

    private void sendKeyTalentRealizationDescription(long chatId, int key, String filePath, String buttonName) {
        try {
            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, selectedYear);
            sendImage(chatId, image);

            if (isKeyInFile(key, filePath)) {
                String description = getDescription(key, filePath);
                String formattedDescription = formatDescription(description, buttonName);
                sendMessage(chatId, formattedDescription);
            } else {
                sendMessage(chatId, "Описание для данного ключа не найдено.");
            }
            showDescriptionButtons(chatId);
        } catch (IOException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось загрузить описание.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            sendMessage(chatId, "Не удалось отправить кнопку.");
        }
    }

    private boolean isKeyInFile(int key, String filePath) throws IOException {
        List<String> keys = Files.readAllLines(Paths.get(filePath));
        for (String line : keys) {
            if (line.startsWith(Integer.toString(key))) {
                return true;
            }
        }
        return false;
    }

    private String formatDescription(String description, String buttonName) {
        StringBuilder formattedDescription = new StringBuilder();
        formattedDescription.append(buttonName)
                .append(" для даты рождения ")
                .append(String.format("%02d.%02d.%d", selectedDay, selectedMonth, selectedYear))
                .append(":\n\n");

        String[] lines = description.split("(?=\\+|--|\\*)");
        for (String line : lines) {
            formattedDescription.append(line.trim()).append("\n\n");
        }

        return formattedDescription.toString();
    }

    private void sendButtons(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(InlineKeyboardButton.builder().text("Зарегистрироваться в боте").callbackData("register").build());
        rowInline1.add(InlineKeyboardButton.builder().text("Купить подписку").callbackData("subscribe").build());

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(InlineKeyboardButton.builder().text("Запуск калькулятора").callbackData("calculator").build());

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(InlineKeyboardButton.builder().text("Памятка").callbackData("reminder").build());

        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        rowInline4.add(InlineKeyboardButton.builder().text("Обращение в службу поддержки").callbackData("support").build());

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите действие:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(message);
    }

    private void showDayPicker(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = 1; j <= 7; j++) {
                int day = i * 7 + j;
                if (day <= 31) {
                    rowInline.add(InlineKeyboardButton.builder()
                            .text(String.valueOf(day))
                            .callbackData("date:day:" + day)
                            .build());
                }
            }
            rowsInline.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите день:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(message);
    }

    private void showMonthPicker(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 1; i <= 12; i += 4) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = i; j < i + 4 && j <= 12; j++) {
                rowInline.add(InlineKeyboardButton.builder()
                        .text(String.valueOf(j))
                        .callbackData("date:month:" + j)
                        .build());
            }
            rowsInline.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите месяц:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(message);
    }

    private void promptYearInput(long chatId, String text) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
        execute(message);
    }

    private void showResultButtons(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(InlineKeyboardButton.builder().text("Ввести новую дату рождения").callbackData("new_date").build());

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(InlineKeyboardButton.builder().text("Получить расшифровку").callbackData("description").build());

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(InlineKeyboardButton.builder().text("Вернуться назад").callbackData("back").build());

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите дальнейшее действие:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(message);
    }

    private void showDescriptionButtons(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(InlineKeyboardButton.builder().text("Альтер-Эго").callbackData("alter_ego_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Центр Личности").callbackData("personality_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Центр Предназначения").callbackData("center_destiny_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Центр Родовых Программ").callbackData("center_family_programs_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Ключ реализации Предназначения").callbackData("key_destiny_realization").build());
        buttons.add(InlineKeyboardButton.builder().text("Ключ реализации Таланта").callbackData("key_talent_realization").build());
        buttons.add(InlineKeyboardButton.builder().text("Тень 1").callbackData("shadow1_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Тень 2").callbackData("shadow2_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Тень 3").callbackData("shadow3_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Маски 🔴").callbackData("masks_red_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Маски 🟢").callbackData("masks_green_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Маски 🟣").callbackData("masks_purple_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Типаж").callbackData("typage_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Точка Сборки").callbackData("assembly_point_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Инкарнационный профиль").callbackData("incarnation_profile_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Ресурс").callbackData("resource_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Квест").callbackData("quest_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Арканы-Планеты").callbackData("arcanes_planets_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Заболевания").callbackData("diseases_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Таланты").callbackData("talents_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Места Силы").callbackData("places_of_power_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Эзотерические способности").callbackData("esoteric_abilities_description").build());
        buttons.add(InlineKeyboardButton.builder().text("Вернуться назад").callbackData("back").build());

        for (int i = 0; i < buttons.size(); i += 3) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                if (i + j < buttons.size()) {
                    rowInline.add(buttons.get(i + j));
                }
            }
            rowsInline.add(rowInline);
        }

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите дальнейшее действие:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(message);
    }

    private void showSubscriptionOptions(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(InlineKeyboardButton.builder().text("Купить подписку на 1 месяц за 1500").callbackData("subscribe_1_month").build());

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(InlineKeyboardButton.builder().text("Купить подписку на 3 месяца за 3000").callbackData("subscribe_3_months").build());

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(InlineKeyboardButton.builder().text("Купить подписку на 12 месяцев за 20000").callbackData("subscribe_12_months").build());

        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        rowInline4.add(InlineKeyboardButton.builder().text("Вернуться назад").callbackData("back").build());

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите подписку:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(message);
    }

    private void sendBackButton(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(InlineKeyboardButton.builder().text("Вернуться назад").callbackData("back").build());

        rowsInline.add(rowInline1);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите действие:")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
        execute(message);
    }

    private void createGreetingFileIfNotExists() throws IOException {
        File file = new File(GREETING_FILE_PATH);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(GREETING_MESSAGE);
            }
        }
    }

    private int applyMinus22Rule(int number) {
        while (number > 22) {
            number -= 22;
        }
        return number;
    }

    private int sumOfDigits(int number) {
        int sum = 0;
        while (number != 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }

    private int calculateResource(int day, int month, int year) {
        int sum = sumOfDigits(day) + sumOfDigits(month) + sumOfDigits(year);
        return applyMinus22Rule(sum);
    }

    private int calculateQuest(int day, int month, int year) {
        int sum = sumOfDigits(day) + sumOfDigits(month) + sumOfDigits(year);
        while (sum > 22) {
            sum = sumOfDigits(sum);
        }
        return sum;
    }

    private int calculateSoulKey(int value1, int value2) {
        int sum = value1 + value2;
        return applyMinus22Rule(sum);
    }

    private int calculateMask(int value1, int value2, int value3) {
        int sum = value1 + value2 + value3;
        return applyMinus22Rule(sum);
    }

    private int calculateShadow(int value1, int value2, int value3) {
        int sum = value1 + value2 + value3;
        return applyMinus22Rule(sum);
    }

    public int calculateTypage(int shadow1, int shadow2, int shadow3) {
        int sum = calculateShadowsSum(shadow1, shadow2, shadow3);
        return applyMinus22Rule(sum);
    }

    private int calculateShadowsSum(int shadow1, int shadow2, int shadow3) {
        return shadow1 + shadow2 + shadow3;
    }

    private int calculateAssemblyPoint(int destinyKey, int talentKey, int centerFamilyPrograms, int centerPersonality, int centerDestiny) {
        int sum = destinyKey + talentKey + centerFamilyPrograms + centerPersonality + centerDestiny;
        return applyMinus22Rule(sum);
    }

    private int calculateIncarnationProfile(int shadow1, int shadow2, int shadow3, int typage) {
        int sum = shadow1 + shadow2 + shadow3 + typage;
        return applyMinus22Rule(sum);
    }

    private BufferedImage drawEsotericImage(int day, int month, int year) throws IOException {
        int width = 1000;
        int height = 1000;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        int alterEgo = applyMinus22Rule(day);
        this.alterEgo = alterEgo;
        int destinyKey = month;
        this.destinyKey = destinyKey;
        int talentKey = sumOfDigits(year);
        this.talentKey = talentKey;

        int centerPersonality = calculateSoulKey(alterEgo, talentKey);
        this.centerPersonality = centerPersonality;
        int centerDestiny = calculateSoulKey(alterEgo, destinyKey);
        this.centerDestiny = centerDestiny;
        int centerFamilyPrograms = calculateSoulKey(talentKey, destinyKey);
        this.centerFamilyPrograms = centerFamilyPrograms;

        maskLoveScenario = calculateSoulKey(centerPersonality, alterEgo);
        maskTalentRealization = calculateSoulKey(centerPersonality, talentKey);
        maskKarmicTask = calculateSoulKey(alterEgo, centerDestiny);
        maskHealingLoveScenario = calculateSoulKey(centerDestiny, centerFamilyPrograms);
        maskKarmicDestiny = calculateSoulKey(centerDestiny, destinyKey);
        maskFinancialHealing = calculateSoulKey(centerFamilyPrograms, destinyKey);
        maskHeartLine = calculateSoulKey(centerFamilyPrograms, centerPersonality);
        maskLoveTransmission = calculateSoulKey(centerPersonality, centerDestiny);
        maskScenarioTransmission = calculateSoulKey(talentKey, centerFamilyPrograms);

        shadow1 = calculateShadow(maskLoveScenario, maskKarmicTask, maskLoveTransmission);
        shadow2 = calculateShadow(maskTalentRealization, maskHeartLine, maskScenarioTransmission);
        shadow3 = calculateShadow(maskHealingLoveScenario, maskKarmicDestiny, maskFinancialHealing);
        typage = calculateTypage(shadow1, shadow2, shadow3);

        int assemblyPoint = calculateAssemblyPoint(destinyKey, talentKey, centerFamilyPrograms, centerPersonality, centerDestiny);
        int incarnationProfile = calculateIncarnationProfile(shadow1, shadow2, shadow3, typage);

        int resource = calculateResource(day, month, year);
        int quest = calculateQuest(day, month, year);

        drawTrianglesAndElements(g2d, alterEgo, destinyKey, talentKey, centerPersonality, centerDestiny, centerFamilyPrograms,
                maskLoveScenario, maskTalentRealization, maskKarmicTask, maskHealingLoveScenario, maskKarmicDestiny, maskFinancialHealing, maskHeartLine, maskLoveTransmission, maskScenarioTransmission,
                shadow1, shadow2, shadow3, typage, assemblyPoint, incarnationProfile, resource, quest);

        drawSmallTrianglesAndText(g2d, shadow1, shadow2, shadow3, typage, assemblyPoint, incarnationProfile, resource, quest);

        g2d.dispose();
        return image;
    }

    private void drawTrianglesAndElements(Graphics2D g2d, int alterEgo, int destinyKey, int talentKey, int centerPersonality, int centerDestiny, int centerFamilyPrograms,
                                          int maskLoveScenario, int maskTalentRealization, int maskKarmicTask, int maskHealingLoveScenario, int maskKarmicDestiny, int maskFinancialHealing, int maskHeartLine, int maskLoveTransmission, int maskScenarioTransmission,
                                          int shadow1, int shadow2, int shadow3, int typage, int assemblyPoint, int incarnationProfile, int resource, int quest) {
        int[] xPoints = {400, 100, 700};
        int[] yPoints = {100, 700, 700};

        drawTriangle(g2d, xPoints[0], yPoints[0], xPoints[1], yPoints[1], xPoints[2], yPoints[2], Color.GRAY, 2);

        drawTriangle(g2d, 400, 100, 250, 400, 550, 400, new Color(255, 0, 255), 2);
        drawTriangle(g2d, 250, 400, 100, 700, 400, 700, new Color(0, 255, 0), 2);
        drawTriangle(g2d, 550, 400, 400, 700, 700, 700, new Color(128, 0, 128), 2);
        drawTriangle(g2d, 400, 700, 250, 400, 550, 400, Color.WHITE, 2);

        drawText(g2d, Integer.toString(alterEgo), 385, 85, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(destinyKey), 705, 700, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(talentKey), 70, 700, new Font("Arial", Font.BOLD, 20), Color.BLACK);

        drawText(g2d, Integer.toString(centerPersonality), 230, 400, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(centerDestiny), 560, 400, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(centerFamilyPrograms), 390, 720, new Font("Arial", Font.BOLD, 20), Color.BLACK);

        drawText(g2d, Integer.toString(maskLoveScenario), 300, 240, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskTalentRealization), 150, 540, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskKarmicTask), 480, 240, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskHealingLoveScenario), 450, 540, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskKarmicDestiny), 640, 540, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskFinancialHealing), 540, 680, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskHeartLine), 300, 540, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskLoveTransmission), 390, 380, new Font("Arial", Font.BOLD, 20), Color.BLACK);
        drawText(g2d, Integer.toString(maskScenarioTransmission), 240, 680, new Font("Arial", Font.BOLD, 20), Color.BLACK);

        drawText(g2d, "Альтер-Эго", 370, 65, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Ключ реализации", 690, 720, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Предназначения", 690, 735, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Ключ реализации Таланта", 70, 720, new Font("Arial", Font.PLAIN, 12), Color.BLACK);

        drawText(g2d, "Центр Личности", 140, 410, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Центр Предназначения", 560, 415, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Центр Родовых Программ", 390, 735, new Font("Arial", Font.PLAIN, 12), Color.BLACK);

        drawText(g2d, "Маска Любовного сценария", 150, 255, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска реализации Таланта", 40, 520, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска Кармической задачи", 490, 255, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска Исцеления любовного сценария", 380, 560, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска Кармического Предназначения", 630, 560, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска Исцеления денежного сценария", 250, 520, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска линии Сердца", 500, 695, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска передачи любви", 350, 393, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Маска передачи сценария", 190, 695, new Font("Arial", Font.PLAIN, 12), Color.BLACK);

        drawText(g2d, "1-я Тень: " + shadow1, 50, 50, new Font("Arial", Font.BOLD, 20), Color.RED);
        drawText(g2d, "2-я Тень: " + shadow2, 50, 80, new Font("Arial", Font.BOLD, 20), Color.RED);
        drawText(g2d, "3-я Тень: " + shadow3, 50, 110, new Font("Arial", Font.BOLD, 20), Color.RED);

        drawText(g2d, "Типаж: " + typage, 50, 140, new Font("Arial", Font.BOLD, 20), Color.RED);
        drawText(g2d, "Точка сборки: " + assemblyPoint, 50, 170, new Font("Arial", Font.BOLD, 20), Color.BLUE);
        drawText(g2d, "Инкарнационный профиль: " + incarnationProfile, 50, 200, new Font("Arial", Font.BOLD, 20), Color.MAGENTA);
        drawText(g2d, "Ресурс: " + resource, 50, 230, new Font("Arial", Font.BOLD, 20), Color.GREEN);
        drawText(g2d, "Квест: " + quest, 50, 260, new Font("Arial", Font.BOLD, 20), Color.ORANGE);
    }

    private void drawSmallTrianglesAndText(Graphics2D g2d, int shadow1, int shadow2, int shadow3, int typage, int assemblyPoint, int incarnationProfile, int resource, int quest) {
        int triangleSize = 20;
        int startX = 50;
        int startY = 750;

        drawSmallTriangle(g2d, startX, startY, triangleSize, new Color(255, 0, 255));
        drawText(g2d, "Любовь/Отношения/Коммуникации", startX + 30, startY + 15, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
        drawText(g2d, "1-я Тень - " + shadow1, startX + 30, startY + 30, new Font("Arial", Font.PLAIN, 14), Color.BLACK);

        startY += 60;
        drawSmallTriangle(g2d, startX, startY, triangleSize, new Color(0, 255, 0));
        drawText(g2d, "Деньги/Карьера", startX + 30, startY + 15, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
        drawText(g2d, "2-я Тень - " + shadow2, startX + 30, startY + 30, new Font("Arial", Font.PLAIN, 14), Color.BLACK);

        startY += 60;
        drawSmallTriangle(g2d, startX, startY, triangleSize, new Color(128, 0, 128));
        drawText(g2d, "Предназначение/Кармическая задача", startX + 30, startY + 15, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
        drawText(g2d, "3-я Тень - " + shadow3, startX + 30, startY + 30, new Font("Arial", Font.PLAIN, 14), Color.BLACK);

        startY += 60;
        drawSmallTriangle(g2d, startX + 300, startY - 180, triangleSize, Color.WHITE);
        drawText(g2d, "Типаж - " + typage, startX + 320, startY - 165, new Font("Arial", Font.PLAIN, 14), Color.BLACK);

        drawText(g2d, "Точка сборки - " + assemblyPoint, startX + 30, startY + 90, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
        drawText(g2d, "Инкарнационный профиль - " + incarnationProfile, startX + 30, startY + 110, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
        drawText(g2d, "Ресурс - " + resource, startX + 30, startY + 130, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
        drawText(g2d, "Квест - " + quest, startX + 30, startY + 150, new Font("Arial", Font.PLAIN, 14), Color.BLACK);
    }

    private void drawSmallTriangle(Graphics2D g2d, int x, int y, int size, Color color) {
        g2d.setColor(color);
        int[] xPoints = {x, x + size / 2, x - size / 2};
        int[] yPoints = {y, y + size, y + size};
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawPolygon(xPoints, yPoints, 3);
    }

    private void drawTriangle(Graphics2D g2d, int x1, int y1, int x2, int y2, int x3, int y3, Color color, int strokeWidth) {
        g2d.setColor(color);
        int[] xPoints = {x1, x2, x3};
        int[] yPoints = {y1, y2, y3};
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.drawPolygon(xPoints, yPoints, 3);
    }

    private void drawText(Graphics2D g2d, String text, int x, int y, Font font, Color color) {
        g2d.setColor(color);
        g2d.setFont(font);
        g2d.drawString(text, x, y);
    }

    private void sendImage(long chatId, BufferedImage image) throws IOException, TelegramApiException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            InputFile inputFile = new InputFile(bais, "esoteric_calculation.jpg");
            SendPhoto msg = new SendPhoto();
            msg.setChatId(String.valueOf(chatId));
            msg.setPhoto(inputFile);
            execute(msg);
        }
    }

    private void sendImage(long chatId, File imageFile) throws TelegramApiException {
        SendPhoto msg = new SendPhoto();
        msg.setChatId(String.valueOf(chatId));
        msg.setPhoto(new InputFile(imageFile));
        execute(msg);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TriangleDrawingBot bot = new TriangleDrawingBot();
            bot.createGreetingFileIfNotExists();
            botsApi.registerBot(bot);
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }
}
