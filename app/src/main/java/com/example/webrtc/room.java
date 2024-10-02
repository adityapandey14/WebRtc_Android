package com.example.webrtc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

public class room extends AppCompatActivity {

    private Socket socket;
    private String remoteSocketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Button callButton = findViewById(R.id.callButton);

        // Initialize Socket
        socket = SocketManager.getInstance().getSocket();
        if (socket == null) {
            Log.e("RoomActivity", "Socket is not initialized!");
            return; // Prevent further execution if socket is not available
        }

        // Listen for user joining room
        socket.on("user:joined", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                remoteSocketId = data.getString("id");
                Log.d("RoomActivity", "User joined: " + remoteSocketId);
            } catch (JSONException e) {
                Log.e("RoomActivity", "Error parsing user joined event", e);
            }
        });

        // Set call button action
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateCall();
            }
        });

        // Handle incoming call
        socket.on("incoming:call", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String from = data.getString("from");
                Log.d("RoomActivity", "Incoming call from: " + from);
                // Accept the call and send back answer
            } catch (JSONException e) {
                Log.e("RoomActivity", "Error parsing incoming call event", e);
            }
        });

        // Handle call accepted
        socket.on("call:accepted", args -> {
            Log.d("RoomActivity", "Call accepted!");
            // Handle peer connection negotiation
        });
    }

    private void initiateCall() {
        if (remoteSocketId == null) {
            Log.e("RoomActivity", "Remote socket ID is null. Cannot initiate call.");
            return; // Prevent call initiation if no remote socket ID
        }

        JSONObject callData = new JSONObject();
        try {
            callData.put("to", remoteSocketId);
            // Get offer from peer connection (This should be a valid SDP offer string)
            callData.put("offer", "your-offer");
            socket.emit("user:call", callData);
        } catch (JSONException e) {
            Log.e("RoomActivity", "Error creating call data", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister socket listeners here to prevent memory leaks
        socket.off("user:joined");
        socket.off("incoming:call");
        socket.off("call:accepted");
    }
}
