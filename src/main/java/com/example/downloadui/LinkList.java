package com.example.downloadui;

import javafx.application.Platform;

import java.util.LinkedList;

public class LinkList {
    ProgressView view;

    LinkedList<String> list;

    double remain, done;

    public LinkList(ProgressView view){
        this.view = view;
        list = new LinkedList<>();
    }

    public void push(String s){
        list.add(s);
        remain++;
        Platform.runLater(() -> view.remain.setText(String.valueOf(remain)));
    }


    public String top(){
        if(list.isEmpty())
            return null;
        Platform.runLater(() ->{ if(!list.isEmpty()) view.link.setText(list.getFirst()); });
        return list.getFirst();
    }


    public void pop(){
        list.removeFirst();
        done++;
        Platform.runLater(() -> view.done.setText(String.valueOf(done)));
    }
}
