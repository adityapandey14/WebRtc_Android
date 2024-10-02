package com.example.webrtc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

public class lobby extends AppCompatActivity {

    private EditText emailEditText;
    private EditText roomEditText;
    private Socket socket;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        emailEditText = findViewById(R.id.emailEditText);
        roomEditText = findViewById(R.id.roomEditText);
        Button joinButton = findViewById(R.id.joinButton);

        // Initialize Socket
        socket = SocketManager.getInstance().getSocket();
        socket.connect();

        // Log socket connection
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("Socket", "Connected to server");
        }).on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.d("Socket", "Connection error");
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRoom();
            }
        });

        // Listen for room join acknowledgment
        socket.on("room:join", args -> {
            Log.d("Lobby", "Received room join response");
            JSONObject data = (JSONObject) args[0];
            try {
                String roomId = data.getString("room");
                Log.d("Lobby", "Room ID: " + roomId);
                Intent intent = new Intent(lobby.this, room.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void joinRoom() {
        String email = emailEditText.getText().toString();
        String room = roomEditText.getText().toString();

        Log.d("Lobby", "Joining room with email: " + email + ", room: " + room);

        JSONObject joinData = new JSONObject();
        try {
            joinData.put("email", email);
            joinData.put("room", room);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("room:join", joinData);
    }
}
