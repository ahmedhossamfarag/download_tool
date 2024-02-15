package com.example.downloadui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainView {
    @FXML
    Label localDir;

    @FXML
    HBox selectDir;

    @FXML
    TextField webPage;

    @FXML
    Label start;


    public void setSelectDir(){
        DirectoryChooser chooser = new DirectoryChooser();
        File f  = chooser.showDialog(selectDir.getParent().getScene().getWindow());
        if(f != null)
            localDir.setText(f.getAbsolutePath());
    }

    public void startDownload(){
        String dir = localDir.getText();
        FXMLLoader loader = new FXMLLoader(MainView.class.getResource("ProgressView.fxml"));
        Stage stage = (Stage) start.getParent().getScene().getWindow();
        try {
            stage.setScene(new Scene(loader.load()));
            ProgressView view = loader.getController();
            LinkList list = new LinkList(view);
            String s = webPage.getText();
            list.push(s);
            view.download = new Download(dir, s.substring(0, s.indexOf('/', "https://".length())), list);
            view.download.operate();
        } catch (IOException e) {

        }

    }


}
