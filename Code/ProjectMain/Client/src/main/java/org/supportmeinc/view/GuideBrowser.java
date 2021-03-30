package org.supportmeinc.view;

import org.supportmeinc.Main;

public class GuideBrowser implements JFXcontroller {


    private Main controller;

    public void initData(Main controller){
        this.controller = controller;
        controller.registerController(this);

    }

    public GuideBrowser(){

    }

    public void getGuide(int index) {
        controller.initGuide(index);
    }

}