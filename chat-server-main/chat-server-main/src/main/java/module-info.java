module ch.fhnw.richards.chatserver {
    requires java.logging;
    requires jdk.httpserver;
    requires org.json;
    requires javafx.graphics;
    requires javafx.controls;
    exports chatroom.server;
    exports chatroom.server.FX;
}