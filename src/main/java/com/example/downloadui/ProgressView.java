package com.example.downloadui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.shape.SVGPath;

public class ProgressView {

    @FXML
    SVGPath stop, play;
    @FXML
    Label remain, done, link;


    Download download;

    public void playClick(){
        stop.setVisible(true);
        download.operate();
    }

    public void stopClick(){
        stop.setVisible(false);
        download.active = false;
        download.thread.interrupt();
    }
}
