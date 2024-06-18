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

        // Логика рисования треугольников и других элементов
        drawTrianglesAndElements(g2d);

        g2d.dispose();
        return image;
    }

    private void drawTrianglesAndElements(Graphics2D g2d) {
        // Координаты точек треугольника
        int[] xPoints = {400, 100, 700};
        int[] yPoints = {100, 700, 700};

        // Рисуем большой внешний треугольник
        drawTriangle(g2d, xPoints[0], yPoints[0], xPoints[1], yPoints[1], xPoints[2], yPoints[2], new Color(255, 0, 255), 2);

        // Внутренние треугольники
        drawTriangle(g2d, 250, 400, 100, 700, 400, 700, new Color(0, 255, 0), 2); // Левый нижний треугольник (зеленый)
        drawTriangle(g2d, 550, 400, 400, 700, 700, 700, new Color(128, 0, 128), 2); // Правый нижний треугольник (фиолетовый)
        drawTriangle(g2d, 400, 100, 250, 400, 550, 400, new Color(255, 0, 255), 2); // Верхний треугольник (розовый)

        // Центральный белый треугольник
        drawTriangle(g2d, 250, 400, 400, 700, 550, 400, Color.WHITE, 2);

        // Добавление текста и номеров
        drawText(g2d, "2", 390, 90, new Font("Arial", Font.BOLD, 20), Color.BLACK);  // Верхняя точка
        drawText(g2d, "5", 380, 350, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Средняя точка

        // Номера на треугольниках
        drawText(g2d, "1", 140, 470, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Левая точка (слева)
        drawText(g2d, "1", 650, 470, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Правая точка (слева)
        drawText(g2d, "4", 380, 650, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Нижняя центральная точка
        drawText(g2d, "3", 490, 600, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Центральная точка (справа)
        drawText(g2d, "21", 60, 740, new Font("Arial", Font.BOLD, 20), Color.BLACK);  // Нижняя левая точка
        drawText(g2d, "22", 360, 780, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Нижняя средняя точка
        drawText(g2d, "22", 600, 740, new Font("Arial", Font.BOLD, 20), Color.BLACK); // Нижняя правая точка

        // Подписи
        drawText(g2d, "Линия сердца 1-4-3", 20, 790, new Font("Arial", Font.PLAIN, 12), Color.BLACK);

        drawText(g2d, "Любовь/Отношения/Коммуникации 1-я Тема - 12", 20, 820, new Font("Arial", Font.PLAIN, 12), new Color(255, 0, 255));
        drawText(g2d, "Деньги/Карьера 2-я Тема - 22", 20, 840, new Font("Arial", Font.PLAIN, 12), new Color(0, 255, 0));
        drawText(g2d, "Предназначение/Кармическая задача 3-я Тема - 8", 20, 860, new Font("Arial", Font.PLAIN, 12), new Color(128, 0, 128));
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


