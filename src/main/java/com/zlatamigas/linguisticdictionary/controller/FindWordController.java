package com.zlatamigas.linguisticdictionary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import com.zlatamigas.linguisticdictionary.model.entity.*;
import com.zlatamigas.linguisticdictionary.service.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller for FindWord.fxml.
 */
public class FindWordController{

    @FXML
    private GridPane gpMainPane;
    @FXML
    private Button btOk;
    @FXML
    private Button btCancel;
    @FXML
    private Button btRestore;
    @FXML
    private Label lbParameter;
    @FXML
    private TextField tfParameter;

    private Dictionary dictionary;
    private EditDictionaryController parentController;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        this.getStage().setOnHidden(event -> {
            getParentController().refreshTable();
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void setParentController(EditDictionaryController parentController) {
        this.parentController = parentController;
        this.dictionary = parentController.getDictionary();
    }

    public EditDictionaryController getParentController() {
        return parentController;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }


    public void showIncorrectFindMessage(String header, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Ошибка поиска");
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.show();
    }


    public void okListener(ActionEvent actionEvent) {

        String parameter = tfParameter.getText();

        if ((parameter).equals("") | parameter.contains(" ")) {
            showIncorrectFindMessage("Слово не может быть пустым или содержать пробелы!", "");
            return;
        }

        ArrayList<WordTableInfo> foundWords = Finding.find(
                getParentController().getWordsInTablesInfo(),
                parameter);

        getParentController().refreshTable(foundWords);

    }

    public void cancelListener(ActionEvent actionEvent) throws IOException {
        getParentController().refreshTable();
        getStage().close();
    }

    public void restoreListener(ActionEvent actionEvent) {
        getParentController().refreshTable();
    }
}
