package com.zlatamigas.linguisticdictionary;

import com.zlatamigas.linguisticdictionary.controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static com.zlatamigas.linguisticdictionary.controller.navigation.PageNavigation.MAIN_MENU;
import static com.zlatamigas.linguisticdictionary.controller.navigation.ResourceNavigation.*;

public class Main extends Application {

    private static String APP_NAME = "Меню";

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_MENU));
        Parent root = (Parent) loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(STYLE_FILE).toExternalForm());

        MainMenuController controller = (MainMenuController) loader.getController();
        controller.setStage(primaryStage);

        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream(ICON)));
        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
