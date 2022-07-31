package com.zlatamigas.linguisticdictionary.util;

import com.zlatamigas.linguisticdictionary.controller.ShowListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.zlatamigas.linguisticdictionary.controller.navigation.PageNavigation.SHOW_LIST;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.ICON;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.STYLE_FILE;

public class ActionDialogs {

    private static final String OK_BUTTON = "ОК";
    private static final String CANCEL_BUTTON = "Отмена";

    private static final Image icon;
    private static final String stylesheet;

    static {
        icon = new Image(ActionDialogs.class.getResourceAsStream(ICON));
        stylesheet = ActionDialogs.class.getResource(STYLE_FILE).toExternalForm();
    }

    public static String selectItemFromListDialog(String labelText, String titleText, List listData) throws IOException {

        FXMLLoader loader = new FXMLLoader(ActionDialogs.class.getResource(SHOW_LIST));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(stylesheet);

        Stage stage = new Stage();
        stage.getIcons().add(icon);

        ShowListController controller = (ShowListController) loader.getController();
        controller.setLabelText(labelText);
        controller.setListData(listData);

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle(titleText);
        stage.setScene(scene);
        stage.showAndWait();

        String s = controller.getSelectedItem();

        return controller.getIsConfirmed() ? s : null;
    }

    public static boolean showAlertDialog(Alert.AlertType alertType, String title, String header, String content) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(icon);

        if (alertType == Alert.AlertType.CONFIRMATION) {

            ButtonType ok = new ButtonType(OK_BUTTON);
            ButtonType cancel = new ButtonType(CANCEL_BUTTON);

            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(ok, cancel);

            Optional<ButtonType> option = alert.showAndWait();

            return option.get() == ok;
        } else {
            alert.show();
            return false;
        }
    }
}
