package chatroom.server.FX;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Model {
    public boolean pingServer(String serverAddress, int port) {
        try {
            URL url = new URL("http://" + serverAddress + ":" + port + "/ping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
