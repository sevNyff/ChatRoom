package chatroom.server.FX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;


public class Model {

    private String serverAddress;
    private int serverPort;
    public String userToken;

    public Model() {
        // Set default server address and port
        this.serverAddress = "javaprojects.ch";
        this.serverPort = 50001;
    }

    public String getUserToken() {
        return userToken;
    }



    public boolean isLoggedIn() {
        // Check if the user is currently logged in
        System.out.println(userToken);
        return userToken != null && !userToken.isEmpty();
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
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return parseUserList(response.toString());
            } else {
                System.out.println("Error: " + responseCode);
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
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
            return new ArrayList<>();
        }
    }

    public String loginAndGetToken(String username, String password) {
        String loginEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/user/login";

        try {
            URL url = new URL(loginEndpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
            connection.setDoOutput(true);

            connection.getOutputStream().write(jsonInputString.getBytes("UTF-8"));

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String token = extractTokenFromResponse(connection);
                connection.disconnect();
                setUserToken(token);
                return token;
            } else {
                System.out.println("Login failed. HTTP Response Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setUserToken(String token) {
        this.userToken = token;
    }

    private String extractTokenFromResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        JSONObject json = new JSONObject(response.toString());
        return json.getString("token");
    }

    public boolean registerUser(String username, String password) {
        String registerEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/user/register";

        try {
            URL url = new URL(registerEndpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();
            System.out.println("Response code Register: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("User registered successfully!");
                return true;
            } else {
                System.out.println("User registration failed. HTTP Response Code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void logout() {
        String serverLogoutEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/user/logout";
       if (isLoggedIn()) {
            try {
                URL url = new URL(serverLogoutEndpoint);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                String jsonInputString = "{\"token\": \"" + userToken + "\"}";
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = connection.getResponseCode();
                System.out.println("Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Logout successful!");
                } else {
                    System.out.println("Logout failed. HTTP Response Code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Logout failed due to an exception: " + e.getMessage());
            } finally {
                userToken = null;
            }
       }
    }
    public boolean sendMessage(String receiver, String message) {
        try {
            String token = getUserToken();
            if (token == null || token.isEmpty()) {
                System.out.println("You need to log in first.");
                return false;
            }
            String sendMessageEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/chat/send";
            String jsonInputString = String.format("{\"token\": \"%s\", \"username\": \"%s\", \"message\": \"%s\"}",
                    token, receiver, message);

            URL url = new URL(sendMessageEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode); //For debugging

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully!");
                return true;
            } else {
                System.out.println("Message sending failed. HTTP Response Code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Message sending failed due to an exception: " + e.getMessage());
            return false;
        }
    }
    public List<String> pollMessages() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }

        try {
            String pollEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/chat/poll";
            URL url = new URL(pollEndpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"token\": \"" + userToken + "\"}";

            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return parseMessageList(response.toString());
            } else {
                System.out.println("Error: " + responseCode);
                return new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<String> parseMessageList(String response) {
        try {
            JSONObject json = new JSONObject(response);

            if (json.has("messages")) {
                JSONArray messages = json.getJSONArray("messages");

                List<String> messageList = new ArrayList<>();
                for (int i = 0; i < messages.length(); i++) {
                    JSONObject messageObject = messages.getJSONObject(i);
                    String username = messageObject.getString("username");
                    String message = messageObject.getString("message");
                    messageList.add(username + ": " + message);
                }
                return messageList;
            } else {
                System.out.println("No messages array in the response");
                return new ArrayList<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



}
