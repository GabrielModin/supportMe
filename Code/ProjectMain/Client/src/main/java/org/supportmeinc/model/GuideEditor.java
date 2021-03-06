package org.supportmeinc.model;

import org.supportmeinc.AlertUtils;
import org.supportmeinc.MainController;
import shared.Card;
import shared.Guide;
import shared.Thumbnail;

import java.util.HashMap;
import java.util.UUID;

public class GuideEditor {

    private Guide outputGuide;
    private HashMap<UUID,Card> cardsList;
    private Card currentCard;
    private Card descriptionCard;
    private UUID guideUUID = UUID.randomUUID();
    private Thumbnail thumbnail;
    private MainController controller;
    private UUID firstCard;

    public UUID getGuideUUID() {
        return guideUUID;
    }

    public GuideEditor(MainController controller) {
        this.controller = controller;
        cardsList = new HashMap<>();
    }

    public void setEditGuide(Guide guide) {
        HashMap<UUID,Card> temp = new HashMap<>();
        for (Card card : guide.getCards()) {
            temp.put(card.getCardUUID(), card);
        }
        this.cardsList = temp;
        this.descriptionCard = guide.getDescriptionCard();
        System.out.println("Set desc card to :" + descriptionCard.getCardUUID());
        this.guideUUID = guide.getGuideUUID();
        this.outputGuide = guide;
        this.firstCard = guide.getDescriptionCard().getAffirmUUID();
    }

    public void saveCard(String title, String description, byte[] img, UUID affirmUUID, UUID negativeUUID, UUID cardUUID) {
        if (title != null) {
            currentCard = new Card(cardUUID);
            currentCard.setTitle(title);
            currentCard.setText(description);
            currentCard.setAffirmUUID(affirmUUID);
            currentCard.setNegUUID(negativeUUID);
            currentCard.setImage(img);

            if (cardsList.containsKey(cardUUID)) {
                cardsList.replace(cardUUID, currentCard);
                System.out.println("replaced");
            } else {
                cardsList.put(cardUUID, currentCard);
                System.out.println("put");
            }
        }

    }

    public void removeCard(UUID cardUUID) {
        cardsList.remove(cardUUID);
    }

    public HashMap<UUID, Card> getCardsList() {
        return cardsList;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void createNewCard() {
        currentCard = new Card();
    }

    //Called from packGuide, creates a description card containing similar information to the thumbnail.
    public void setDescription(String title, String description, byte[] img, Guide guide) {
        Card card = new Card();
        card.setText(description);
        card.setTitle(title);
        card.setImage(img);
        card.setNegUUID(null);
        card.setAffirmUUID(firstCard);
        cardsList.put(card.getCardUUID(), card);

        Thumbnail thumbnail = new Thumbnail(guideUUID);
        thumbnail.setDescription(card.getText());
        thumbnail.setTitle(card.getTitle());
        thumbnail.setImage(card.getImage());

        this.descriptionCard = card;
        this.thumbnail = thumbnail;
    }

    public String getCardTitle(UUID uuid) {
        String retVal = null;

        if(cardsList.containsKey(uuid)) {
            retVal = cardsList.get(uuid).getTitle();
        }
        return retVal;
    }

    public String getCardText(UUID uuid){
        return cardsList.get(uuid).getText();
    }
    public UUID getCardAffirmUUID(UUID uuid){
        return cardsList.get(uuid).getAffirmUUID();
    }
    public UUID getCardNegUUID(UUID uuid){
        return cardsList.get(uuid).getNegUUID();
    }
    public byte[] getCardImage(UUID uuid){
        return cardsList.get(uuid).getImage();
    }
    public Guide getOutputGuide() {
        return outputGuide;
    }

    /*
    Takes in parameters to create the description card & thumbnail.
    Creates and stores all data in a new Guide object as outputGuide.
     */
    public void packGuide(String title, String description, byte[] img) {
        for (Card card : cardsList.values()) {
            if (card.getTitle() == null) {
                removeCard(card.getCardUUID());
            }
        }
        if (descriptionCard != null) {
            cardsList.remove(descriptionCard.getCardUUID());
        }
        Guide returnGuide = new Guide(guideUUID);
        setDescription(title, description, img, returnGuide);
        returnGuide.setCards(cardsList.values().toArray(new Card[0]));
        returnGuide.setDescriptionCard(descriptionCard);
        returnGuide.setThumbnail(thumbnail);
        returnGuide.setAuthor(controller.getAuthor());
        System.out.println(controller.getAuthor());
        this.outputGuide = returnGuide;

    }

    public boolean checkCardLinksValid() {
        for (Card card : cardsList.values()) {
            if (card.getTitle() == null) {
                cardsList.remove(card.getCardUUID());
            }
        }
        boolean boolReturn;
        int ok = 0;
        HashMap<UUID,Card> compareList = cardsList;

        if (descriptionCard != null) {
            compareList.remove(descriptionCard.getCardUUID());
        }
        
        for (Card card : cardsList.values()) {
            if (card.getNegUUID() == null && card.getAffirmUUID() == null){
                ok++;
                System.out.println("Card set as first: " + card.getTitle());
            } else if (card.getNegUUID() == null || card.getAffirmUUID() == null) {
                ok = -1;
                System.out.println("Card missing links");
                AlertUtils.alertError("Card : " + card.getTitle() + " missing links", "The card only contains one reference to another card", "Please ensure that the card contains both upcoming cards!");
            }
        }

        if(ok != 1) {
            boolReturn = false;
        } else {
            boolReturn = true;
        }
        return boolReturn;
    }

    public String getGuideTitle() {
        return outputGuide.getThumbnail().getTitle();
    }

    public String getGuideDescription() {
        return outputGuide.getThumbnail().getTitle();
    }

    public Card getCard(UUID uuid){
        return cardsList.get(uuid);
    }

    public void setFirstCard(UUID cardUUID) {
        this.firstCard = cardUUID;
        System.out.println(firstCard);
    }

    public Card getDescriptionCard() {
        return descriptionCard;
    }

    public UUID getFirstCard() {
        return firstCard;
    }
}
