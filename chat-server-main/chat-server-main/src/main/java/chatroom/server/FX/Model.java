package chatroom.server.FX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Model {

    private String serverAddress;
    private int serverPort;
    private String userToken;
    protected HashMap<String, String> chats;
    private ArrayList<String> userChats;

    public Model() {
        this.serverAddress = "javaprojects.ch";
        this.serverPort = 50001;
        this.chats = new HashMap<>();
        this.userChats = new ArrayList<>();
    }
    //Getters und setters
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
    public String getServerAddress(){return this.serverAddress;}
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    public int getServerPort(){return this.serverPort;}
    public ArrayList<String> getUserChats() {return userChats;}
    public void setUserChats(ArrayList<String> userChats) {this.userChats = userChats;}
}
