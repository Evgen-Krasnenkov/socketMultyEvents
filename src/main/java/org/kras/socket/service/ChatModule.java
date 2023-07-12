package org.kras.socket.service;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.kras.socket.model.Message;
import org.kras.socket.model.Speech;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChatModule {
    private final SocketIONamespace namespace;

    @Autowired
    public ChatModule(SocketIOServer server) {
        this.namespace = server.addNamespace("/room");
        this.namespace.addConnectListener(onConnected());
        this.namespace.addDisconnectListener(onDisconnected());
        this.namespace.addEventListener("ttsrequest", Message.class, onTextToSpeechReceived());
        this.namespace.addEventListener("sttrequest", Speech.class, onSpeechToTextReceived());
    }

    private DataListener<Speech> onSpeechToTextReceived() {
        return (client, data, ackSender) -> {
            log.info("Client[{}] - Received chat message '{}'", client.getSessionId().toString(), data);
            namespace.getBroadcastOperations().sendEvent("sttresponse", data);
        };
    }

    private DataListener<Message> onTextToSpeechReceived() {
        return (client, data, ackSender) -> {
            log.info("Client[{}] - Received chat message '{}'", client.getSessionId().toString(), data);
            namespace.getBroadcastOperations().sendEvent("ttsresponse", data);
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            HandshakeData handshakeData = client.getHandshakeData();
            log.info("Client[{}] - Connected to chat module through '{}'", client.getSessionId().toString(), handshakeData.getUrl());
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Client[{}] - Disconnected from chat module.", client.getSessionId().toString());
        };
    }

}
