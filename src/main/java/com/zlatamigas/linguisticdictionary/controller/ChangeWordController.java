package com.zlatamigas.linguisticdictionary.controller;

import com.zlatamigas.linguisticdictionary.model.entity.AnnotatedWord;
import com.zlatamigas.linguisticdictionary.model.entity.TagsInfo;
import com.zlatamigas.linguisticdictionary.util.ActionDialogs;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class ChangeWordController implements Initializable {

    @FXML
    Button btOk;
    @FXML
    Button btCancel;
    @FXML
    TextField tfWord;
    @FXML
    ComboBox<String> combobChooseTag;
    @FXML
    TextField tfOldWord;
    @FXML
    ComboBox<String> combobOldTag;
    @FXML
    CheckBox cbAutoTag;

    private EditAnnotatedTextController parentController;
    private Stage stage;

    private AnnotatedWord recordToChange;
    private int idRecordToChange;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        combobChooseTag.getItems().setAll(TagsInfo.getTagNames());
        combobChooseTag.setValue("");
        combobChooseTag.setVisibleRowCount(10);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setParentController(EditAnnotatedTextController parentController) {
        this.parentController = parentController;
    }

    public EditAnnotatedTextController getParentController() {
        return parentController;
    }

    public void setRecordToChange(AnnotatedWord recordToChange) {
        this.recordToChange = recordToChange;

        tfWord.setText(recordToChange.getWord());
        tfOldWord.setText(recordToChange.getWord());
        combobOldTag.setValue(recordToChange.getTag());
        combobOldTag.setDisable(true);
        combobChooseTag.setValue(recordToChange.getTag());
    }

    public void setIdRecordToChange(int idRecordToChange) {
        this.idRecordToChange = idRecordToChange;
    }

    public void onOkListener(ActionEvent actionEvent) {

        String newWord = tfWord.getText();

        if (newWord != null) {

            if (newWord.equals("") | newWord.contains(" ")) {

                ActionDialogs.showAlertDialog(
                        Alert.AlertType.WARNING,
                        "Невозможно изменить слово",
                        "Недопустимо использование пустых слов и пробелов!",
                        "Недоступный формат слова!"
                );

                return;
            }

            getParentController().setMenuBarInteractive(false);

            String tag = combobChooseTag.getSelectionModel().getSelectedItem();

            if (cbAutoTag.isSelected()) {
                Properties props = new Properties();
                props.setProperty("annotators", "tokenize,ssplit,pos");
                StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

                tag = pipeline.processToCoreDocument(newWord).tokens().get(0).tag();
            }

            AnnotatedWord newRecord = new AnnotatedWord(newWord, tag);

            parentController.addWord(newRecord, idRecordToChange);
        } else {
            stageClose();
        }

        getParentController().setMenuBarInteractive(true);
        stageClose();
    }

    public void stageClose() {
        getStage().close();
    }

    public void onCancelListener(ActionEvent actionEvent) {
        stageClose();
    }
}
