package org.sergei.qrgen;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowManager {
    private double xOffset = 0;
    private double yOffset = 0;

    public void prepareStage(Stage stage, Scene scene, BorderPane root) {
        stage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        stage.setScene(scene);

        stage.setResizable(false);
        stage.setMinWidth(AppSettings.WINDOW_WIDTH);
        stage.setMaxWidth(AppSettings.WINDOW_WIDTH);
        stage.setMinHeight(AppSettings.WINDOW_HEIGHT);
        stage.setMaxHeight(AppSettings.WINDOW_HEIGHT);
        stage.setAlwaysOnTop(true);

        stage.setTitle("QRGeneratorV2");
    }
}
