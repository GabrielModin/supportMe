package org.supportmeinc.model;

import shared.Card;
import shared.Guide;
import shared.Thumbnail;

import java.util.ArrayList;

public class GuideManager {

    private Guide currentGuide;
    private Guide[] guides;
    private Thumbnail[] thumbnails; // fixa plz
    private Connection connection;
    private Card[][] cards;
    private ArrayList<Card> cardArrayList;

    public GuideManager(Connection connection) {
        this.connection = connection;
        thumbnails = connection.getThumbnails();
    }

    public Card initGuide(int index) {
        currentGuide = connection.getGuide(thumbnails[index].getGuideUUID());
        return currentGuide.getDescriptionCard();
    }

    public Card getCard(boolean choice) {
        return null;
    }

    public Thumbnail[] getThumbnails() {
        return thumbnails;
    }

    public void createGuide() {
        boolean answer = true;
        String title = "wow"; //TODO: Exchange them agaisnt values taken from the gui like gui.getTitleFromGUI()
        String text = "woow";
        byte[] image = {0};
        currentGuide = new Guide();
        while (answer) {
            createCard(title, text, image);
            answer = false; //Change to gui.finishGuide();
        }
    }

    public void createCard(String title, String text, byte[] image) {
        Card newCard = new Card();
        newCard.setTitle(title);
        while (text.length() > 280) {
            //text = gui.getTextFromGUI(); //TODO later on
        }
        newCard.setText(text);
        newCard.setImage(image);
        cardArrayList.add(newCard);
    }


}
