package com.zlatamigas.linguisticdictionary.controller;

import com.zlatamigas.linguisticdictionary.model.annotator.AnnotateText;
import com.zlatamigas.linguisticdictionary.model.builder.AnnotatorBuilder;
import com.zlatamigas.linguisticdictionary.model.builder.DictionaryBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.zlatamigas.linguisticdictionary.model.entity.*;
import com.zlatamigas.linguisticdictionary.util.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.zlatamigas.linguisticdictionary.controller.navigation.PageNavigation.*;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.ICON;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.STYLE_FILE;

/**
 * Controller for MainMenu.fxml.
 */
public class MainMenuController implements Initializable {

    @FXML
    public Button btAnnAdd;
    @FXML
    public Button btDictEdit;
    @FXML
    public Button btDictDelete;
    @FXML
    public Button btDictView;
    @FXML
    public Button btAnnRead;
    @FXML
    public Button btDictAdd;
    public Button btAnnView;

    private Stage stage;
    private Scene scene;
    private Parent root;


    AnnotatorBuilder annotatorBuilder;
    DictionaryBuilder dictionaryBuilder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        annotatorBuilder = new AnnotatorBuilder();
        dictionaryBuilder = new DictionaryBuilder();

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void annAddListener(ActionEvent actionEvent) throws IOException {

        FileChooser fileChooser  = new FileChooser();
        fileChooser.setTitle("Аннотировать");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Все файлы", "*.*"));
        File file = fileChooser.showOpenDialog(this.stage);

        if (file == null)
            return;


        FXMLLoader loader = new FXMLLoader(getClass().getResource(EDIT_ANNOTATED_TEXT));
        Parent root = (Parent) loader.load();

        EditAnnotatedTextController controller = (EditAnnotatedTextController) loader.getController();

        controller.setCreateNew(true);
        controller.setMainMenuController(this);
        controller.setStage(this.stage);
        controller.setAnnotatedWords(AnnotateText.annotateText(file.getAbsolutePath()));
        controller.setAnnotatedTextToFind(file.getName());

        this.stage.setTitle("Аннотирование текста: "+ file.getName());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        this.stage.setScene(scene);
        this.stage.show();
    }

    public void annOpenListener(ActionEvent actionEvent) throws IOException {

        String fileName = ActionDialogs.selectItemFromListDialog("Выберите аннотированный текст:", "Открыть текст", annotatorBuilder.getRealTimeAnnotatedIndex());

        if(fileName==null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(EDIT_ANNOTATED_TEXT));
        Parent root = (Parent) loader.load();

        EditAnnotatedTextController controller = (EditAnnotatedTextController) loader.getController();

        controller.setCreateNew(false);
        controller.setMainMenuController(this);
        controller.setStage(this.stage);
        controller.setAnnotatedWords(annotatorBuilder.readAnnotatedFile(fileName));
        controller.setAnnotatedTextToFind(fileName);

        this.stage.setTitle("Просмотр аннотированного текста: " + fileName);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        this.stage.setScene(scene);
        this.stage.show();
    }

    public void annViewListener(ActionEvent actionEvent) throws IOException {

        ActionDialogs.selectItemFromListDialog("Доступные тексты:", "Аннотированные тексты",
                annotatorBuilder.getRealTimeAnnotatedIndex());
    }

