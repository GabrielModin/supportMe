package org.supportmeinc.view;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.supportmeinc.AlertUtils;
import org.supportmeinc.Main;
import org.supportmeinc.MainController;
import org.supportmeinc.model.GuideManager;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;

public class GuideBrowser implements JFXcontroller, Initializable { //Class begin

    public VBox vNailBox;
    private MainController controller;
    private ArrayList<ThumbnailItem> thumbnailItems = new ArrayList<>();
    private UUID currentUUID;
    private ThumbnailItem thumbnailItem;

    @FXML private FlowPane flowPane, flowPaneSaved, flowPaneDownloaded;
    @FXML private ScrollPane scrollPane;
    @FXML private Button btnEdit, btnDelete, btnOpen, btnCreate, btnDownload, btnSearch;
    @FXML private TextField searchField;

    public void initData(MainController controller){
        this.controller = controller;
        controller.setGuideBrowser(this);
    }

    public void openGuide(UUID uuid) { //called from Right-click context menu in GuideBrowser-GUI
        controller.openGuide(uuid);
    }

    public void openGuide() { //called from Open Guide button in GuideBrowser-GUI
        controller.openGuide(currentUUID);
    }

    public void editGuide() {
        controller.setEditGuide(currentUUID);
    }

    public void deleteGuide() {
        boolean bool = thumbnailItem.getAuthor();

        if (bool) {
            controller.deleteGuide(currentUUID);
            AlertUtils.alertInformation("Guide deleted", "Selected Guide was deleted", "Success!");
        }
        else {
            controller.removeSelfAccess(currentUUID);
            AlertUtils.alertInformation("Guide removed", "Selected Guide was removed from your list", "Success");
        }

        controller.refreshThumbnails();
    }

    public void downloadGuide() {
        controller.downloadGuide(currentUUID);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void resetView() {
        thumbnailItems = new ArrayList<>();
        flowPane.getChildren().clear();
        flowPaneSaved.getChildren().clear();
        flowPaneDownloaded.getChildren().clear();
    }

    public void createNewGuide() {
        controller.createNewGuide();
    }

    public void setSelectedThumbnail(ThumbnailItem thumbnailItem) { //called when thumbnail is clicked
        if (this.thumbnailItem != null) {
            this.thumbnailItem.deSelected();
        }
        this.currentUUID = thumbnailItem.getUUID();
        this.thumbnailItem = thumbnailItem;
        btnEdit.setDisable(!thumbnailItem.getAuthor());
        //btnDelete.setVisible(author);
    }

    public void addThumbnailAuthor(String title, byte[] image, String description, UUID guideUUID) {
        ThumbnailItem item = null;
        AnchorPane anchorPane = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/thumbnail.fxml"));

            anchorPane = loader.load();
            item = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (item != null && anchorPane != null) {
            item.setData(title, image, description, guideUUID, this, true);
            thumbnailItems.add(item);
            flowPane.getChildren().add(anchorPane);
        }
    }

    public GuideBrowser(){

    }

    public void addThumbnailAccess(String title, byte[] image, String description, UUID guideUUID) {
        ThumbnailItem item = null;
        AnchorPane anchorPane = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/thumbnail.fxml"));

            anchorPane = loader.load();
            item = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (item != null && anchorPane != null) {
            item.setData(title, image, description, guideUUID, this, false);
            thumbnailItems.add(item);
            flowPaneSaved.getChildren().add(anchorPane);
        }
    }

    public void addThumbnailDownloaded(String title, byte[] image, String description, UUID guideUUID) {
        ThumbnailItem item = null;
        AnchorPane anchorPane = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/thumbnail.fxml"));

            anchorPane = loader.load();
            item = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (item != null && anchorPane != null) {
            item.setData(title, image, description, guideUUID, this, false);
            thumbnailItems.add(item);
            flowPaneDownloaded.getChildren().add(anchorPane);
        }
    }

    public void offlineMode() {
        btnEdit.setDisable(true);
        btnCreate.setDisable(true);
        btnDownload.setDisable(true);
        btnDelete.setDisable(true);
    }

    public void onlineMode() {
        btnEdit.setDisable(false);
        btnCreate.setDisable(false);
        btnDownload.setDisable(false);
        btnDelete.setDisable(false);
    }
    

    public void search() {
        controller.refreshThumbnails(searchField.getText());
    }
} //class end
