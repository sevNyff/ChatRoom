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

                setUserToken(token);

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
    public void setUserToken(String token) {
        this.userToken = token;
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

    public boolean registerUser(String username, String password) {
        String registerEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/user/register";

        try {
            // Construct the registration URL (replace with your actual registration endpoint)
            URL url = new URL(registerEndpoint);

            // Create the HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Construct the JSON payload for registration (replace with your actual format)
            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            // Set up the connection for output (i.e., sending the JSON payload)
            connection.setDoOutput(true);

            // Write the JSON payload to the connection
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Registration successful
                System.out.println("User registered successfully!");
                return true;
            } else {
                // Handle registration failure (display an error message, etc.)
                System.out.println("User registration failed. HTTP Response Code: " + responseCode);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception if needed
            return false;
        }
    }
    public void logout() {
        String serverLogoutEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/user/logout";
       if (isLoggedIn()) {
            try {
                // Construct the logout URL
                URL url = new URL(serverLogoutEndpoint);

                // Create the HTTP connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                String jsonInputString = "{\"token\": \"" + userToken + "\"}";

                // Set up the connection for output (i.e., sending the JSON payload)
                connection.setDoOutput(true);

                // Write the JSON payload to the connection
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }
                // Get the HTTP response code
                int responseCode = connection.getResponseCode();

                System.out.println("Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Logout successful
                    System.out.println("Logout successful!");
                } else {
                    // Handle logout failure (display an error message, etc.)
                    System.out.println("Logout failed. HTTP Response Code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception if needed
                System.out.println("Logout failed due to an exception: " + e.getMessage());
            } finally {
                // Whether the logout succeeded or not, clear the user token locally
                userToken = null;
            }
       }
    }
    public boolean sendMessage(String receiver, String message) {
        try {
            // Token aus dem Model abrufen
            String token = getUserToken();
            //String token = "2338FC763A6B189428F8D6125B03E769";

            // Überprüfen, ob der Benutzer eingeloggt ist
            if (token == null || token.isEmpty()) {
                System.out.println("You need to log in first.");
                return false;
            }

            // Serveradresse für die Nachricht
            String sendMessageEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/chat/send";

            // JSON-Payload für die Nachricht erstellen
            String jsonInputString = String.format("{\"token\": \"%s\", \"username\": \"%s\", \"message\": \"%s\"}",
                    token, receiver, message);

            // URL und Verbindung erstellen
            URL url = new URL(sendMessageEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);


            // JSON-Payload an die Verbindung schreiben
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // HTTP-Antwortcode abrufen
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode); //For debugging

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Nachricht erfolgreich gesendet
                System.out.println("Message sent successfully!");
                return true;
            } else {
                // Nachrichtsendung fehlgeschlagen
                System.out.println("Message sending failed. HTTP Response Code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fehler bei der Nachrichtenübermittlung
            System.out.println("Message sending failed due to an exception: " + e.getMessage());
            return false;
        }
    }
    public List<String> pollMessages() {
        if (!isLoggedIn()) {
            // If the user is not logged in, return an empty list
            return new ArrayList<>();
        }

        try {
            String pollEndpoint = "http://" + this.serverAddress + ":" + this.serverPort + "/chat/poll";
            URL url = new URL(pollEndpoint);

            // Create the HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Construct the JSON payload for polling messages
            String jsonInputString = "{\"token\": \"" + userToken + "\"}";

            // Set up the connection for output (i.e., sending the JSON payload)
            connection.setDoOutput(true);

            // Write the JSON payload to the connection
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // Get the HTTP response code
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

                // Parse the response to get the list of messages
                return parseMessageList(response.toString());
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
                // Handle the case where the "messages" array is not present in the response
                System.out.println("No messages array in the response");
                return new ArrayList<>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing exception if needed
            return new ArrayList<>(); // Return an empty list in case of an exception
        }
    }



}
