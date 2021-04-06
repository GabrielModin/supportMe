package org.supportmeinc.model;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import shared.*;

public class Connection {

    private Socket socket;
    private GuideManager guideManager;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private User user;
    private Receive receive;
    private Send send;

    public Connection(String ip, int port, User user) {
        this.user = user;
        try {
            socket = new Socket(ip, port);

            receive = new Receive();
            send = new Send();

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        send.start();
        receive.start();

    }

    private class Receive extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    ois.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Buffer<Object> objectBuffer = new Buffer<>();

    public void send(Object object){
        objectBuffer.put(object);
    }

    private class Send extends Thread {

        @Override
        public void run() {
            send(user);
            try {
                while (true) {
                    try {
                        oos.writeObject(objectBuffer.get());
                        oos.flush();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Guide getGuide(UUID guideUUID) {
        return goodLordTheCardGiver();
    }

    public Guide goodLordTheCardGiver() {
        Guide guide = new Guide();
        Card card = new Card();
        card.setTitle("The last guide you will ever need");
        card.setText("Step 1 get rich, step 2 ???, setp 3 profit");
        card.setImage(JfxUtils.toBytes("src/main/resources/org/supportmeinc/FinalLogotyp.png"));
        guide.setDescriptionCard(card);
        return guide;
    }

    public Thumbnail[] getThumbnails() {
        return new Thumbnail[]{new Thumbnail(UUID.randomUUID())};
    }

    public void disconnect() {
        try {
            socket.close();
//            guideManager.loadGuides();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGuideManager(GuideManager manager) {
        guideManager = manager;
    }

}
