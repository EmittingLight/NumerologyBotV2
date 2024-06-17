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
        // Пример рисования элементов на основе изображения

        // Рисуем большие треугольники с внутренней заливкой
        drawTriangle(g2d, 400, 50, 250, 400, 550, 400, new Color(255, 0, 255), 5); // Магента треугольник (Альтер-Эго)
        drawTriangle(g2d, 250, 400, 150, 750, 400, 750, new Color(0, 255, 0), 5); // Зеленый треугольник (Деньги/Карьера)
        drawTriangle(g2d, 550, 400, 400, 750, 650, 750, new Color(0, 0, 255), 5); // Синий треугольник (Карм. задача)
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

    private void drawLine(Graphics2D g2d, int x1, int y1, int x2, int y2, Color color, int strokeWidth) {
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.drawLine(x1, y1, x2, y2);
    }

    private void drawCircle(Graphics2D g2d, int x, int y, int radius, Color color) {
        g2d.setColor(color);
        g2d.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
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