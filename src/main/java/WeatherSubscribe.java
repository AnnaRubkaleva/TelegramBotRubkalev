import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherSubscribe{
 //   WeatherHandlers handler = WeatherHandlers.getInstance();
    WeatherBot handler = WeatherBot.getInstance();

    public WeatherSubscribe(){
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new FeederTask(),0,  86400000);
    }

        class FeederTask extends TimerTask{

            public void run() {
                try {
                    Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                    Connection conn= DriverManager.getConnection("jdbc:ucanaccess://SubscribeDB.accdb");
                    Statement st=conn.createStatement();

                    String sql = "SELECT DISTINCT chatID, userName, latitude, longitude FROM Subscriber";
                    ResultSet rs = st.executeQuery(sql);

                    while (rs.next()) {
                        String chatID = rs.getString("chatID");
                        String lat = rs.getString("latitude");
                        String lon = rs.getString("longitude");

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.enableMarkdown(true);
                        sendMessage.setChatId(chatID);
                        sendMessage.setText(Weather.getWeatherLocation(lat, lon, new Model()));
                        try {
                            handler.execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                    }
                    st.close();
                    conn.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
}
