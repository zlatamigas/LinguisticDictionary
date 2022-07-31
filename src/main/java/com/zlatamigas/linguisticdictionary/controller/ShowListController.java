package com.zlatamigas.linguisticdictionary.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.List;

public class ShowListController {

    public Label lbInfo;
    public ListView lvInfoList;
    public Button btChoose;

    private boolean isConfirmed = false;

    public void setLabelText(String name) {
        lbInfo.setText(name);
    }

    public void setListData(List<String> list) {
        Collections.sort(list);
        lvInfoList.setItems(FXCollections.observableArrayList(list));
    }

    public boolean getIsConfirmed(){
        return isConfirmed;
    }

    public String getSelectedItem() {
        return (String) lvInfoList.getSelectionModel().getSelectedItem();
    }
    public void chooseListener(ActionEvent event) {

        isConfirmed = true;

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
