package com.zlatamigas.linguisticdictionary.controller;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import com.zlatamigas.linguisticdictionary.model.entity.TagsInfo;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowTagsController implements Initializable {

    @FXML
    TextArea taDescription;
    @FXML
    ListView<String> lvTags;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        taDescription.setWrapText(true);

        lvTags.getItems().setAll(TagsInfo.getTagNames());
        lvTags.getSelectionModel().selectedItemProperty().addListener(this::searchingTypeSelectedListener);

    }

    private void searchingTypeSelectedListener(ObservableValue<? extends String> observableValue, String s, String t1) {

        int i = lvTags.getSelectionModel().getSelectedIndex();
        taDescription.setText(TagsInfo.getTagDescription(i));
    }

}
