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
import java.util.List;

public class TriangleDrawingBot extends TelegramLongPollingBot {

    private static final String GREETING_FILE_PATH = "greeting.txt";
    private static final String REMINDER_FILE_PATH = "reminder.txt";
    private static final String SUPPORT_FILE_PATH = "support.txt";
    private static final String GREETING_MESSAGE = "Добро пожаловать в NumerologyBot!";
    private static final String IMAGE_PATH = "pic1.jpg";
    private int selectedDay;
    private int selectedMonth;

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
                        int year = Integer.parseInt(messageText);
                        if (year < 1000 || year > 9999) {
                            sendMessage(chatId, "Введите корректный год в формате: YYYY (например, 1987).");
                        } else {
                            BufferedImage image = drawEsotericImage(selectedDay, selectedMonth, year);
                            sendImage(chatId, image);
                            showResultButtons(chatId);
                        }
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "Введите корректный год в формате: YYYY (например, 1987).");
                    } catch (IOException | TelegramApiException e) {
                        e.printStackTrace();
                        sendMessage(chatId, "Произошла ошибка при обработке вашей команды. Пожалуйста, попробуйте снова.");
                    }
                } else if (messageText.equals("/start")) {
                    sendGreeting(chatId);
                }
            } else if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                long chatId = update.getCallbackQuery().getMessage().getChatId();

                if (callbackData.startsWith("date:day:")) {
                    selectedDay = Integer.parseInt(callbackData.split(":")[2]);
                    showMonthPicker(chatId);
                } else if (callbackData.startsWith("date:month:")) {
                    selectedMonth = Integer.parseInt(callbackData.split(":")[2]);
                    promptYearInput(chatId);
                } else {
                    switch (callbackData) {
                        case "register":
                            sendMessage(chatId, "Регистрация в боте...");
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
                        case "description":
                            sendMessage(chatId, "Получить описание еще не реализовано.");
                            break;
                        case "back":
                            sendGreeting(chatId);
                            break;
                    }
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendGreeting(long chatId) {
        try {
            String greeting = new String(Files.readAllBytes(Paths.get(GREETING_FILE_PATH)));
            sendMessage(chatId, greeting);

            // Отправка изображения
            sendImage(chatId, new File(IMAGE_PATH));

            // Отправка кнопок
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

    private void sendButtons(long chatId) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(InlineKeyboardButton.builder().text("Зарегистрироваться в боте").callbackData("register").build());

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

    private void promptYearInput(long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Теперь введите год в формате: YYYY")
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
        int destinyKey = month;
        int talentKey = sumOfDigits(year);

        int centerPersonality = calculateSoulKey(alterEgo, talentKey);
        int centerDestiny = calculateSoulKey(alterEgo, destinyKey);
        int centerFamilyPrograms = calculateSoulKey(talentKey, destinyKey);

        int maskLoveScenario = calculateSoulKey(centerPersonality, alterEgo);
        int maskTalentRealization = calculateSoulKey(centerPersonality, talentKey);
        int maskKarmicTask = calculateSoulKey(alterEgo, centerDestiny);
        int maskHealingLoveScenario = calculateSoulKey(centerDestiny, centerFamilyPrograms);
        int maskKarmicDestiny = calculateSoulKey(centerDestiny, destinyKey);
        int maskFinancialHealing = calculateSoulKey(centerFamilyPrograms, destinyKey);
        int maskHeartLine = calculateSoulKey(centerFamilyPrograms, centerPersonality);
        int maskLoveTransmission = calculateSoulKey(centerPersonality, centerDestiny);
        int maskScenarioTransmission = calculateSoulKey(talentKey, centerFamilyPrograms);

        int shadow1 = calculateShadow(maskLoveScenario, maskKarmicTask, maskLoveTransmission);
        int shadow2 = calculateShadow(maskTalentRealization, maskHeartLine, maskScenarioTransmission);
        int shadow3 = calculateShadow(maskHealingLoveScenario, maskKarmicDestiny, maskFinancialHealing);
        int typage = calculateTypage(shadow1, shadow2, shadow3);

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


















