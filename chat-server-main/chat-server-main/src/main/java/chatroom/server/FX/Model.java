package chatroom.server.FX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class Model {

    private String serverAddress;
    private int serverPort;

    public Model() {
        // Set default server address and port
        this.serverAddress = "javaprojects.ch";
        this.serverPort = 50001;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
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

    public List<String> fetchUsersFromServer() {
        String serverEndpoint = "http://javaprojects.ch:50001/users/online";

        try {
            URL url = new URL(serverEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method and headers if needed
            connection.setRequestMethod("GET");
            // connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the response to get the list of online users
                return parseUserList(response.toString());
            } else {
                System.out.println("Error: " + responseCode);
                // Handle error if needed
                return new ArrayList<>(); // Return an empty list in case of an error
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception if needed
            return new ArrayList<>(); // Return an empty list in case of an exception
        }
    }

    private List<String> parseUserList(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONArray onlineUsers = json.getJSONArray("online");

            List<String> userList = new ArrayList<>();
            for (int i = 0; i < onlineUsers.length(); i++) {
                userList.add(onlineUsers.getString(i));
            }
            return userList;
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing exception if needed
            return new ArrayList<>(); // Return an empty list in case of an exception
        }
    }

}
