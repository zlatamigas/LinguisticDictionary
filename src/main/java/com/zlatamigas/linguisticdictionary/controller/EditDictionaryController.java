package com.zlatamigas.linguisticdictionary.controller;

import com.zlatamigas.linguisticdictionary.model.builder.AnnotatorBuilder;
import com.zlatamigas.linguisticdictionary.model.builder.DictionaryBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.zlatamigas.linguisticdictionary.model.entity.*;
import com.zlatamigas.linguisticdictionary.service.*;
import com.zlatamigas.linguisticdictionary.util.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.zlatamigas.linguisticdictionary.controller.navigation.PageNavigation.*;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.ICON;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.STYLE_FILE;

/**
 * Controller for EditDictionary.fxml.
 */
public class EditDictionaryController implements Initializable {

    public MenuBar menuBar;
    public MenuItem miSave;
    public MenuItem miExit;
    public MenuItem miSortWordA;
    public MenuItem miSortWordD;
    public MenuItem miSortFreqA;
    public MenuItem miSortFreqD;
    public MenuItem miFind;
    public MenuItem miTags;
    public MenuItem miAddAnnotatedText;
    public TableView tableWord;
    public TableView tableTags;
    public Button btAddTag;
    public Button btDelTag;
    public TableColumn tcWord;
    public TableColumn tcFullFreq;
    public TableColumn tcTag;
    public TableColumn tcFreq;
    public Button btAddWord;
    public Button btDelWord;
    public TextField tfWordLemma;
    public TextArea taTagsLemma;
    public MenuItem miAddDictionaryPart;
    public MenuItem miAnnTexts;

    private MainMenuController parentController;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private FileChooser fileChooser;


    public String SORT_TYPES[];
    private int sortedType = 1;

    private ObservableList<TagsTableInfo> tagsInTable;
    private ObservableList<WordTableInfo> wordsInTable;

    private ArrayList<EditDictShowInfo> tablesInfo;
    private ArrayList<WordTableInfo> wordsInTablesInfo;
    private ArrayList<WordTableInfo> showWordsInTablesInfo;

    private static DictionaryBuilder dictionaryBuilder = new DictionaryBuilder();
    private static AnnotatorBuilder annotatorBuilder = new AnnotatorBuilder();

    private Dictionary dictionary;
    private boolean createNew;
    private String dictionaryName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SORT_TYPES = new String[]{
                miSortWordA.getId(),
                miSortWordD.getId(),
                miSortFreqA.getId(),
                miSortFreqD.getId()
        };

