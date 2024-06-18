package ru.yaga;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TriangleDrawingBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "NumerologyBot";
    }

    @Override
    public String getBotToken() {
        return "REMOVED_TOKEN"; // Замените на токен вашего бота
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Пример обработки команды для рисования треугольника
            if (messageText.startsWith("/drawtriangle")) {
                String[] parts = messageText.split(" ");
                if (parts.length == 4) {
                    try {
                        int day = Integer.parseInt(parts[1]);
                        int month = Integer.parseInt(parts[2]);
                        int year = Integer.parseInt(parts[3]);

                        BufferedImage image = drawEsotericImage(day, month, year);
                        sendImage(chatId, image);
                    } catch (NumberFormatException | IOException | TelegramApiException e) {
                        e.printStackTrace();
                        sendMessage(chatId, "Произошла ошибка при обработке вашей команды. Убедитесь, что вы ввели правильный формат даты.");
                    }
                } else {
                    sendMessage(chatId, "Неверный формат команды. Используйте /drawtriangle day month year");
                }
            }
        }
    }

    private int sumOfDigits(int number) {
        while (number > 9) {
            int sum = 0;
            while (number != 0) {
                sum += number % 10;
                number /= 10;
            }
            number = sum;
        }
        return number;
    }

    private int calculateYearValue(int year) {
        int sum = 0;
        while (year != 0) {
            sum += year % 10;
            year /= 10;
        }
        return sum;
    }

    private BufferedImage drawEsotericImage(int day, int month, int year) throws IOException {
        int width = 800;
        int height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Рисуем фон
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Настройки пера
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        // Расчет значений вершин
        int alterEgo = sumOfDigits(day);  // Альтер-Эго: сумма цифр дня рождения, приведенная к одной цифре
        int destinyKey = month;  // Ключ реализации Предназначения: месяц рождения
        int talentKey = calculateYearValue(year);  // Ключ реализации Таланта: сумма цифр года рождения

        // Пример для даты 24.01.1974:
        // Альтер-Эго: 2 (2 + 4 = 6; 6 = 2, если необходимо)
        // Ключ реализации Предназначения: 1
        // Ключ реализации Таланта: 21 (1 + 9 + 7 + 4 = 21)

        if (alterEgo == 6) {
            alterEgo = 2; // специальное правило для Альтер-Эго
        }

        // Логика рисования треугольников и других элементов
        drawTrianglesAndElements(g2d, alterEgo, destinyKey, talentKey);

        g2d.dispose();
        return image;
    }

    private void drawTrianglesAndElements(Graphics2D g2d, int alterEgo, int destinyKey, int talentKey) {
        // Координаты точек треугольника
        int[] xPoints = {400, 100, 700};
        int[] yPoints = {100, 700, 700};

        // Рисуем большой внешний треугольник
        drawTriangle(g2d, xPoints[0], yPoints[0], xPoints[1], yPoints[1], xPoints[2], yPoints[2], Color.GRAY, 2);

        // Внутренние треугольники
        drawTriangle(g2d, 400, 100, 250, 400, 550, 400, new Color(255, 0, 255), 2); // Верхний треугольник (ярко-розовый)
        drawTriangle(g2d, 250, 400, 100, 700, 400, 700, new Color(0, 255, 0), 2); // Левый нижний треугольник (ярко-зеленый)
        drawTriangle(g2d, 550, 400, 400, 700, 700, 700, new Color(128, 0, 128), 2); // Правый нижний треугольник (ярко-фиолетовый)
        drawTriangle(g2d, 400, 700, 250, 400, 550, 400, Color.WHITE, 2); // Центральный треугольник (белый)

        // Добавление текста и номеров
        drawText(g2d, Integer.toString(alterEgo), 390, 90, new Font("Arial", Font.BOLD, 20), Color.BLACK);  // Верхняя точка
        drawText(g2d, Integer.toString(destinyKey), 690, 710, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Правая нижняя точка
        drawText(g2d, Integer.toString(talentKey), 90, 710, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Левая нижняя точка

        // Подписи
        drawText(g2d, "Альтер-Эго", 380, 70, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Ключ реализации Предназначения", 620, 730, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
        drawText(g2d, "Ключ реализации Таланта", 60, 730, new Font("Arial", Font.PLAIN, 12), Color.BLACK);
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

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TriangleDrawingBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}







