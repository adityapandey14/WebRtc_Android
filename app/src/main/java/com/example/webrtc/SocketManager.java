package com.example.webrtc;

import android.util.Log;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class SocketManager {

    private static SocketManager instance;
    private Socket socket;

    private SocketManager() {
        try {
            socket = IO.socket("http://localhost:8000"); // Replace with your server URL
        } catch (URISyntaxException e) {
            Log.e("SocketManager", "Error initializing socket", e);
        }
    }

    public static SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

    public void connect() {
        socket.connect();
    }

    public void disconnect() {
        socket.disconnect();
    }
}