        tableWord.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                WordTableInfo wordChosen = (WordTableInfo) tableWord.getSelectionModel().getSelectedItem();
                if (wordChosen != null) {
                    fillWordData(wordChosen.getWord());
                }
            }
        });

        tablesInfo = new ArrayList<>();
        wordsInTablesInfo = new ArrayList<>();
        showWordsInTablesInfo = new ArrayList<>();

        tcWord.setCellValueFactory(new PropertyValueFactory<>("Word"));
        tcWord.setSortable(false);
        tcFullFreq.setCellValueFactory(new PropertyValueFactory<>("FullFrequency"));
        tcFullFreq.setSortable(false);
        tcTag.setCellValueFactory(new PropertyValueFactory<>("Tag"));
        tcTag.setSortable(false);
        tcFreq.setCellValueFactory(new PropertyValueFactory<>("Frequency"));
        tcFreq.setSortable(false);

        wordsInTable = FXCollections.observableArrayList();
        tagsInTable = FXCollections.observableArrayList();

        tableWord.setItems(wordsInTable);
        tableTags.setItems(tagsInTable);

        tableWord.setPlaceholder(new Label("Подходящие данные отсутствуют"));
        tableTags.setPlaceholder(new Label("Подходящие данные отсутствуют"));

        fileChooser = new FileChooser();
    }

    public ArrayList<WordTableInfo> getWordsInTablesInfo() {
        return wordsInTablesInfo;
    }

    public void setParentController(MainMenuController parentController) {
        this.parentController = parentController;
    }

    public void setCreateNew(boolean createNew) {
        this.createNew = createNew;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    private void fillWordData(String word) {

        int id = tablesInfo.indexOf(new EditDictShowInfo(new WordTableInfo(word, 0)));
        if (id == -1)
            return;

        tagsInTable.setAll(tablesInfo.get(id).getWordTags());
        tfWordLemma.setText(tablesInfo.get(id).getLemma());

        StringBuffer sb = new StringBuffer();
        for (String lt : tablesInfo.get(id).getLemmaTags())
            sb.append("{").append(lt).append("}\n");

        taTagsLemma.setText(sb.toString());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;

        if (dictionary.getRecords().size() == 0)
            return;

        refreshTable();
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void refreshTable() {

        tablesInfo = TableInfoManager.getTableInfo(dictionary);
        wordsInTablesInfo.clear();
        showWordsInTablesInfo.clear();

        for (EditDictShowInfo edi : tablesInfo)
            wordsInTablesInfo.add(edi.getWord());

        Sorting.sort(wordsInTablesInfo, sortedType);

        showWordsInTablesInfo = wordsInTablesInfo;
        wordsInTable.setAll(showWordsInTablesInfo);
    }

    public void refreshTable(ArrayList<WordTableInfo> showWords) {

        tablesInfo = TableInfoManager.getTableInfo(dictionary);

        wordsInTablesInfo.clear();

        int i = -1;
        for (EditDictShowInfo edi : tablesInfo) {
            wordsInTablesInfo.add(edi.getWord());
            i = showWords.indexOf(edi.getWord());
            if (i != -1)
                showWords.get(i).setFullFrequency(edi.getWord().getFullFrequency());
        }

        Sorting.sort(wordsInTablesInfo, sortedType);
        Sorting.sort(showWords, sortedType);

        showWordsInTablesInfo = showWords;
        wordsInTable.setAll(showWordsInTablesInfo);
    }


    private void exit() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_MENU));
        Parent root = (Parent) loader.load();

        MainMenuController controller = (MainMenuController) loader.getController();

        controller.setStage(this.stage);

        this.stage.setTitle("Меню");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        stage.setScene(scene);
        this.stage.show();
    }

    public void saveListener(ActionEvent actionEvent) throws IOException {

        if (createNew)
            dictionaryBuilder.saveNewDictionary(dictionaryName, dictionary);
        else
            dictionaryBuilder.saveChangedDictionary(dictionaryName, dictionary);

        exit();
    }

    public void exitListener(ActionEvent e) throws IOException {

        if (ActionDialogs.showAlertDialog(
                Alert.AlertType.CONFIRMATION,
                "Выход",
                "Вы уверены, что хотите выйти?",
                "Внесенные изменения не будут сохранены!")
        )
            exit();
    }


    public void sortTable(int sortTypeId) {
        if (sortTypeId < 1 || sortTypeId > 4)
            return;

        sortedType = sortTypeId;
    }

    public void sortDictionaryListener(ActionEvent e) {

        for (int i = 0; i < SORT_TYPES.length; ++i) {
            if (((MenuItem) e.getSource()).getId().equalsIgnoreCase(SORT_TYPES[i])) {
                sortTable(i + 1);
                refreshTable(showWordsInTablesInfo);
                break;
            }
        }
    }

    public void findInDictionaryListener(ActionEvent e) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FIND_WORD));
        root = (Parent) loader.load();
        FindWordController controller = (FindWordController) loader.getController();
        scene = new Scene(root);
        Stage st = new Stage();
        st.getIcons().add(
                new Image(EditDictionaryController.class.getResourceAsStream(ICON)));


        controller.setParentController(this);
        controller.setStage(st);

        st.setTitle("Найти слово");
        st.setScene(scene);
        st.show();
    }


    public void addTagListener(ActionEvent actionEvent) throws IOException {

        WordTableInfo word = (WordTableInfo) tableWord.getSelectionModel().getSelectedItem();
        if (word == null) {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Ошибка",
                    "Слово не выбрано!",
                    "Выберете объект из предложенных в таблице или, если\nтаблица пуста, " +
                            "предварительно добавьте данные в таблицу.");
            return;
        }


        String tag = ActionDialogs.selectItemFromListDialog("Добавить тег:", "Теги",
                new ArrayList<>(Arrays.asList(TagsInfo.getTagNames())));

        if (tag == null)
            return;

        dictionary.addWord(word.getWord(), tag, 0);
        refreshTable(showWordsInTablesInfo);
    }

    public void deleteTagListener(ActionEvent actionEvent) {

        WordTableInfo word = (WordTableInfo) tableWord.getSelectionModel().getSelectedItem();
        TagsTableInfo tag = (TagsTableInfo) tableTags.getSelectionModel().getSelectedItem();

        if (word == null || tag == null) {

            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Ошибка",
                    "Комбинация \"слово-тег\" не выбрана!",
                    "Выберете соответствующие слово и его тег из предложенных в таблицах или, если таблицы пусты, " +
                            "предварительно добавьте данные в таблицу.");
            return;

        }

        dictionary.removeTagFromWord(word.getWord(), tag.getTag());
        refreshTable(showWordsInTablesInfo);

    }

    public void addRecordListener(ActionEvent actionEvent) throws IOException {

        String s = "";

        TextInputDialog dialog = new TextInputDialog("");

        dialog.setTitle("Создать слово");
        dialog.setHeaderText("Новое слово:");
        dialog.setContentText("");
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image(EditDictionaryController.class.getResourceAsStream(ICON)));

        ButtonType ok = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            s = result.get();
        }

        if (s.equals("")){
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Некорректное слово!",
                    "Слово не может быть пустым!",
                    "");
            return;
        }

        if (!dictionary.containsWord(s)) {

            dictionary.addWord(s, "", 0);
            String lemma = Dictionary.getLemma(s);
            if (!lemma.equalsIgnoreCase(s)) {
                showWordsInTablesInfo.add(new WordTableInfo(lemma, 0));
            }

            showWordsInTablesInfo.add(new WordTableInfo(s, 0));
            refreshTable(showWordsInTablesInfo);
        }
        else {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Добавление слова",
                    "Слово было ранее добавлено в словарь!",
                    "");
        }
    }

    public void deleteRecordListener(ActionEvent e) {

        WordTableInfo word = (WordTableInfo) tableWord.getSelectionModel().getSelectedItem();

        if (word == null) {

            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Ошибка",
                    "Слово не выбрано!",
                    "Выберете объект из предложенных в таблице или, если\nтаблица пуста, " +
                            "предварительно добавьте данные в таблицу.");
            return;

        }
        if (dictionary.removeWord(word.getWord())) {

            showWordsInTablesInfo.remove(new WordTableInfo(word.getWord(), 0));
            refreshTable(showWordsInTablesInfo);
        } else {
            int id = dictionary.getRecords().indexOf(new DictionaryRecord(word.getWord()));
            int count = dictionary.getRecords().get(id).getRelativeWords().size();
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Удалить слово",
                    "Слово не было удалено!",
                    "Слово является основой других слов: "+ count + "!");

        }
    }


    public void showTagInfoListener(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SHOW_TAGS));
        root = (Parent) loader.load();
        ShowTagsController controller = (ShowTagsController) loader.getController();

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        Stage st = new Stage();
        st.getIcons().add(
                new Image(EditDictionaryController.class.getResourceAsStream(ICON)));


        st.setTitle("Список тегов");
        st.setScene(scene);
        st.show();
    }


    public void addAnnotatedText(ActionEvent actionEvent) throws IOException {

        ArrayList<String> fileNames = new ArrayList<>();
        String fileName = ActionDialogs.selectItemFromListDialog("Выберите текст:",
                "Добавить текст", annotatorBuilder.getRealTimeAnnotatedIndex());

        if (fileName == null)
            return;


        fileNames.add(fileName);
        int added = dictionaryBuilder.addAnnotatedTextToDictionary(dictionary, fileNames);

        if (added >= 1) {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Добавление текста в словарь",
                    "Текст " + fileName + " успешно добавлен!",
                    "");
        } else {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Добавление текста в словарь",
                    "Текст " + fileName + " был ранее добавлен в словарь!",
                    "");
        }
        refreshTable();
    }

    public void addDictionaryPart(ActionEvent actionEvent) throws IOException {

        String fileName = ActionDialogs.selectItemFromListDialog("Словарь для добавления:",
                "Добавить словарь", dictionaryBuilder.getRealTimeDictionaryIndex());

        if (fileName == null)
            return;

        if (dictionaryBuilder.addDictionaryToDictionary(dictionary, fileName)) {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.INFORMATION,
                    "Добавить тексты словаря",
                    "Тексты словаря " + fileName + " успешно добавлены!",
                    "");
            refreshTable();
        } else {
            ActionDialogs.showAlertDialog(
                    Alert.AlertType.WARNING,
                    "Добавить тексты словаря",
                    "Не удалось слить словари!",
                    "Невозможно слияние словарей с общими текстами!");
        }
    }

    public void showAnnTextsInfoListener(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SHOW_LIST));
        root = (Parent) loader.load();
        ShowListController controller = (ShowListController) loader.getController();

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        Stage st = new Stage();

        controller.setLabelText("Аннотированные тексты в словаре");
        controller.setListData(dictionary.getAnnotatedTextsToUse());

        st.setTitle("Тексты");
        st.setScene(scene);
        st.show();
    }
}
