package com.livejournal.lofe.lofe;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WSServer extends WebSocketServer {

    private WebSocketServer wss;

    public WSServer() {
        super( new InetSocketAddress( 8887 ) );
        //start();
        MyUtil.log("Запущен сервер на порту 8887");
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        conn.send("Welcome to the серрвер!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
        MyUtil.log("Новое соединение");
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        broadcast( conn + " has left the room!" );
        System.out.println( conn + " has left the room!" );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        broadcast( message );
        System.out.println( conn + ": " + message );
    }
    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        broadcast( message.array() );
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}
