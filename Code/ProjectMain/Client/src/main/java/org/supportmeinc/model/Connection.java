package org.supportmeinc.model;


import javafx.scene.control.Alert;
import org.supportmeinc.AlertUtils;
import shared.Guide;
import shared.Thumbnail;
import shared.User;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class Connection {

    private Socket socket;
    private GuideManager guideManager;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private User user;
    private Receive receive;
    private Send send;
    private ThumbnailListener listener;
    private Semaphore userReceived = new Semaphore(0);

    Buffer<Object> sendBuffer = new Buffer<>();
    Buffer<Object> receiveBuffer = new Buffer<>();

    public Connection(String ip, int port, User user) throws IOException {
        this.user = user;
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), 1000);

        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        inputStream = new ObjectInputStream(this.socket.getInputStream());

        send = new Send();
        receive = new Receive();

        send.start();
        receive.start();

    }

    public void registerListener(ThumbnailListener listener){
        this.listener = listener;
    }

    private void send(Object object){
        sendBuffer.put(object);
    }

    public Guide getGuide(Thumbnail thumbnail) throws InterruptedException{
        Guide retGuide = null;
        send(thumbnail);
        Object guide = receiveBuffer.get();
        if (guide instanceof Guide){
            retGuide = (Guide) guide;
        }
        return retGuide;
    }

    public Guide getGuide(UUID uuid) throws InterruptedException {
        Guide guide;
        guide = guideManager.getGuide(uuid);
        return guide;
    }

    public void getThumbnails(Thumbnail[] thumbnails) throws InterruptedException{
        send(thumbnails);
        Object returnAccessObject = receiveBuffer.get();
        Thumbnail[] returnAccess = null;
        boolean success;
        if (returnAccessObject instanceof Thumbnail[]) {
            returnAccess = (Thumbnail[]) returnAccessObject;
            success = true;
        } else {
            success = false;
        }

        Object returnAuthorObject = receiveBuffer.get();
        Thumbnail[] returnAuthor = null;
        if (returnAuthorObject instanceof Thumbnail[] && success) {
            returnAuthor = (Thumbnail[]) returnAuthorObject;
            success = true;
        } else {
            success = false;
        }

        if (success ) {
            listener.thumbnailsReceived(returnAccess, returnAuthor);
        }
    }

    public void grantAccess(UUID uuid, String email) { // grant access to email on guide from uuid
        String request = requestBuilder(requestType.grant, uuid.toString() + ":" + email);
        send(request);
    }

    public void revokeAccess(UUID uuid, String email) { // revoke access to email on guide from uuid
        String request = requestBuilder(requestType.revoke, uuid.toString() + ":" + email);
        send(request);
    }

    public void removeGuide(UUID uuid) { // revoke access to email on guide from uuid
        String request = requestBuilder(requestType.removeGuide, uuid.toString());
        send(request);
    }

    public String[] getAccessList(UUID uuid) { // get access list from guide uuid, null if no access on guide is granted
        String[] returnArray = {};
        String request = requestBuilder(requestType.getAccessList, uuid.toString());
        send(request);
        try {
            Object arrayObject = receiveBuffer.get();
            if (arrayObject instanceof String[]) {
                returnArray = (String[]) arrayObject;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return returnArray;
    }

    private String requestBuilder(requestType type, String data){
        String request = null;
        request = String.format("%s:%s",type.name(), data);
        return request;
    }

    public boolean saveGuide(Guide guide) {
        boolean success = false;
        send(guide);
        Object obj = null;

        try {
            obj = receiveBuffer.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(obj instanceof Boolean){
            success = (Boolean) obj;
            System.out.println("Save guide: " + obj);
        }
        return success;
    }

    public void disconnect() throws IOException{
        send.interrupt();
        receive.interrupt();
        socket.close();
    }

    public void setGuideManager(GuideManager manager) {
        guideManager = manager;
    }

    public User getUser()  {
        try {
            userReceived.acquire();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User returnUser = user;
        userReceived.release();

        return returnUser;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public void downloadGuide(UUID uuid) {
        Guide guide;
        try {
            guide = getGuide(uuid);
            ObjectOutputStream fileOutputStream = new ObjectOutputStream(new FileOutputStream(user.getEmail()+ ".dat"));
            fileOutputStream.writeObject(guide);
            AlertUtils.alertWarning("Guide successfully downloaded", "Guide successfully saved to file.", "Guide was successfully saved to file.");

        } catch (InterruptedException | IOException e) {
            AlertUtils.alertWarning("Guide couldn't be downloaded", "Guide could not find file", "Guide could not save guide to file.");
            e.printStackTrace();
        }
    }

    private class Receive extends Thread {

        @Override
        public void run() {
            try {
                Object userLogin = inputStream.readObject();
                if (userLogin instanceof User){
                    setUser((User) userLogin);
                    userReceived.release();
                } else {
                    disconnect();
                }

                while (!Thread.interrupted()) {

                    Object object = inputStream.readObject();
                    receiveBuffer.put(object);

                }
            } catch (IOException e) {
                try {
                    disconnect();
                } catch (IOException ex){

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class Send extends Thread {

        @Override
        public void run() {
            send(user);
            try {
                while (!Thread.interrupted()) {

                    Object object = sendBuffer.get();

                    outputStream.writeObject(object);

                    outputStream.flush();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                try {
                    disconnect();

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
