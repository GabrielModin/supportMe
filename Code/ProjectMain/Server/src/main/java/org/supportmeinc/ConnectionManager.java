package org.supportmeinc;

import shared.Thumbnail;
import shared.User;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedList;

public class ConnectionManager implements Runnable, ObjectReceivedListener{

    private ServerSocket serverSocket;
    private Thread acceptConnectionThread;
    HashMap<User,Connection> userConnection;

    public ConnectionManager(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        start();
    }

    private void start(){;
        if (acceptConnectionThread == null) {
            acceptConnectionThread = new Thread(this);
        } else {
            return;
        }
        acceptConnectionThread.start();
    }

    LinkedList<Connection> newConnections = new LinkedList<>();

    @Override
    public void run() {
        while (!Thread.interrupted()){
            try {
                Connection connection = new Connection(serverSocket.accept());
                connection.setObjectReceivedListener(this);
                newConnections.add(connection);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void objectReceived(Object object, User user) {

        if (object instanceof User){
            for (Connection connection: newConnections) {
                if(connection.getUser() == user){
                    userConnection.put(user, connection);
                    newConnections.remove(connection);
                }
            }
        }

        if (object instanceof Thumbnail){

        }

        if (object instanceof Thumbnail[]){

        }
    }
}
