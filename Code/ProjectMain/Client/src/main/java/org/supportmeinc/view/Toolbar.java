package org.supportmeinc.view;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.supportmeinc.MainController;
import org.supportmeinc.SceneName;

import java.io.IOException;

public class Toolbar implements JFXcontroller {

    private MainController controller;

    @FXML private BorderPane borderPane;

    public void initData(MainController controller){
        this.controller = controller;
        controller.registerToolbar(this);
        //controller.registerController(this);
    }

    public void homeButton() throws IOException {
      //  borderPane.setCenter(controller.loadFXML("guideBrowser"));
//        controller.testCard();
    }

    public void createNewGuide() throws IOException {
     //   borderPane.setCenter(controller.loadFXML("guideEditor"));
    }

    public void swapScene(AnchorPane pane) {
        borderPane.setCenter(pane);
    }

} //class end