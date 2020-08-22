import java.sql.SQLException;
import java.sql.*;

public class MyConnection {
    static Connection connection;

    /**
     * Добавить подписку
     */
    synchronized public void addSubscriber(long chatID, String userName, String lat, String lon) throws SQLException{
     try {
         Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
         Connection conn=DriverManager.getConnection("jdbc:ucanaccess://SubscribeDB.accdb");
         Statement st=conn.createStatement();

         String sql = "INSERT INTO Subscriber(chatID, userName, latitude, longitude) VALUES (" + chatID +  ", '" + userName + "', '" + lat + "', '" + lon +"')";
         st.executeUpdate(sql);

         st.close();
         conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Отменить подписку
     */
    synchronized public void deleteSubscriber(long chatID, String userName) throws SQLException{
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            Connection conn=DriverManager.getConnection("jdbc:ucanaccess://SubscribeDB.accdb");
            Statement st=conn.createStatement();

            String sql = "DELETE FROM Subscriber WHERE chatID = " + chatID +  " AND userName = '" + userName + "'";
            st.executeUpdate(sql);

            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
