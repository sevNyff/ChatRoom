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
import java.util.HashMap;
import java.util.List;


public class Model {

    private String serverAddress;
    private int serverPort;
    private String userToken;
    private HashMap<String, String> chats;

    public Model() {
        // Set default server address and port
        this.serverAddress = "127.0.0.1";
        this.serverPort = 50001;
        this.chats = new HashMap<>();
    }

    public void setChats(HashMap<String, String> chats) {
        this.chats = chats;
    }
    public HashMap<String, String> getChats() {
        return chats;
    }
    public void setUserToken(String token) {
        this.userToken = token;
    }
    public String getUserToken() {
        return userToken;
    }
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    public String getServerAddress(){return this.serverAddress;};
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public int getServerPort(){return this.serverPort;}
}
