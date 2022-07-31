package com.zlatamigas.linguisticdictionary.controller;

import com.zlatamigas.linguisticdictionary.model.builder.AnnotatorBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.zlatamigas.linguisticdictionary.model.entity.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static com.zlatamigas.linguisticdictionary.controller.navigation.PageNavigation.*;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.ICON;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.STYLE_FILE;

public class StatisticsController implements Initializable {

    public TableView tvTable;
    public TableColumn tcValue1;
    public TableColumn tcValue2;
    public TableColumn tcFreq;
    public ComboBox cbTableType;


    private ArrayList<TextTagsStatisticsTableInfo> codesTextTagsStatisticsTableInfos;
    private ArrayList<TextTagsStatisticsTableInfo> wordCodeTextTagsStatisticsTableInfos;
    private ArrayList<TextTagsStatisticsTableInfo> codeCodeTextTagsStatisticsTableInfos;

    private ArrayList<String> usedTexts;

    private ObservableList<TextTagsStatisticsTableInfo> tableData;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private ArrayList<String> optionNames
            = new ArrayList<>(Arrays.asList(
            "код",
            "слово-код",
            "код-код"));
    private ObservableList<String> options;
    private int tableMode = 0;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setUsedTexts(ArrayList<String> usedTexts) {
        this.usedTexts = usedTexts;

        AnnotatorBuilder annotatorBuilder = new AnnotatorBuilder();
        ArrayList<TextTagsStatisticsTableInfo>[] data = annotatorBuilder.readAnnotatedFileTextsTagsStatistics(usedTexts);

        codesTextTagsStatisticsTableInfos = data[0];
        wordCodeTextTagsStatisticsTableInfos = data[1];
        codeCodeTextTagsStatisticsTableInfos = data[2];

        cbTableType.getSelectionModel().selectFirst();
    }

    public ArrayList<String> getUsedTexts() {
        return usedTexts;
    }



    private void changedTableMode(){

        switch (tableMode) {
            case 0:
                tableData.setAll(codesTextTagsStatisticsTableInfos);
                break;
            case 1:
                tableData.setAll(wordCodeTextTagsStatisticsTableInfos);
                break;
            case 2:
                tableData.setAll(codeCodeTextTagsStatisticsTableInfos);
                break;
            default:
                tableData.setAll(new ArrayList<>());
        }

        tvTable.setItems(tableData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        options = FXCollections.observableArrayList(optionNames);
        tableData = FXCollections.observableArrayList();

        cbTableType.setItems(options);
        cbTableType.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String oldVal, String newVal) {

                for(int i = 0; i < optionNames.size(); ++i)
                    if(newVal.equals(optionNames.get(i))){
                        tableMode = i;
                        changedTableMode();
                        break;
                    }
            }
        });

        tcValue1.setCellValueFactory(new PropertyValueFactory<>("Value1"));
        tcValue2.setCellValueFactory(new PropertyValueFactory<>("Value2"));
        tcFreq.setCellValueFactory(new PropertyValueFactory<>("Freq"));

        tvTable.setItems(tableData);

        codesTextTagsStatisticsTableInfos = new ArrayList<>();
        wordCodeTextTagsStatisticsTableInfos = new ArrayList<>();
        codeCodeTextTagsStatisticsTableInfos = new ArrayList<>();
    }

    private void exit(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_MENU));
        Parent root = (Parent) loader.load();

        MainMenuController controller = (MainMenuController) loader.getController();

        controller.setStage(stage);

        stage.setTitle("Меню");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public void exitListener(ActionEvent e) throws IOException {
        exit(e);
    }

    public void showTagInfoListener(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SHOW_TAGS));
        Parent root = (Parent) loader.load();
        ShowTagsController controller = (ShowTagsController) loader.getController();
        Scene scene = new Scene(root);
        Stage st = new Stage();

        st.setTitle("Список тегов");
        st.setScene(scene);
        st.show();
    }


    public void showTextsListener(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SHOW_LIST));
        root = (Parent) loader.load();
        ShowListController controller = (ShowListController) loader.getController();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        Stage st = new Stage();
        st.getIcons().add(
                new Image(StatisticsController.class.getResourceAsStream(ICON)));


        controller.setLabelText("Аннотированные тексты в словаре");
        controller.setListData(usedTexts);

        st.setTitle("Тексты");
        st.setScene(scene);
        st.show();
    }
}
