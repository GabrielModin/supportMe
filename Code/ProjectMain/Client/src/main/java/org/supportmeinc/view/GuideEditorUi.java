package org.supportmeinc.view;

import org.supportmeinc.Main;

public class GuideEditorUi implements JFXcontroller {

    Main controller;

    public void initData(Main controller){
        this.controller = controller;
        controller.registerController(this);
    }

    public GuideEditorUi(){

    }
}