package chatroom.server.FX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    public String getServerAddress(){return this.serverAddress;};
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public int getServerPort(){return this.serverPort;}
    public boolean pingServer(String serverAddress, int port) {
        try {
            URL url = new URL("http://" + serverAddress + ":" + port + "/ping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            //wait 1 second for response, else not pingable
            connection.setConnectTimeout(1000);

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> fetchUsersFromServer() {
        String serverEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/users/online";

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

    public String loginAndGetToken(String username, String password) {
        String loginEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/user/login";

        try {
            // Construct the login URL (replace with your actual login endpoint)

            URL url = new URL(loginEndpoint);

            // Create the HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Construct the JSON payload for login (replace with your actual format)
            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            // Set up the connection for output (i.e., sending the JSON payload)
            connection.setDoOutput(true);

            // Write the JSON payload to the connection
            connection.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the token from the response (replace with your actual token extraction)
                String token = extractTokenFromResponse(connection);

                // Close the connection
                connection.disconnect();

                return token;
            } else {
                // Handle login failure (display an error message, etc.)
                System.out.println("Login failed. HTTP Response Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception if needed
            return null;
        }
    }

    private String extractTokenFromResponse(HttpURLConnection connection) throws IOException {
        // Read the response from the connection
        StringBuilder response = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // Parse the response to get the token (replace with your actual token extraction)
        JSONObject json = new JSONObject(response.toString());
        return json.getString("token");
    }

}
