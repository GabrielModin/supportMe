package org.supportmeinc.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.supportmeinc.ImageUtils;
import org.supportmeinc.MainController;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.UUID;

public class GuideEditorUi implements JFXcontroller, Initializable {

    private MainController controller;

    @FXML private Label lblTitlePreview, lblCardTextPreview, yesCardSelected, noCardSelected;
    @FXML private ImageView imgPreview;
    @FXML private TextField txtCardTitle, txtFilePath;
    @FXML private TextArea txtCardText;
    @FXML private ComboBox<String> cmbYes, cmbNo;
    @FXML private ListView<String> listView;

    private ArrayList<UUID> guideCardUUID;

    private Alert alert = new Alert(Alert.AlertType.WARNING);

    private String title = null;
    private String text = null;
    private UUID yesUUID = null;
    private UUID noUUID = null;
    private UUID cardUUID;
    private byte[] img = null;


    public GuideEditorUi() {
        this.listView = new ListView<>();
    }

    public void initData(MainController controller){
        this.controller = controller;
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }



    public void repopulateLists() {
        guideCardUUID = null;
        guideCardUUID = new ArrayList<>(Arrays.asList(controller.getGuideEditorCardUUIDs()));

        cmbYes.getItems().clear();
        cmbNo.getItems().clear();
        listView.getItems().clear();

        for (UUID uuid : guideCardUUID) {

            cmbYes.getItems().add(controller.getCardTitle(uuid));
            cmbNo.getItems().add(controller.getCardTitle(uuid));
            listView.getItems().add(controller.getCardTitle(uuid));

        }
    }

    public void updateTitlePreview() { //TODO add alert
        String cardTitle = txtCardTitle.getText();

        if (cardTitle.length() > 20) {
            cardTitle = cardTitle.substring(0, 20);
            txtCardTitle.setText(cardTitle);
        }

        title = cardTitle;
        lblTitlePreview.setText(cardTitle);
    }

    public void updateTextPreview() { //TODO add alert
        String cardText = txtCardText.getText();

        if (cardText.length() > 280) {
            cardText = cardText.substring(0, 280);
            txtCardText.setText(cardText);
        }

        text = cardText;
        lblCardTextPreview.setText(cardText);
    }

    public void openSelectedCard() { //TODO early version, not tested with actual card
        //Vad gör denna????
//        UUID selectedCard = guideCardUUID.get(listView.getSelectionModel().getSelectedIndex());
//        System.out.println("opening : " + selectedCard);
//        if (selectedCard != null) {
//            lblTitlePreview.setText(controller.getCardTitle(selectedCard));
//            imgPreview.setImage(ImageUtils.toImage(controller.getCardImage(selectedCard)));
//            lblCardTextPreview.setText(controller.getCardText(selectedCard));
//        }
    }

    public void selectImage() {
        File file = controller.jfxFileChooser();
        String fileName = file.toString();
        int index = fileName.lastIndexOf(".");
        String extension = fileName.substring(index+1);
        System.out.println(extension);

        if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")) {
            byte[] byteFile = ImageUtils.toBytes(file);
            Image image = ImageUtils.toImage(byteFile);
            imgPreview.setImage(image);
            txtFilePath.setText(fileName);
            img = ImageUtils.toBytes(file);

        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("File type warning");
            alert.setHeaderText("Could not add selected image to Card");
            alert.setContentText("Selected file must be of type .png or .jpg, please try again");
            alert.show();
        }
    }

    public void save() {
        if (cmbYes.getSelectionModel().getSelectedItem() != null) {
            yesUUID = guideCardUUID.get(cmbYes.getSelectionModel().getSelectedIndex());
        }
        if (cmbNo.getSelectionModel().getSelectedItem() != null) {
            noUUID = guideCardUUID.get(cmbNo.getSelectionModel().getSelectedIndex());
        }
        if(!txtCardTitle.getText().isBlank()) {
            title = txtCardTitle.getText();
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No title added");
            alert.setHeaderText("Can't create card without title");
            alert.setContentText("Please fill in title");
            alert.show();
            return;
        }

        if (text.length() > 280) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Card text cannot be more than 280 characters");
            alert.setHeaderText("Can't create card with text more than 280 characters");
            alert.setContentText("If your card is two steps, please divide them");
            alert.show();
            return;
        }

        controller.saveCard(title, text, img, yesUUID, noUUID, cardUUID);
        System.out.println("saving : " + cardUUID);

        repopulateLists();
    }

    public void loadCardOnListSelection() {
        if(listView.getSelectionModel().isSelected(listView.getSelectionModel().getSelectedIndex())) {
            if(listView.getSelectionModel().getSelectedItem() != null) {
                cardUUID = guideCardUUID.get(listView.getSelectionModel().getSelectedIndex());

                title = controller.getCardTitle(cardUUID);
                text = controller.getCardText(cardUUID);
                img = controller.getCardImage(cardUUID);
                yesUUID = controller.getCardAffirmUUID(cardUUID);
                noUUID = controller.getCardNegUUID(cardUUID);

                System.out.println("yes : " + yesUUID);
                System.out.println("no : " + noUUID);


                System.out.println("opening : " + cardUUID);
                System.out.println(text);
                System.out.println(title);

                txtCardText.setText(text);
                txtCardTitle.setText(title);
                imgPreview.setImage(ImageUtils.toImage(img));

                updateTextPreview();
                updateTitlePreview();
            }
        }
    }

    public void removeCard() {
        if(listView.getSelectionModel().isSelected(listView.getSelectionModel().getSelectedIndex())) {
            controller.removeCard(guideCardUUID.get(listView.getSelectionModel().getSelectedIndex()));
        } else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No card selected");
            alert.setHeaderText("Couldn't remove card");
            alert.setContentText("Please select a card to be deleted");
            alert.show();
        }
        repopulateLists();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void createNewCard(ActionEvent actionEvent) {
        title = null;
        text = null;
        yesUUID = null;
        noUUID = null;
        cardUUID = null;
        img = null;

        txtCardText.setText("");
        txtCardTitle.setText("");
        imgPreview.setImage(null);

        updateTitlePreview();
        updateTextPreview();

        cardUUID = controller.createNewCard();
        repopulateLists();
    }

    public void cmbYesSelect(ActionEvent actionEvent) {
        System.out.println(yesUUID);
        System.out.println(controller.getCardTitle(yesUUID));
    }

    public void cmbNoSelect(ActionEvent actionEvent) {
        System.out.println(noUUID);
        System.out.println(controller.getCardTitle(noUUID));
    }
}
