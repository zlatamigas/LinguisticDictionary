package com.zlatamigas.linguisticdictionary.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.zlatamigas.linguisticdictionary.model.entity.AnnotatedWord;
import com.zlatamigas.linguisticdictionary.util.ActionDialogs;
import com.zlatamigas.linguisticdictionary.model.builder.AnnotatorBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.zlatamigas.linguisticdictionary.controller.navigation.PageNavigation.*;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.ICON;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.STYLE_FILE;

/**
 * Controller for EditAnnotatedText.fxml.
 */
public class EditAnnotatedTextController implements Initializable {

    @FXML
    Menu mWord;
    @FXML
    MenuBar menuBar;
    @FXML
    MenuItem miChangeWord;
    @FXML
    MenuItem miSave;
    @FXML
    MenuItem miTags;
    @FXML
    TableView<AnnotatedWord> tableM;
    @FXML
    TableColumn<AnnotatedWord, String> tcWord;
    @FXML
    TableColumn<AnnotatedWord, String> tcTag;

    private Stage stage;
    private Scene scene;
    private Parent root;
    private MainMenuController mainMenuController;

    private ObservableList<AnnotatedWord> data;
    private ArrayList<AnnotatedWord> annotatedWords;

    private FileChooser fileChooser;

    private AnnotatorBuilder annotatorBuilder;

    private boolean createNew;
    private String annotatedTextToFind;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        annotatorBuilder = new AnnotatorBuilder();

        tcWord.setCellValueFactory(new PropertyValueFactory<>("Word"));
        tcWord.setSortable(false);
        tcTag.setCellValueFactory(new PropertyValueFactory<>("Tag"));
        tcTag.setSortable(false);
        data = FXCollections.observableArrayList();
        tableM.setItems(data);
        tableM.setPlaceholder(new Label("Подходящие данные отсутствуют"));
        annotatedWords = new ArrayList<>();
        refreshTable();

        fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
    }

    public void refreshTable() {

        int size = tableM.getItems().size();
        tableM.getItems().remove(0, size);

        if (this.annotatedWords == null)
            return;

        this.data = rebuildObservableList(this.annotatedWords);
        tableM.setItems(data);
        tableM.refresh();
    }

    public void setAnnotatedTextToFind(String annotatedTextToFind) {
        this.annotatedTextToFind = annotatedTextToFind;
    }

    public void setAnnotatedWords(ArrayList<AnnotatedWord> annotatedWords) {

        this.annotatedWords = annotatedWords;
        refreshTable();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void setCreateNew(boolean createNew) {
        this.createNew = createNew;

        if (!createNew) {
            mWord.setDisable(true);
            mWord.hide();
            miSave.setDisable(true);
        }
    }

    public boolean checkSelection(int i) {

        if (i == -1) {

            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Ошибка",
                    "Слово не выбрано!",
                    "Выберете объект из предложенных в таблице или, если\nтаблица пуста, " +
                            "предварительно добавьте данные в таблицу.");

            return false;
        }
        return true;
    }

    public ObservableList<AnnotatedWord> rebuildObservableList(ArrayList<AnnotatedWord> records) {
        ObservableList<AnnotatedWord> ol = FXCollections.observableArrayList();
        for (AnnotatedWord m : records) {
            ol.add(new AnnotatedWord(
                    m.getWord(),
                    m.getTag()
            ));
        }
        return ol;
    }

    public void setMenuBarInteractive(boolean enable) {
        menuBar.setDisable(!enable);
    }

    private void exit() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_MENU));
        Parent root = (Parent) loader.load();

        MainMenuController controller = (MainMenuController) loader.getController();

        controller.setStage(this.stage);

        this.stage.setTitle("Меню");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());
        this.stage.setScene(scene);
        this.stage.show();
    }

    public void exitListener(ActionEvent e) throws IOException {

        if (createNew) {
            if (!ActionDialogs.showAlertDialog(
                    Alert.AlertType.CONFIRMATION,
                    "Выход",
                    "Вы уверены, что хотите выйти?",
                    "Анноторованный текст не будет сохранен!")
            ) return;
        }

        exit();
    }

    public void saveListener(ActionEvent actionEvent) throws IOException {

        if (createNew) {
            String savedName = annotatorBuilder.addAnnotatedFile(annotatedTextToFind, annotatedWords);

            ActionDialogs.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Текст сохранен",
                    "Текст сохранен под именем " + savedName + ".",
                    "");
        }

        exit();

    }

    public void showTagInfoListener(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SHOW_TAGS));
        root = (Parent) loader.load();
        ShowTagsController controller = (ShowTagsController) loader.getController();
        scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        Stage st = new Stage();
        st.getIcons().add(
                new Image(EditAnnotatedTextController.class.getResourceAsStream(ICON)));

        st.setTitle("Список тегов");
        st.setScene(scene);
        st.show();
    }

    public void changeWordListener(ActionEvent actionEvent) throws IOException {

        int id = tableM.getSelectionModel().getSelectedIndex();
        if (!checkSelection(id))
            return;


        FXMLLoader loader = new FXMLLoader(getClass().getResource(CHANGE_WORD));
        root = (Parent) loader.load();
        ChangeWordController controller = (ChangeWordController) loader.getController();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        Stage st = new Stage();
        st.getIcons().add(new Image(EditAnnotatedTextController.class.getResourceAsStream(ICON)));

        controller.setStage(st);
        controller.setIdRecordToChange(id);
        controller.setRecordToChange(annotatedWords.get(id));

        controller.setParentController(this);
        controller.setStage(st);

        st.setTitle("Change word");
        st.setScene(scene);
        st.show();
    }

    public void addWord(AnnotatedWord newWord, int id) {
        if (newWord != null) {
            annotatedWords.get(id).setWord(newWord.getWord());
            annotatedWords.get(id).setTag(newWord.getTag());

            refreshTable();
        }
    }
}