    public void annDeleteListener(ActionEvent actionEvent) throws IOException {
        String fileName = ActionDialogs.selectItemFromListDialog("Выберите аннотированный текст:", "Открыть текст", annotatorBuilder.getRealTimeAnnotatedIndex());

        if(fileName==null)
            return;


        if(!ActionDialogs.showAlertDialog(
                Alert.AlertType.CONFIRMATION,
                "Удаление текста",
                "Вы уверены, что хотите удалить текст "+ fileName + "?",
                "Вы не сможете восстановить удаленные данные. " +
                        "Текст будет удалён из всех словарей, без удаления входящих в них статистики по словам."))
            return;

        if(annotatorBuilder.deleteAnnotatedFile(fileName)) {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Удаление текста",
                    "Текст " + fileName + " успешно удален!" ,
                    "");
        }
        else {

            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Удаление текста",
                    "При удалении текста " + fileName + " произошла ошибка!" ,
                    "");
        }
    }

    public void annStatistics(ActionEvent actionEvent) throws IOException {

        String fileName = ActionDialogs.selectItemFromListDialog("Выберите словарь:", "Словари", dictionaryBuilder.getRealTimeDictionaryIndex());

        if(fileName==null)
            return;

        ArrayList<String> texts = dictionaryBuilder.getAnnotatedTextsFromIndFile(fileName);
        if(texts==null){
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(STATISTICS));
        root = (Parent) loader.load();
        StatisticsController controller = (StatisticsController) loader.getController();
        scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());


        controller.setStage(stage);
        controller.setUsedTexts(texts);
        controller.setStage(this.stage);

        this.stage.setTitle("Статистика: " + fileName);
        this.stage.setScene(scene);
        this.stage.show();
    }

    public void dictAddListener(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(EDIT_DICTIONARY));
        root = (Parent) loader.load();
        EditDictionaryController controller = (EditDictionaryController) loader.getController();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());


        String s = "";

        TextInputDialog dialog = new TextInputDialog("");

        dialog.setTitle("Введите название файла");
        dialog.setHeaderText("Название словаря:");
        dialog.setContentText("");
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image(MainMenuController.class.getResourceAsStream(ICON)));


        ButtonType ok = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            s = result.get();
        }
        else
            return;


        if(s.equals("")) {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Некорректное название словаря!",
                    "Название не может быть пустым!",
                    "");
            return;
        }


        if(DictionaryBuilder.contains(s)) {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Некорректное название словаря!",
                    "Словарь с таким названием уже существует!",
                    "Выберете другое название для нового словаря.");
            return;
        }

        controller.setCreateNew(true);
        controller.setDictionaryName(s);

        controller.setDictionary(new Dictionary(new ArrayList<>(), new ArrayList<>()));
        controller.setStage(this.stage);
        controller.setParentController(this);

        this.stage.setTitle("Создать новый словарь: " + s);
        this.stage.setScene(scene);
        this.stage.show();
    }

    public void dictEditListener(ActionEvent actionEvent) throws IOException {

        String fileName = ActionDialogs.selectItemFromListDialog("Выберите словарь:", "Открыть словарь", dictionaryBuilder.getRealTimeDictionaryIndex());

        if(fileName==null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(EDIT_DICTIONARY));
        root = (Parent) loader.load();
        EditDictionaryController controller = (EditDictionaryController) loader.getController();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        controller.setCreateNew(false);
        controller.setDictionaryName(fileName);

        controller.setDictionary(dictionaryBuilder.readDictionary(fileName));
        controller.setStage(this.stage);
        controller.setParentController(this);

        this.stage.setTitle("Словарь: " + fileName);
        this.stage.setScene(scene);
        this.stage.show();
    }

    public void dictDeleteListener(ActionEvent actionEvent) throws IOException {
        String fileName = ActionDialogs.selectItemFromListDialog("Выберите словарь:", "Удалить словарь", dictionaryBuilder.getRealTimeDictionaryIndex());

        if(fileName==null)
            return;

        if(!ActionDialogs.showAlertDialog(
                Alert.AlertType.CONFIRMATION,
                "Удаление словаря",
                "Вы уверены, что хотите удалить словарь "+ fileName + "?",
                "Вы не сможете восстановить удаленные данные."))
            return;

        if(dictionaryBuilder.deleteDictionary(fileName)) {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Удаление словаря",
                    "Словарь " + fileName + " успешно удален!" ,
                    "");
        }
        else {

            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Удаление словаря",
                    "При удалении словаря " + fileName + " произошла ошибка!" ,
                    "");
        }

    }

    public void dictViewListener(ActionEvent actionEvent) throws IOException {

        ActionDialogs.selectItemFromListDialog("Доступные словари:", "Словари",
                dictionaryBuilder.getRealTimeDictionaryIndex());
    }
}
