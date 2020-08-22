import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherBot extends TelegramLongPollingBot {

    private MyConnection db;
    private static WeatherBot instance;
    private boolean isGetSubscribing = false;


    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new WeatherBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        new WeatherSubscribe();
  }

    public static WeatherBot getInstance(){
        if(instance == null) instance = new WeatherBot();
        return instance;
    }

    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        Model model = new Model();
        Message message = update.getMessage();


        if (message != null && message.hasText()) {
            String strMessage = message.getText();
            switch (strMessage) {
                case "/start":
                case "Справка":
                    String str1 ="Telegram-бот, который сообщает текущую погоду и прогноз на сутки в ответ на присланное местоположение (геопозицю)." + "\n" + "\n" +
                    "Список доступных команд: "  + "\n" +
                            "/start - начало работы" + "\n" +
                            "/subscribe - подписаться на ежедневную рассылку" + "\n" +
                            "/unsubscribe - отписаться от ежедневной рассылки";
                    sendMsg(message.getChatId().toString(), str1);
                    break;
                case "/subscribe":
                    isGetSubscribing = true;
                    sendMsg(message.getChatId().toString(), "Отправьте геолокацию населённого пункта, для которого Вы хотите получать ежедневный прогноз погоды.");
                    break;

                case "/unsubscribe":
                    long id = message.getChatId();
                    String userName = message.getChat().getUserName();

                    try {
                        db = new MyConnection();
                        db.deleteSubscriber(id, userName);
                        sendMsg(message.getChatId().toString(),"Подписка отменена!");

                    } catch (SQLException e) {
                        sendMsg(message.getChatId().toString(),"Не удалось отписаться!");
                    }

                    break;

                default:
                    sendMsg(message.getChatId().toString(), "Непонятно! Отправьте, пожалуйста, Вашу геолокацию!");
                    /*try {
                        sendMsg(message.getChatId().toString(), Weather.getWeatherNameCity(message.getText(), model));
                    } catch (IOException e) {
                        sendMsg(message.getChatId().toString(), "Город не найден!");
                    }*/
                    break;
            }
        }
        else
            if (message != null && message.hasLocation()) {
                Location location = message.getLocation();
                String strLocation = location.toString();

                if (isGetSubscribing)
                {
                    isGetSubscribing = false;
                    long id = message.getChatId();
                    String userName = message.getChat().getUserName();
                    String lat = location.getLatitude().toString();
                    String lon = location.getLongitude().toString();

                    try {
                        db = new MyConnection();
                        db.addSubscriber(id, userName, lat, lon);
                        sendMsg(message.getChatId().toString(),"Спасибо за подписку!");

                    } catch (SQLException e) {
                        sendMsg(message.getChatId().toString(),"Не удалось подписаться!");
                    }

                }
                else
                    try {
                        sendMsg(message.getChatId().toString(), Weather.getWeatherLocation(location.getLatitude().toString(), location.getLongitude().toString(), model));
                    } catch (IOException e) {
                        sendMsg(message.getChatId().toString(), "Город не найден!");
                    }
            }
    }

    /**
     * Метод для отображения кнопок при вводе сообщения.
     * @param chatId
     * @param s
     */
    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для отображения кнопок при вводе сообщения.
     * @param sendMessage Содержит сообщение.
     */
    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Справка"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "RubkalevWeatherBot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "1196838058:AAGRSWxhF0045BYT-GWEUfXmDt1SeYinPQY";
    }
}
