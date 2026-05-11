package org.sergei.qrgen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(false);

        BorderPane root = new BorderPane();
        TitleBar titleBar = new TitleBar(primaryStage);
        root.setTop(titleBar);
        MainView mainView = new MainView();
        root.setCenter(mainView);

        Scene scene = new Scene(root, AppSettings.WINDOW_WIDTH, AppSettings.WINDOW_HEIGHT);

        var styleResource = getClass().getResource("/style.css");
        if (styleResource != null) {
            scene.getStylesheets().add(styleResource.toExternalForm());
        } else {
            System.err.println("ВНИМАНИЕ: style.css не найден в resources!");
        }

        WindowManager windowManager = new WindowManager();
        windowManager.prepareStage(primaryStage, scene, root);

        TrayManager trayManager = new TrayManager(primaryStage);
        trayManager.setupTray();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
