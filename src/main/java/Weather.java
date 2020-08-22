
//9d8dc646e372a9d578cdb4833f573294
//testWeather

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Date;
import java.util.Scanner;

//18b9c0747d42d8794cf976a839209958
//Default
public class Weather {
  /*  public static String getWeatherNameCity(String message, Model model) throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=18b9c0747d42d8794cf976a839209958");

        return getWeather(url, model);
    }*/

    public static String getWeatherLocation(String lat,String lon, Model model) throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon="+ lon + "&units=metric&appid=18b9c0747d42d8794cf976a839209958");
        String result = getWeather(url, model);

        URL url1 = new URL("http://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon="+ lon + "&exclude=minutely,hourly&units=metric&appid=18b9c0747d42d8794cf976a839209958");
        result = result + "\n\n" + "Прогноз на сутки: " + "\n";
        result = result + getWeatherForDay(url1);

        return result;
    }

    public static String getWeather(URL url, Model model) throws IOException {
        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";
        while (in.hasNext()) {
            result += in.nextLine();
        }

        JSONObject object = new JSONObject(result);
        model.setName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        model.setTemp(main.getDouble("temp"));
        model.setHumidity(main.getDouble("humidity"));

        JSONArray getArray = object.getJSONArray("weather");
        for (int i = 0; i < getArray.length(); i++) {
            JSONObject obj = getArray.getJSONObject(i);
            model.setIcon((String) obj.get("icon"));
            model.setMain((String) obj.get("main"));
        }

        return "Город: " + model.getName() + "\n" +
                "Температура: " + model.getTemp() + " C" + "\n" +
                "Влажность: " + model.getHumidity() + " %" + "\n" +
                "Осадки: " + model.getMain() + "\n" +
                "http://openweathermap.org/img/w/" + model.getIcon() + ".png";
    }

    public static String getWeatherForDay(URL url) throws IOException {
        Scanner in = new Scanner((InputStream) url.getContent());
        String res = "";
        while (in.hasNext()) {
            res += in.nextLine();
        }

        JSONObject object = new JSONObject(res);
        JSONArray getArray = object.getJSONArray("daily");
        JSONObject object_0 = getArray.getJSONObject(0);
        JSONObject temp = object_0.getJSONObject("temp");


        double tempMorn =temp.getDouble("morn");
        double tempDay = temp.getDouble("day");
        double tempEvening = temp.getDouble("eve");
        double tempNight = temp.getDouble("night");

        return   "Утром: "+ tempMorn + " C" + "\n" + "Днем: "+ tempDay + " C"  + "\n" + "Вечером: "+ tempEvening + " C" + "\n" + "Ночью: "+ tempNight + " C"  ;
    }
}
